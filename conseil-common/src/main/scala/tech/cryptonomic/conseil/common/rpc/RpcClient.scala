package tech.cryptonomic.conseil.common.rpc

import scala.util.control.NoStackTrace

import cats.effect.{Concurrent, Resource}
import com.typesafe.scalalogging.LazyLogging
import fs2.Stream
import org.http4s.{EntityDecoder, EntityEncoder, Header, Method, Uri}
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl

import tech.cryptonomic.conseil.common.rpc.RpcClient._

/**
  * JSON-RPC client according to the specification at https://www.jsonrpc.org/specification
  *
  * Usage example:
  *
  * {{{
  *   import scala.concurrent.ExecutionContext
  *   import io.circe.generic.auto._
  *   import org.http4s.circe.CirceEntityDecoder._
  *   import org.http4s.circe.CirceEntityEncoder._
  * 
  *   implicit val contextShift = IO.contextShift(ExecutionContext.global)
  *   
  *   // Define case class for JSON-RPC method and response
  *   case class ApiMethodParams(id: Int)
  *   case class ApiMethodResponse(name: String)
  *
  *   // Create stream with the request
  *   val request = Stream(
  *     RpcRequest(
  *       jsonrpc = "2.0",
  *       mathod = "apiMethod",
  *       params = ApiMethodParams(id = 1)
  *       id = "id_of_the_request"
  *     )
  *   )
  *
  *   // Create JSON-RPC client
  *   val client = new RpcClient[IO]("endpoint", 10, httpClient)
  *
  *   // Call api method with
  *   val response: Stream[IO, ApiMethodResponse] =
  *     client.stream[ApiMethodParams, ApiMethodResponse](batchSize = 10)(request)
  * }}}
  *
  * @param endpoint JSON-RPC server url
  * @param maxConcurrent Max concurrent processes used to fetch data from JSON-RPC server
  * @param httpClient Http4s http client
  * @param headers Additional http headers, e.g. authorization
  */
class RpcClient[F[_]: Concurrent](
    endpoint: String,
    maxConcurrent: Int,
    httpClient: Client[F],
    headers: Header*
) extends Http4sClientDsl[F]
    with LazyLogging {

  /**
    * Method that converts [[fs2.Stream]] of JSON-RPC requests into [[fs2.Stream]]] of desired result objects,
    * or throws the exception with the error. Request [[fs2.Stream]]] can only contain calls to one API method
    * and can be batched to speed up the response time.
    *
    * @param batchSize The size of the batched request in single HTTP call
    * @param request [[fs2.Stream]]] of the [[RpcRequest]]
    *
    * Returns a [[fs2.Stream]]] that contains result from JSON-RPC server call.
    */
  def stream[P, R](batchSize: Int)(request: Stream[F, RpcRequest[P]])(
      implicit decode: EntityDecoder[F, Seq[RpcResponse[R]]],
      encode: EntityEncoder[F, List[RpcRequest[P]]]
  ): Stream[F, R] =
    request
      .chunkN(batchSize)
      .evalTap { requests =>
        Concurrent[F].delay(logger.debug(s"Call RPC in a batch of: ${requests.size}"))
      }
      .mapAsyncUnordered(maxConcurrent) { chunks =>
        httpClient
          .expect[Seq[RpcResponse[R]]](
            Method.POST(chunks.toList, Uri.unsafeFromString(endpoint), headers: _*)
          )
      }
      .flatMap(Stream.emits)
      .flatMap {
        case RpcResponse(_, _, Some(error), _) =>
          Stream.raiseError[F](error)
        case RpcResponse(_, Some(result), _, _) =>
          Stream(result)
        case response =>
          Stream.raiseError[F](
            new UnsupportedOperationException(
              s"Unexpected response from JSON-RPC server at $endpoint $response"
            )
          )
      }

}

object RpcClient {

  /**
    * Create [[cats.Resource]] with [[RpcClient]].
    *
    * @param endpoint JSON-RPC server url
    * @param maxConcurrent Max concurrent processes used to fetch data from JSON-RPC server
    * @param httpClient Http4s http client
    * @param headers Additional http headers, e.g. authorization
    */
  def resource[F[_]: Concurrent](
      endpoint: String,
      maxConcurrent: Int,
      httpClient: Client[F],
      headers: Header*
  ): Resource[F, RpcClient[F]] =
    for {
      client <- Resource.pure(
        new RpcClient[F](
          endpoint,
          maxConcurrent,
          httpClient,
          headers: _*
        )
      )
    } yield client

  /**
    * Exception with JSON-RPC error message
    * 
    * Defined Error Codes:
    * -32700 ---> parse error. not well formed
    * -32701 ---> parse error. unsupported encoding
    * -32702 ---> parse error. invalid character for encoding
    * -32600 ---> server error. invalid xml-rpc. not conforming to spec.
    * -32601 ---> server error. requested method not found
    * -32602 ---> server error. invalid method parameters
    * -32603 ---> server error. internal xml-rpc error
    * -32500 ---> application error
    * -32400 ---> system error
    * -32300 ---> transport error
    * 
    * In addition, the range -32099 .. -32000, inclusive is reserved for implementation defined server errors. 
    */
  case class RpcException(
      code: Int,
      message: String,
      data: Option[String]
  ) extends RuntimeException(s"Json-RPC error $code: $message ${data.getOrElse("")}")
      with NoStackTrace

  case class RpcRequest[P](
      jsonrpc: String,
      method: String,
      params: P,
      id: String
  )

  case class RpcResponse[R](
      jsonrpc: Option[String],
      result: Option[R],
      error: Option[RpcException],
      id: String
  )
}

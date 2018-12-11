package tech.cryptonomic.conseil.generic.chain

import tech.cryptonomic.conseil.generic.chain.QueryProtocolTypes.Query

import scala.concurrent.{ExecutionContext, Future}

/**
  * Trait containing the interface for the QueryProtocol
  */
trait QueryProtocolOperations {
  /** Interface method for querying with given predicates
    *
    * @param  tableName name of the table which we query
    * @param  query     query predicates and fields
    * @return query result as a map
    * */
  def queryWithPredicates(tableName: String, query: Query)(implicit ec: ExecutionContext): Future[List[Map[String, Any]]]
}

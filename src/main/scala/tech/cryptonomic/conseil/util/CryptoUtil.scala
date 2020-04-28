package tech.cryptonomic.conseil.util

import fr.acinq.bitcoin.Base58Check
import scorex.util.encode.{Base16 => Hex}

import scala.util.Try

/**
  * Utility function for common cryptographic operations.
  */
object CryptoUtil {

  final case class KeyStore(publicKey: String, privateKey: String, publicKeyHash: String)

  /** Get byte prefix for Base58Check encoding and decoding of a given type of data.
    * @param prefix The type of data
    * @return       Byte prefix
    */
  private def getBase58BytesForPrefix(prefix: String): Try[List[Byte]] = Try {
    prefix.toLowerCase match {
      case "tz1" => List(6, 161, 159).map(_.toByte)
      case "tz2" => List(6, 161, 161).map(_.toByte)
      case "tz3" => List(6, 161, 164).map(_.toByte)
      case "kt1" => List(2, 90, 121).map(_.toByte)
      case "edpk" => List(13, 15, 37, 217).map(_.toByte)
      case "edsk" => List(43, 246, 78, 7).map(_.toByte)
      case "edsig" => List(9, 245, 205, 134, 18).map(_.toByte)
      case "op" => List(5, 116).map(_.toByte)
      case "expr" => List(13, 44, 64, 27).map(_.toByte)
      case _ => throw new Exception(s"Could not find prefix for $prefix!")
    }
  }

  /** Base58Check encodes a given binary payload using a given prefix.
    * @param payload  Binary payload
    * @param prefix   Prefix
    * @return         Encoded string
    */
  def base58CheckEncode(payload: Seq[Byte], prefix: String): Try[String] =
    getBase58BytesForPrefix(prefix).map { prefix =>
      Base58Check.encode(prefix, payload)
    }

  /** Base58Check decodes a given binary payload using a given prefix.
    * @param s      Base58Check-encoded string
    * @param prefix Prefix
    * @return       Decoded bytes
    */
  def base58CheckDecode(s: String, prefix: String): Try[Seq[Byte]] =
    getBase58BytesForPrefix(prefix).map { prefix =>
      val charsToSlice = prefix.length
      val (first, rest) = Base58Check.decode(s)
      val decodedBytes = first :: rest.toList
      decodedBytes.drop(charsToSlice)
    }

  /** Decodes the account b58-check address as an hexadecimal bytestring */
  def packAddress(b58Address: String): Try[String] = {

    def dataLength(num: Long) = ("0000000" + num.toHexString).takeRight(8)

    def wrap(hexString: String): String =
      b58Address.take(3).toLowerCase match {
        case "tz1" => "0000" + hexString
        case "tz2" => "0001" + hexString
        case "tz3" => "0002" + hexString
        case "kt1" => "01" + hexString + "00"
      }

    //what if the wrapped length is odd?
    base58CheckDecode(b58Address, b58Address.take(3).toLowerCase).map { bytes =>
      val wrapped = wrap(Hex.encode(bytes.toArray))
      s"050a${dataLength(wrapped.length / 2)}$wrapped"
    }
  }
}
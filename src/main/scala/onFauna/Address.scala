package onFauna

import faunadb.FaunaClient
import faunadb.values.Codec
import grizzled.slf4j.Logging
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import FaunaUtils.{RefField, TermField, createClass}

import scala.concurrent.{ExecutionContext, Future}


case class Address(street: String, city: String, region: String,
                   country: String, phone: Option[String])

object Address extends Logging {
  implicit val addressCodec: Codec[Address] = Codec.caseClass[Address]

  implicit val addressDecoder: Decoder[Address] = deriveDecoder[Address]
  implicit val addressEncoder: Encoder[Address] = deriveEncoder[Address]

  var CLASS_NAME = "address"
  var INDEX_NAME = "address_by_country"
  val termFields = Seq(TermField("country"))
  val valueFields = Seq(RefField())

  def createSchema(implicit client: FaunaClient, ec: ExecutionContext): Future[Unit] = {
    logger.info(s"starting $CLASS_NAME create schema")

    for {
      createClassResult <- createClass(CLASS_NAME)
    } yield {
      logger.info(s"Created $CLASS_NAME class")
    }
  }
}
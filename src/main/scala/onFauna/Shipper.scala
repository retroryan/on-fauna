package onFauna

import faunadb.FaunaClient
import faunadb.values.Codec
import grizzled.slf4j.Logging
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import onFauna.FaunaUtils.{RefField, TermField, createClass, createIndex}

import scala.concurrent.{ExecutionContext, Future}


case class Shipper(shipperID:Int,companyName:String, phone:String)

object Shipper extends Logging {
  implicit val shipperCodec: Codec[Shipper] = Codec.caseClass[Shipper]

  implicit val shipperDecoder: Decoder[Shipper] = deriveDecoder[Shipper]
  implicit val shipperEncoder: Encoder[Shipper] = deriveEncoder[Shipper]

  var CLASS_NAME = "shipper"
  var INDEX_NAME = "shipper_by_id"
  val termFields = Seq(TermField("shipperID"))
  val valueFields = Seq(RefField())

  def createSchema(implicit client: FaunaClient, ec: ExecutionContext): Future[Unit] = {
    logger.info(s"starting $CLASS_NAME create schema")

    for {
      createClassResult <- createClass(CLASS_NAME)
      createIndexResult <- createIndex(INDEX_NAME, CLASS_NAME, termFields, valueFields)
    } yield {
      logger.info(s"Created $CLASS_NAME class")
      logger.info(s"Created $INDEX_NAME index: ${JsonUtil.toJson(createIndexResult)}")
    }
  }
}

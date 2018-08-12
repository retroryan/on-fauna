package onFauna

import faunadb.FaunaClient
import faunadb.values.Codec
import grizzled.slf4j.Logging
import io.circe._
import io.circe.generic.semiauto._
import onFauna.FaunaUtils.{RefField, TermField, createClass, createIndex, createClassIndex}

import scala.concurrent.{ExecutionContext, Future}

case class Customer(customerID:String,companyName:String, contactName:String,contactTitle:String, address:Address)

object Customer extends Logging {

  implicit val customerCodec: Codec[Customer] = Codec.caseClass[Customer]

  implicit val customerDecoder: Decoder[Customer] = deriveDecoder[Customer]
  implicit val customerEncoder: Encoder[Customer] = deriveEncoder[Customer]

  var CLASS_NAME = "customer"
  var INDEX_NAME = "customer_by_id"
  val termFields = Seq(TermField("customerID"))
  val valueFields = Seq(RefField())

  def createSchema(implicit client: FaunaClient, ec: ExecutionContext): Future[Unit] = {
    logger.info(s"starting $CLASS_NAME create schema")

    for {
      createClassResult <- createClass(CLASS_NAME)
      createIndexResult <- createIndex(INDEX_NAME, CLASS_NAME, termFields, valueFields)
      createClassIndexResult <- createClassIndex(CLASS_NAME)
    } yield {
      logger.info(s"Created $CLASS_NAME class")
      logger.info(s"Created $INDEX_NAME index: ${JsonUtil.toJson(createIndexResult)}")
      logger.info(s"Created $INDEX_NAME index: ${JsonUtil.toJson(createClassIndexResult)}")
    }
  }
}

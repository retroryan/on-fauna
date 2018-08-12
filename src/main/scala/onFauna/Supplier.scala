package onFauna


import faunadb.FaunaClient
import faunadb.values.Codec
import grizzled.slf4j.Logging
import io.circe._
import io.circe.generic.semiauto._
import onFauna.FaunaUtils.{RefField, TermField, createClass, createIndex}

import scala.concurrent.{ExecutionContext, Future}

case class Supplier(supplierID: Int, companyName: String, contactName: String, contactTitle: String, address: Address)

object Supplier extends Logging {

  implicit val supplierCodec: Codec[Supplier] = Codec.caseClass[Supplier]

  implicit val supplierDecoder: Decoder[Supplier] = deriveDecoder[Supplier]
  implicit val supplierEncoder: Encoder[Supplier] = deriveEncoder[Supplier]

  var CLASS_NAME = "supplier"
  var INDEX_NAME = "supplier_by_id"
  val termFields = Seq(TermField("supplierID"))
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

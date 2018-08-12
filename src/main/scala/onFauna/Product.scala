package onFauna

import faunadb.FaunaClient
import faunadb.values.Codec
import grizzled.slf4j.Logging
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import ledger.JsonUtil
import onFauna.FaunaUtils.{RefField, TermField, createClass, createIndex}

import scala.concurrent.{ExecutionContext, Future}

case class Product(productID: Int, supplierID: Int, categoryID: Int, quantityPerUnit: String,
                   unitPrice: Double, unitsInStock: Int, unitsOnOrder: Int, reorderLevel: Int, discontinued: Boolean,
                   name: String)

object Product extends Logging {

  implicit val productCodec: Codec[Product] = Codec.caseClass[Product]

  implicit val productDecoder: Decoder[Product] = deriveDecoder[Product]
  implicit val productEncoder: Encoder[Product] = deriveEncoder[Product]

  var CLASS_NAME = "product"
  var INDEX_NAME = "product_by_id"
  val termFields = Seq(TermField("productID"))
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
package onFauna

import faunadb.FaunaClient
import faunadb.values.Codec
import grizzled.slf4j.Logging
import io.circe._
import io.circe.generic.semiauto._
import ledger.JsonUtil
import onFauna.FaunaUtils.{RefField, TermField, createClass, createIndex}

import scala.concurrent.{ExecutionContext, Future}

case class Order(orderID: Int, customerID: String, employeeID: Int, orderDate: String,
                 requiredDate: String, shippedDate: String, shipVia: Int,
                 freight: Double, shipName: String, shipAddress: Address, details: Seq[OrderDetails])

object Order extends Logging {

  implicit val orderCodec: Codec[Order] = Codec.caseClass[Order]

  implicit val orderDecoder: Decoder[Order] = deriveDecoder[Order]
  implicit val orderEncoder: Encoder[Order] = deriveEncoder[Order]

  var CLASS_NAME = "order"
  var INDEX_NAME = "order_by_id"
  val termFields = Seq(TermField("orderID"))
  val valueFields = Seq(RefField())

  val customerTermFields = Seq(TermField("customerID"))

  def createSchema(implicit client: FaunaClient, ec: ExecutionContext): Future[Unit] = {
    logger.info(s"starting $CLASS_NAME create schema")

    for {
      createClassResult <- createClass(CLASS_NAME)
      createIndexResult <- createIndex(INDEX_NAME, CLASS_NAME, termFields, valueFields)
      createCustomerIdIndexResult <- createIndex("orders_by_customer_id", CLASS_NAME, customerTermFields, valueFields)
    } yield {
      logger.info(s"Created $CLASS_NAME class")
      logger.info(s"Created $INDEX_NAME index: ${JsonUtil.toJson(createIndexResult)}")
      logger.info(s"Created orders_by_customer_id index: ${JsonUtil.toJson(createCustomerIdIndexResult)}")
    }
  }
}

case class OrderDetails(productID: Int, unitPrice: Double, quantity: Int, discount: Double)

object OrderDetails extends Logging {

  implicit val orderDetailsCodec: Codec[OrderDetails] = Codec.caseClass[OrderDetails]

  implicit val orderDetailsDecoder: Decoder[OrderDetails] = deriveDecoder[OrderDetails]
  implicit val orderDetailsEncoder: Encoder[OrderDetails] = deriveEncoder[OrderDetails]
}
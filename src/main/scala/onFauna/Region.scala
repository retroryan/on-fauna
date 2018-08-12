package onFauna

import faunadb.FaunaClient
import faunadb.values.Codec
import grizzled.slf4j.Logging
import io.circe._
import io.circe.generic.semiauto._
import onFauna.FaunaUtils.{RefField, TermField, createClass, createIndex}

import scala.concurrent.{ExecutionContext, Future}

case class Region(regionID: Int, name:String, territories:Seq[Territory])

object Region extends Logging {

  implicit val regionCodec: Codec[Region] = Codec.caseClass[Region]

  implicit val regionDecoder: Decoder[Region] = deriveDecoder[Region]
  implicit val regionEncoder: Encoder[Region] = deriveEncoder[Region]

  var CLASS_NAME = "region"
  var INDEX_NAME = "region_by_id"
  val termFields = Seq(TermField("regionID"))
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

case class Territory(territoryID: Int, name:String)

object Territory extends Logging {

  implicit val territoryCodec: Codec[Territory] = Codec.caseClass[Territory]

  implicit val territoryDecoder: Decoder[Territory] = deriveDecoder[Territory]
  implicit val territoryEncoder: Encoder[Territory] = deriveEncoder[Territory]
}
package onFauna

import faunadb.FaunaClient
import faunadb.values.Codec
import grizzled.slf4j.Logging
import io.circe._
import io.circe.generic.semiauto._
import onFauna.FaunaUtils.{RefField, TermField, createClass, createIndex}

import scala.concurrent.{ExecutionContext, Future}

case class Employee(employeeID:Int, lastName:String, firstName:String, title:String, titleOfCourtesy:String, birthDate:String, hireDate:String, notes:String, reportsTo:Option[Int], address:Address, territoryIDs:Seq[Int] )

object Employee extends Logging {

  implicit val employeeCodec: Codec[Employee] = Codec.caseClass[Employee]

  implicit val employeeDecoder: Decoder[Employee] = deriveDecoder[Employee]
  implicit val employeeEncoder: Encoder[Employee] = deriveEncoder[Employee]


  var CLASS_NAME = "employee"
  var INDEX_NAME = "employee_by_id"
  val termFields = Seq(TermField("employeeID"))
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
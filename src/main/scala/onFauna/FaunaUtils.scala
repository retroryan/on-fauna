package onFauna

import faunadb.FaunaClient
import faunadb.query._
import faunadb.values.{Encoder, Value}
import grizzled.slf4j.Logging

import scala.concurrent.{ExecutionContext, Future}

object FaunaUtils extends Logging {

  def createFaunaClient(dbName: String)(implicit ec: ExecutionContext): FaunaClient = {
    logger.info("starting create fauna client")

    val faunaDBConfig = FaunaDBConfig.getFaunaDBConfig

    //Create an admin client. This is the client we will use to create the database
    //val adminClient = FaunaClient(faunaDBConfig.secret, faunaDBConfig.endPoint)

    //default to using the cloud end point
    val adminClient = FaunaClient(faunaDBConfig.secret)

    logger.info("Successfully connected to FaunaDB as Admin!")

    val databaseRequest = createFaunaDatabase(dbName, faunaDBConfig.deleteDB, adminClient)

    //only block on startup when we create the database
    val createDBResponse = await(databaseRequest)

    logger.info(s"Created database: $dbName :: \n${JsonUtil.toJson(createDBResponse)}")

    /*
    * Create a key specific to the database we just created. We will use this to
    * create a new client we will use in the remainder of the examples.
    */
    await(databaseRequest)
    val eventualValue = adminClient.query(CreateKey(Obj("database" -> Database(dbName), "role" -> "server")))
    val keyReq = getEventualData[String](eventualValue, "secret")
    val serverKey = await(keyReq)
    adminClient.close
    //FaunaClient(serverKey, faunaDBConfig.endPoint)
    FaunaClient(serverKey)
  }

  /*
 * The code below creates the Database that will be used for this example. Please note that
 * the existence of the database is  evaluated and one of two options is followed in a single call to the Fauna DB:
 * --- If the DB already exists, just return the existing DB
 * --- Or delete the existing DB and recreate
 */
  private def createFaunaDatabase(dbName: String, deleteDB: Boolean, adminClient: FaunaClient)(implicit ec: ExecutionContext) = {
    if (deleteDB) {
      logger.info(s"deleting existing database")
      adminClient.query(
        Do(
          If(
            Exists(Database(dbName)),
            Delete(Database(dbName)),
            true
          ),
          CreateDatabase(Obj("name" -> dbName)))
      )
    }
    else {
      logger.info(s"creating or getting database")
      adminClient.query(
        If(
          Exists(Database(dbName)),
          Get(Database(dbName)),
          CreateDatabase(Obj("name" -> dbName))
        )
      )
    }
  }

  def createClass(className: String)(implicit client: FaunaClient, ec: ExecutionContext): Future[Value] = {
    val classObj = Class(className)
    val createClass = CreateClass(Obj("name" -> className))
    val conditionalCreateClass = If(
      Exists(classObj),
      Get(classObj),
      createClass
    )

    client.query(conditionalCreateClass)
  }

  sealed trait Field

  case class TermField(name: String) extends Field

  case class ValueField(name: String, reverse: Boolean) extends Field

  case class RefField(name: String = "ref") extends Field

  def createIndex(indexName: String, className: String, terms: Seq[Field], values: Seq[Field])(implicit client: FaunaClient, ec: ExecutionContext): Future[Value] = {
    val indexObj = Index(indexName)

    val termsArr = terms.collect {
      case term: TermField => Obj("field" -> Arr("data", term.name))
      case refField: RefField => Obj("field" -> Arr("ref"))
    }

    val valuesArr = values.collect {
      case value: ValueField => Obj("field" -> Arr("data", value.name), "reverse" -> value.reverse)
      case refField: RefField => Obj("field" -> Arr("ref"))
    }

    val createIndex = CreateIndex(
      Obj(
        "name" -> indexName,
        "source" -> Class(className),
        "terms" -> Arr(termsArr: _*),
        "values" -> Arr(valuesArr: _*)
      )
    )


    val conditionalCreateIndex = If(
      Exists(indexObj),
      Get(indexObj),
      createIndex
    )

    client.query(conditionalCreateIndex)
  }

  def createClassIndex(className: String)(implicit client: FaunaClient, ec: ExecutionContext): Future[Value] = {
    val indexName = s"${className}_class_index"

    val indexObj = Index(indexName)

    val createIndex = CreateIndex(
      Obj(
        "name" -> indexName,
        "source" -> Class(className)
      )
    )
    val conditionalCreateIndex = If(
      Exists(indexObj),
      Get(indexObj),
      createIndex
    )
    client.query(conditionalCreateIndex)
  }

  def save[A : Encoder](clazz:String, value:A)(implicit client: FaunaClient, ec: ExecutionContext): Future[String] = {
    val eventualValue = client.query(
      Create(
        Class(clazz),
        Obj("data" -> value))
    )

   /* eventualValue.foreach {
      strz => println(s"created: $strz")
    }*/
    eventualValue.map(v => v.toString)
  }
}

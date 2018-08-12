package onFauna

import faunadb.FaunaClient
import grizzled.slf4j.Logging
import io.circe
import io.circe.parser._

import scala.io.Source
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main extends Logging {

  val NORTHWINDS_DB = "northwinds"

  def main(args: Array[String]): Unit = {

    logger.info("starting graph ledger tests")
    implicit val faunaClient: FaunaClient = FaunaUtils.createFaunaClient(NORTHWINDS_DB)

    createSchemas

    loadData
  }

  def createSchemas(implicit client: FaunaClient) = {

    val work = for {
      _ <- Address.createSchema
      _ <- Category.createSchema
      _ <- Customer.createSchema
      _ <- Employee.createSchema
      _ <- Order.createSchema
      _ <- Product.createSchema
      _ <- Region.createSchema
      _ <- Shipper.createSchema
      _ <- Supplier.createSchema

    } yield {
      logger.info(s"initialized schema \n")
    }

    await(work)
  }

  def loadData(implicit client: FaunaClient) = {

    val parentDir = "data-json"

    val work = for {
      _ <- loadCategories(parentDir)
      _ <- loadCustomers(parentDir)
      _ <- loadEmployees(parentDir)
      _ <- loadOrders(parentDir)
      _ <- loadProducts(parentDir)
      _ <- loadRegions(parentDir)
      _ <- loadShipper(parentDir)
      _ <- loadSupplier(parentDir)
    }
      yield {
        logger.info(s"loaded data \n")
      }

    await(work)

  }


  private def loadCategories(parentDir: String)(implicit client: FaunaClient) = {
    val filename = s"$parentDir/categories.json"
    val rawJson = Source.fromFile(filename).mkString
    val parsedJson = parse(rawJson)

    val loadRes = for {
      categoriesJson <- parsedJson
      categories <- categoriesJson.as[Seq[Category]]
    }
      yield {
        categories.map { category =>
          println(s"$category")
          FaunaUtils.save(Category.CLASS_NAME, category)
        }
      }

    processResults(loadRes)
  }

  private def loadCustomers(parentDir: String)(implicit client: FaunaClient) = {
    val filename = s"$parentDir/customers.json"
    val rawJson = Source.fromFile(filename).mkString
    val parsedJson = parse(rawJson)

    println("loaded customers")

    val loadRes = for {
      customersJson <- parsedJson
      customers <- customersJson.as[Seq[Customer]]
    }
      yield {
        customers.map { customer =>
          println(customer)
          FaunaUtils.save(Customer.CLASS_NAME, customer)
        }
      }

    processResults(loadRes)
  }

  private def loadEmployees(parentDir: String)(implicit client: FaunaClient) = {
    val filename = s"$parentDir/employees.json"
    val rawJson = Source.fromFile(filename).mkString
    val parsedJson = parse(rawJson)

    println("loaded employees")

    val loadRes = for {
      employeesJson <- parsedJson
      employees <- employeesJson.as[Seq[Employee]]
    }
      yield {
        employees.map { employee =>
          println(employee)
          FaunaUtils.save(Employee.CLASS_NAME, employee)
        }
      }

    processResults(loadRes)
  }

  private def loadOrders(parentDir: String)(implicit client: FaunaClient) = {
    val filename = s"$parentDir/orders.json"
    val rawJson = Source.fromFile(filename).mkString
    val parsedJson = parse(rawJson)

    println("loaded orders")

    val loadRes = for {
      ordersJson <- parsedJson
      orders <- ordersJson.as[Seq[Order]]
    }
      yield {
        orders.map { order =>
          println(order)
          FaunaUtils.save(Order.CLASS_NAME, order)
        }
      }

    processResults(loadRes)
  }


  def loadProducts(parentDir: String)(implicit client: FaunaClient) = {
    val filename = s"$parentDir/products.json"
    val rawJson = Source.fromFile(filename).mkString
    val parsedJson = parse(rawJson)

    println("loaded products")

    val loadRes = for {
      productsJson <- parsedJson
      products <- productsJson.as[Seq[Product]]
    }
      yield {
        products.map { product =>
          println(product)
          FaunaUtils.save(Product.CLASS_NAME, product)
        }
      }

    processResults(loadRes)
  }

  def loadRegions(parentDir: String)(implicit client: FaunaClient) = {
    val filename = s"$parentDir/regions.json"
    val rawJson = Source.fromFile(filename).mkString
    val parsedJson = parse(rawJson)

    println("loaded regions")

    val loadRes = for {
      regionsJson <- parsedJson
      regions <- regionsJson.as[Seq[Region]]
    }
      yield {
        regions.map { region =>
          println(region)
          FaunaUtils.save(Region.CLASS_NAME, region)
        }
      }
    processResults(loadRes)
  }

  def loadShipper(parentDir: String)(implicit client: FaunaClient) = {
    val filename = s"$parentDir/shippers.json"
    val rawJson = Source.fromFile(filename).mkString
    val parsedJson = parse(rawJson)

    println("loaded shippers")

    val loadRes = for {
      shippersJson <- parsedJson
      shippers <- shippersJson.as[Seq[Shipper]]
    }
      yield {
        shippers.map { shipper =>
          println(shipper)
          FaunaUtils.save(Shipper.CLASS_NAME, shipper)
        }
      }

    processResults(loadRes)
  }

  def loadSupplier(parentDir: String)(implicit client: FaunaClient) = {
    val filename = s"$parentDir/suppliers.json"
    val rawJson = Source.fromFile(filename).mkString
    val parsedJson = parse(rawJson)

    println("loaded suppliers")

    val loadRes = for {
      suppliersJson <- parsedJson
      suppliers <- suppliersJson.as[Seq[Supplier]]
    }
      yield {
        suppliers.map { supplier =>
          println(supplier)
          FaunaUtils.save(Supplier.CLASS_NAME, supplier)
        }
      }

    processResults(loadRes)
  }

  private def processResults(loadRes: Either[circe.Error, Seq[Future[String]]]) = {
    loadRes match {
      case Left(error) =>
        println(error)
        Future.failed(error)
      case Right(strs) =>
        val eventualStrings = Future.sequence(strs)
        eventualStrings
    }
  }

}

package onFauna

/*
 * Read and set connection information so it does not have to be repeated in all of the examples
 */

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ValueReader


case class FaunaDBConfig(endPoint: String, secret: String, deleteDB: Boolean, dbName:String)

object FaunaDBConfig {

  def getFaunaDBConfig: FaunaDBConfig = {
    val config = ConfigFactory.load()
    config.as[FaunaDBConfig]("fauna")
  }


  implicit val reader: ValueReader[FaunaDBConfig] = ValueReader.relative[FaunaDBConfig] { config =>
    val url = config.getString("url")

    val secret = config.getString("secret")
    val deleteDB = config.getBoolean("delete_db")
    val dbName = config.getString("db_name")

    FaunaDBConfig(
      url,
      secret,
      deleteDB,
      dbName
    )
  }
}

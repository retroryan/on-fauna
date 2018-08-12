
import faunadb.values._
import scala.concurrent.duration._

import scala.concurrent.{Await, ExecutionContext, Future}

package object onFauna {

  def getEventualData[A : Decoder](eventualValue: Future[Value], key: String = "data")(implicit ec: ExecutionContext): Future[A] = {
    eventualValue
      .flatMap { v =>
        Future.fromTry(v(key).to[A].toEither.left.map(x => new Exception(s"errs ${x}")).toTry)
      }
  }

  //  [A : Decoder] is the same as adding the following on the end
  // (implicit ev:Decoder[A])
  def getEventualCollection[A : Decoder](eventualValue: Future[Value])(implicit ec: ExecutionContext): Future[Seq[A]] = {
    eventualValue
      .flatMap { v =>
        Future.fromTry(v.collect(Field.to[A]).toEither.left.map(x => new Exception(s"errs ${x}")).toTry)
      }
  }

  case class DeserializationException(message: String) extends Exception


  implicit class ResultToTry[A](val x: Result[A]) {
    def asTry: scala.util.Try[A] = {
      x.toEither
        .left
        .map(errs => DeserializationException(s"$errs"))
        .toTry
    }
  }

  def await[T](f: Future[T]): T = Await.result(f, 15.second)
}

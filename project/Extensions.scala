import bintry.Client
import org.json4s.native.Serialization.writePretty
import org.json4s.{DefaultFormats, JValue}
import sbt.internal.util.ManagedLogger

import scala.concurrent.duration.DurationLong
import scala.concurrent.{Await, Future}

object Extensions {
  implicit val jsonFormats: DefaultFormats.type = DefaultFormats

  implicit class RichLogger[T](x: T) {
    def log(prefix: String = "")(implicit logger: ManagedLogger): T = {
      val actualPrefix = if (prefix.isEmpty) "" else s"$prefix: "
      logger.info(s"$actualPrefix$x")
      x
    }
  }

  implicit class RichJson(x: JValue) {
    def pretty: String = writePretty(x)
  }

  implicit class RichCompletion[T](x: Client.Completion[T]) {
    import dispatch.{Future => _, _}
    def runJson(implicit log: ManagedLogger): Future[JValue] = x(as.json4s.Json)
  }

  implicit class RichFuture[T](f: Future[T]) {
    def get: T = Await.result(f, 15.minute)
  }

}

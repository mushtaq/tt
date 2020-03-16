import bintry.Client
import org.json4s.JValue
import sbt.internal.util.ManagedLogger

import scala.concurrent.{Await, Future}
import scala.util.control.NonFatal
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationLong
import org.json4s.native.Serialization.writePretty
import org.json4s.DefaultFormats

object Extensions {
  implicit val jsonFormats: DefaultFormats.type = DefaultFormats

  implicit class RichLogger[T](x: T) {
    def log(implicit logger: ManagedLogger): T = {
      logger.info(x.toString)
      x
    }
  }

  implicit class RichJsonLogger(x: JValue) {
    def logJson(implicit logger: ManagedLogger): JValue = {
      logger.info(writePretty(x))
      x
    }
  }

  implicit class RichCompletion[T](x: Client.Completion[T]) {
    def run(implicit log: ManagedLogger): Future[T] = x().map(_.log).recover {
      case NonFatal(ex) => log.error(ex.getMessage); throw ex
    }

    import dispatch.{Future => _, _}

    def runJson(implicit log: ManagedLogger): Future[JValue] = x(as.json4s.Json).map(_.logJson).recover {
      case NonFatal(ex) => log.error(ex.getMessage); throw ex
    }

    def get(implicit log: ManagedLogger): T = Await.result(run, 5.minute)
    def getJson(implicit log: ManagedLogger): JValue = Await.result(runJson, 5.minute)
  }

}

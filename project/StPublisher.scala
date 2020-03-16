import bintry.Client
import org.json4s.JValue
import org.scalablytyped.converter.plugin.ScalablyTypedPluginBase
import sbt.Keys._
import sbt._
import sbt.internal.util.ManagedLogger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationLong
import scala.concurrent.{Await, Future}
import scala.util.control.NonFatal

object StPublisher extends AutoPlugin {
  override def requires: Plugins = ScalablyTypedPluginBase
  lazy val client: Client = Client("mausamy", "4934b5bfa581174c94e2054818850fd770728796")
  lazy val repo: client.Repo = client.repo("mausamy", "publish-dependencies")

  object autoImport {
    val stPublish = taskKey[Unit]("publish scalablytyped generated artifacts")
  }

  import ScalablyTypedPluginBase.autoImport._
  import autoImport._

  implicit class Show1[T](x: T) {
    def log(implicit logger: ManagedLogger): T = {
      logger.info(x.toString)
      x
    }
  }

  implicit class RichCompletion[T](x: Client.Completion[T]) {
    def run(implicit log: ManagedLogger): Future[T] = x().map(_.log).recover {
      case NonFatal(ex) => log.error(ex.getMessage); throw ex
    }

    import dispatch.{Future => _, _}
    import org.json4s.DefaultFormats
    implicit val jsonFormats: DefaultFormats.type = DefaultFormats

    def runJson(implicit log: ManagedLogger): Future[JValue] = x(as.json4s.Json).map(_.log).recover {
      case NonFatal(ex) => log.error(ex.getMessage); throw ex
    }

    def get(implicit log: ManagedLogger): T = Await.result(run, 5.minute)
    def getJson(implicit log: ManagedLogger): JValue = Await.result(runJson, 5.minute)
  }

  override lazy val projectSettings =
    Seq(
      stPublish := {
        implicit val log: ManagedLogger = streams.value.log
        val remotePackages = repo.packages().get.map(_.name)

        stImport.value
          .filter(_.organization == "org.scalablytyped")
          .foreach {
            moduleID =>
              val pkgName = moduleID.name.split("_").head
              val packageExists = remotePackages.contains(pkgName)
              if (!packageExists) {
                repo
                  .createPackage(pkgName)
                  .licenses("MIT")
                  .vcs("abc")
                  .desc("to test publishing")
                  .get
              }

              List("jars" -> ".jar", "srcs" -> "-sources.jar", "poms" -> ".pom").foreach {
                case (dir, suffix) =>
                  repo
                    .get(pkgName)
                    .mvnUpload(
                      s"org/scalablytyped/${moduleID.name}/${moduleID.revision}/${moduleID.name}-${moduleID.revision}$suffix",
                      new File(
                        s"${System.getProperty("user.home")}/.ivy2/local/org.scalablytyped/${moduleID.name}/${moduleID.revision}/$dir/${moduleID.name}$suffix")
                    )
                    .getJson
              }
          }
      }
    )
}

import Extensions._
import bintry.Client
import org.scalablytyped.converter.plugin.ScalablyTypedPluginBase
import sbt.Keys._
import sbt._
import sbt.internal.util.ManagedLogger

import scala.concurrent.ExecutionContext.Implicits.global

object StPublisher extends AutoPlugin {
  override def requires: Plugins = ScalablyTypedPluginBase

  object autoImport {
    val stPublish = taskKey[Unit]("publish scalablytyped generated artifacts to bintray")
  }

  import ScalablyTypedPluginBase.autoImport._
  import autoImport._

  override lazy val projectSettings =
    Seq(
      stPublish := {
        lazy val client: Client = Client("mausamy", "4934b5bfa581174c94e2054818850fd770728796")
        lazy val repo: client.Repo = client.repo(client.user, "tmtyped")

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
                  .licenses("Apache-2.0")
                  .vcs("https://github.com/mushtaq/typings")
                  .get
              }

              List("jars" -> ".jar", "srcs" -> "-sources.jar", "poms" -> ".pom").foreach {
                case (dir, suffix) =>
                  val remotePath =
                    s"org/scalablytyped/${moduleID.name}/${moduleID.revision}/${moduleID.name}-${moduleID.revision}$suffix"
                  val artifactLocation =
                    s"${System.getProperty("user.home")}/.ivy2/local/org.scalablytyped/${moduleID.name}/${moduleID.revision}/$dir/${moduleID.name}$suffix"

                  repo
                    .get(pkgName)
                    .mvnUpload(remotePath, new File(artifactLocation))
                    .getJson
              }
          }

        client.close()
      }
    )
}

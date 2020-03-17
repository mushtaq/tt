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
        val remotePackages = repo.packages()().get.map(_.name)
        remotePackages.mkString(", ").log("existing remote packages")

        stImport.value
          .filter(_.organization == "org.scalablytyped")
          .foreach {
            moduleId =>
              val moduleInfo = ModuleInfo(moduleId)
              val packageExists = remotePackages.contains(moduleInfo.pkgName)
              if (!packageExists) {
                repo
                  .createPackage(moduleInfo.pkgName)
                  .licenses("Apache-2.0")
                  .vcs("https://github.com/mushtaq/typings")()
                  .get
                  .log("created package")
              } else {
                moduleInfo.pkgName.log(s"package exists")
              }

              val latestVersion = repo.get(moduleInfo.pkgName)().get.versions.lastOption
              latestVersion.log(s"latest version for ${moduleInfo.pkgName}")
              val versionExists = latestVersion.contains(moduleId.revision)
              if (!versionExists) {
                List(moduleInfo.jarMapping, moduleInfo.sourceJarMapping, moduleInfo.pomMapping).foreach {
                  case (artifactFile, mavenPath) =>
                    mavenPath.log("uploading")
                    repo
                      .get(moduleInfo.pkgName)
                      .mvnUpload(mavenPath, artifactFile)
                      .publish(true)(dispatch.as.json4s.Json)
                      .get
                      .pretty
                      .log()
                }
              } else {
                moduleId.revision.log(s"this version is already uploaded for ${moduleInfo.pkgName}")
              }
          }

        client.close()
      }
    )
}

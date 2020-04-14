import java.nio.file.Files

import bintry.Client
import org.scalablytyped.converter.plugin.ScalablyTypedPluginBase
import sbt.Keys._
import sbt._

import scala.concurrent.ExecutionContext.Implicits.global
import java.nio.file.{Path => JPath}

object StPublisherPlugin extends AutoPlugin {
  override def requires: Plugins = ScalablyTypedPluginBase

  object autoImport {
    val stPublish = taskKey[Unit]("publish scalablytyped generated artifacts to bintray")
  }

  import ScalablyTypedPluginBase.autoImport._
  import autoImport._

  override lazy val projectSettings =
    Seq(
      stPublish := {
        val client: Client    = Client("mausamy", "4934b5bfa581174c94e2054818850fd770728796")
        val repo: client.Repo = client.repo(client.user, "tmtyped")
        val log               = streams.value.log
        val stPublisher       = new StPublisher(repo)(log)
        val modules           = stImport.value.toList.sortBy(_.name)
        val deps              = stPublisher.publishAll("org.scalablytyped", modules)
        log.info("writing latest versions in the dependencies.txt")
        Files.writeString(JPath.of((baseDirectory.value / "dependencies.txt").toURI), deps.mkString("\n"))
        client.close()
      }
    )
}

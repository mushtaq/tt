import java.nio.file.Files

import bintry.Client
import org.scalablytyped.converter.plugin.ScalablyTypedPluginBase
import sbt.Keys._
import sbt._

import scala.concurrent.ExecutionContext.Implicits.global
import java.nio.file.{Path => JPath}

object StPublisherPlugin extends AutoPlugin {
  private val BINTRAY_USER_NAME = sys.env("BINTRAY_USER_NAME")
  private val BINTRAY_TOKEN     = sys.env("BINTRAY_TOKEN")

  override def requires: Plugins = ScalablyTypedPluginBase

  object autoImport {
    val stPublish = taskKey[Unit]("publish scalablytyped generated artifacts to bintray")
  }

  import ScalablyTypedPluginBase.autoImport._
  import autoImport._

  override lazy val projectSettings =
    Seq(
      stPublish := {
        val client: Client    = Client(BINTRAY_USER_NAME, BINTRAY_TOKEN)
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

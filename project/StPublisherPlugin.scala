import bintry.Client
import org.scalablytyped.converter.plugin.ScalablyTypedPluginBase
import sbt.Keys._
import sbt._

import scala.concurrent.ExecutionContext.Implicits.global

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
        val client: Client = Client("mausamy", "4934b5bfa581174c94e2054818850fd770728796")
        val repo: client.Repo = client.repo(client.user, "tmtyped")
        val stPublisher = new StPublisher(repo)(streams.value.log)
        stPublisher.publishAll("org.scalablytyped", stImport.value)
        client.close()
      }
    )
}

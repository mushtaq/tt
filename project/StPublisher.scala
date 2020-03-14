import org.scalablytyped.converter.plugin.ScalablyTypedPluginBase
import sbt._
import Keys._

object StPublisher extends AutoPlugin {
  override def requires: Plugins = ScalablyTypedPluginBase

  object autoImport {
    val stPublish = taskKey[Unit]("publish scalablytyped generated artifacts")
  }

  import autoImport._
  import ScalablyTypedPluginBase.autoImport._

  override lazy val projectSettings =
    Seq(
      stPublish := {
        val log = streams.value.log
        (Compile / dependencyClasspath).value.foreach { x =>
          val a = x.metadata
          log.info(x.toString)
          log.info(a.toString)
        }
        stImport.value.filter(_.organization == "org.scalablytyped").foreach { x =>
          x.toString()
          val path = s"org/scalablytyped/${x.name}/${x.revision}"
          log.info(path)
//          log.info(x.sources().explicitArtifacts.head.)
        }
      }
    )
}

import scala.sys.process.Process

inThisBuild(
  Seq(
    scalaVersion := "2.13.1",
    version := "0.1.0-SNAPSHOT",
    organization := "com.github.tmtsoftware.typings",
    organizationName := "ThoughtWorks"
  )
)

lazy val `typings-root` = project.in(file(".")).aggregate(typings)

lazy val typings = project
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin, ScalaJSPlugin)
  .settings(
    externalNpm := {
      Process("sh -l -c yarn").!
      baseDirectory.value
    },
    stStdlib := List("es6"),
    stFlavour := Flavour.Slinky
  )

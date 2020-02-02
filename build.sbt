import scala.sys.process.Process

lazy val `typings-root` = project.in(file(".")).aggregate(typings)

lazy val typings = project
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin, ScalaJSPlugin)
  .settings(
    scalaVersion := "2.13.1",
    version := "0.1.0-SNAPSHOT",
    organization := "com.github.tmtsoftware.typings",
    organizationName := "ThoughtWorks"
  )
  .settings(
    externalNpm := {
      Process("yarn", baseDirectory.value).!
      baseDirectory.value
    },
    Compile / stStdlib := List("es6"),
    Compile / stFlavour := Flavour.Slinky
  )

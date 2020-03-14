import scala.sys.process.Process

inThisBuild(
  Seq(
    scalaVersion := "2.13.1",
    version := "0.1.0-SNAPSHOT",
    organization := "com.github.tmtsoftware.typings",
    organizationName := "ThoughtWorks",
    libraryDependencies := Seq(
//      "org.foundweekends" %% "bintry" % "0.5.2"
    )
  )
)

lazy val `typings-root` = project.in(file(".")).aggregate(typings)

lazy val `demo` = project.settings(
  scalaVersion := "2.12.10",
  version := "0.1.0-SNAPSHOT",
  organization := "com.github.tmtsoftware.typings",
  organizationName := "ThoughtWorks",
  libraryDependencies := Seq(
          "org.foundweekends" %% "bintry" % "0.5.2"
  )
)

lazy val typings = project
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin, ScalaJSPlugin, StPublisher)
  .settings(
    externalNpm := {
      Process("sh -l -c yarn", baseDirectory.value).!
      baseDirectory.value
    },
    stStdlib := List("es6"),
    stFlavour := Flavour.Slinky
  )

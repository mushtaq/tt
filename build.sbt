enablePlugins(ScalablyTypedConverterExternalNpmPlugin, ScalaJSPlugin, StPublisherPlugin)

scalaVersion := "2.13.1"
version := "0.1.0-SNAPSHOT"
organization := "com.github.tmtsoftware.typings"
organizationName := "ThoughtWorks"

externalNpm := {
  sys.process.Process("sh -l -c yarn", baseDirectory.value).!
  baseDirectory.value
}
stStdlib := List("es6")
stFlavour := Flavour.Slinky

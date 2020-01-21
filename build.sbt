import org.scalajs.core.tools.linker.ModuleKind

import scala.sys.process.Process

inThisBuild(
  Seq(
    scalaVersion := "2.13.1",
    version := "0.1.0-SNAPSHOT",
    organization := "com.github.tmtsoftware.tt",
    organizationName := "ThoughtWorks"
  )
)

lazy val `tmt-typed-root` = project.in(file(".")).aggregate(`tmt-typed`)

lazy val `tmt-typed` = project
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .configure(baseSettings)
  .settings(
    externalNpm := {
      Process("yarn", baseDirectory.value).!
      baseDirectory.value
    },
    Compile / stStdlib := List("es6"),
    Compile / stEnableScalaJsDefined := Selection.All,
    Compile / stOutputPackage := "tt"
  )

lazy val baseSettings: Project => Project =
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      scalacOptions ++= ScalacOptions.flags,
      emitSourceMaps := false,
      scalaJSModuleKind := ModuleKind.CommonJSModule,
      scalacOptions += "-P:scalajs:sjsDefinedByDefault"
    )

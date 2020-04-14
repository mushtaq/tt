addSbtPlugin("org.scala-js"                % "sbt-scalajs"   % "1.0.1")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta9")

libraryDependencies += "org.foundweekends" %% "bintry" % "0.5.2"

resolvers += Resolver.bintrayRepo("oyvindberg", "converter")

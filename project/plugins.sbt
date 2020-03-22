addSbtPlugin("org.scala-js"                % "sbt-scalajs"     % "0.6.32")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter06" % "1.0.0-beta6")

libraryDependencies += "org.foundweekends" %% "bintry" % "0.5.2"

resolvers += Resolver.bintrayRepo("oyvindberg", "converter")

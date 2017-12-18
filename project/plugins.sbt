// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.14")

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "0.6.6")

addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.2.0")

resolvers += "Flyway" at "https://flywaydb.org/repo"
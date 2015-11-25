name := "TodoListSample"

version := "1.0"

lazy val `todolistsample` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

PlayKeys.playOmnidoc := false

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  evolutions,
  "com.typesafe.slick" %% "slick" % "3.1.0",
  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0",
  "joda-time" % "joda-time" % "2.5",
  "org.joda" % "joda-convert" % "1.2" ,
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.4.190",
  "com.typesafe.play" %% "play-json" % "2.3.4",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.6" % "test",
  "org.scalaz" %% "scalaz-core" % "7.1.5"
)


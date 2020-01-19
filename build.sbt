import NativePackagerHelper._

organization := "com.cobble"

name := "CorridorsOfTime"

version := "1.2.0"

scalaVersion := "2.13.1"

maintainer := "https://github.com/Cobbleopolis/"

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= Seq(
    "org.apache.commons" % "commons-csv" % "1.4",
    "com.github.graphstream" % "gs-core" % "2.0.0-beta",
    "com.github.graphstream" % "gs-ui-swing" % "2.0-alpha",
    "com.typesafe.play" %% "play-json" % "2.8.1"
)

mainClass in Compile := Some("com.cobble.corridor.CorridorsOfTime")

mappings in Universal ++= Seq(
    file("codes.json") -> "codes.json",
    file("Graph.css") -> "Graph.css",
    file("Master.csv") -> "Master.csv",
    file("README.md") -> "README.md",
    file("LICENSE") -> "LICENSE",
)

mappings in Universal ++= directory("images")

enablePlugins(UniversalPlugin, JavaAppPackaging)
organization := "com.cobble"

name := "CorridorsOfTime"

version := "1.0.0"

scalaVersion := "2.13.1"

maintainer := "https://github.com/Cobbleopolis/"

libraryDependencies ++= Seq(
    "org.graphstream" % "gs-core" % "1.2",
    "org.graphstream" % "gs-ui" % "1.2",
    "com.typesafe.play" %% "play-json" % "2.8.1"
)

mainClass in Compile := Some("com.cobble.corridor.CorridorsOfTime")

mappings in Universal ++= Seq(
    file("codes.json") -> "codes.json",
    file("README.md") -> "README.md",
    file("LICENSE") -> "LICENSE",
)

enablePlugins(UniversalPlugin, JavaAppPackaging)
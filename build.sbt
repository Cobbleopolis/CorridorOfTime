organization := "com.cobble"

name := "CorridorOfTime"

version := "1.0.0"

scalaVersion := "2.13.1"

maintainer := "https://github.com/Cobbleopolis/"

libraryDependencies ++= Seq(
    "org.graphstream" % "gs-core" % "1.2",
    "org.graphstream" % "gs-ui" % "1.2",
    "com.typesafe.play" %% "play-json" % "2.8.1"
)

mainClass in Compile := Some("com.cobble.corridor.CorridorOfTime")

mappings in Universal ++= Seq(
    file("codes.json") -> "codes.json",
    file("LICENSE") -> "LICENSE",
)

enablePlugins(UniversalPlugin, JavaAppPackaging)
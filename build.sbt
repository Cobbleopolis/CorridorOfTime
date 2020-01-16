name := "CorridorOfTime"

version := "0.1"

scalaVersion := "2.13.1"

resolvers += "Typesafe Repo" at "https://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
    "org.apache.commons" % "commons-csv" % "1.4",
    "org.graphstream" % "gs-core" % "1.2",
    "org.graphstream" % "gs-ui" % "1.2",
    "com.typesafe.play" %% "play-json" % "2.8.1"
)
//"org.jgrapht" % "jgrapht-core" % "1.3.0",
//"com.github.vlsi.mxgraph" % "jgraphx" % "4.0.5

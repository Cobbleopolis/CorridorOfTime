package com.cobble.corridor

class CodeMap(val codes: Array[Code]) {

    var connections: Array[Connection] = Array()

    def generateMap(): Array[Connection] = {
        codes.foreach(c => {
            connections ++= codes.map(o => (o, c.canConnect(o)))
                .filter(o => o._2.nonEmpty && !connections.exists(i => i.start == o._1 && i.end == c))
                .map(i => Connection(c, i._1, i._2.get))
        })
        connections
//        var allConnections: Array[(Code, Code)] = Array()
//        codes.map(c => {
//            println(s"Finding connections for: ${c.checksum}")
//            val connections: Array[(Code, Int)] = codes.map(o => (o, c.canConnect(o)))
//                .filter(o => c != o._1 && !allConnections.contains((o._1, c)) && o._2.nonEmpty).map(i => (i._1, i._2.getOrElse(-1)))
//            allConnections ++= connections.map(o => (c, o._1))
//            (c, connections)
//        }).filter(_._2.length > 0)
    }
}

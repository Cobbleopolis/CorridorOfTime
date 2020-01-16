package com.cobble.corridor

class CodeMap(codes: Array[Code]) {

    def generateMap(): Array[(Code, Array[(Code, Int)])] = {
        var allConnections: Array[(Code, Code)] = Array()
        codes.map(c => {
            val connections: Array[(Code, Int)] = codes.map(o => (o, c.canConnect(o)))
                .filter(o => c != o._1 && !allConnections.contains((o._1, c)) && o._2.nonEmpty).map(i => (i._1, i._2.getOrElse(-1)))
            allConnections ++= connections.map(o => (c, o._1))
            (c, connections)
        }).filter(_._2.length > 0)
    }
}

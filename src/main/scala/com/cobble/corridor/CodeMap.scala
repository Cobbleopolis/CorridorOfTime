package com.cobble.corridor

class CodeMap(val codes: Array[Code]) {

    var connections: Array[Connection] = Array()

    def generateMap(): Array[Connection] = {
        codes.foreach(c => {
            val newConnections: Array[Connection] =codes.map(o => (o, c.canConnect(o)))
                .filter(o =>
                    o._2.nonEmpty &&
                        !connections.exists(i => i.isPartOfConnection(c) && i.isPartOfConnection(o._1)) &&
                        !c.isSideConnected(o._2.get) && !o._1.isSideConnected(CorridorUtils.getOppositeSide(o._2.get))

                )
                .map(i => Connection(c, i._1, i._2.get))
            newConnections.foreach(c => {
                c.start.connections(c.side) = true
                c.end.connections(CorridorUtils.getOppositeSide(c.side)) = true
            })
            connections ++= newConnections
        })
        connections = connections.distinct.filter(_.isValid)
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

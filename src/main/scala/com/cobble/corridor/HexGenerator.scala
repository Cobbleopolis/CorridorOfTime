package com.cobble.corridor

import org.graphstream.stream.SourceBase

import scala.collection.mutable.ArrayBuffer

class HexGenerator(codeMap: CodeMap) extends SourceBase {

    final val GRAPH_X_SPACE: Double = 200

    final val GRAPH_Y_SPACE: Double = 300

    final val NODE_SPACE: Double = 40

    final val SIDE_COUNT: Int = 6

    final val SIDE_DELTA: Double = (2 * Math.PI) / SIDE_COUNT

    final val DEG_90: Double = (2 * Math.PI) / 4

    final val MAX_X: Double = 15000

    var largestX: Double = 0

    var lowestY: Double = 0

    var yAnchor: Double = 0

    var index: Int = 0

    val addedNodes: ArrayBuffer[Code] = ArrayBuffer()

    var nodeLocation: Map[Code, (Double, Double)] = Map()

    val addedConnections: ArrayBuffer[Connection] = ArrayBuffer()

    def begin(): Unit = {
        val startingCode: Code = codeMap.connections(index).start
        addNode(startingCode)
        setNodeLocation(startingCode, 0, 0)
    }

    def nextEvents(): Boolean = {
        val connectionOpt: Option[Connection] = codeMap.connections
            .filter(!addedConnections.contains(_))
            .find(con => addedNodes.exists(n => con.isPartOfConnection(n)))
        var codeOpt: Option[Code] = None
        if (connectionOpt.isDefined) {
            val connection: Connection = connectionOpt.get

            addNode(connection.start)
            addNodeAttribute(connection.start, "ui.label", connection.start.center.toString)
            var startNodeClass: String = connection.start.center.toString
            if (connection.start.isEntranceOrExit)
                startNodeClass += ", entranceexit"
            addNodeAttribute(connection.start, "ui.class",  startNodeClass)
            if (!nodeLocation.contains(connection.start))
                setNodeLocation(connection.start, getRotatedPoint(nodeLocation(connection.end), CorridorUtils.getOppositeSide(connection.side)))

            addNode(connection.end)
            addNodeAttribute(connection.end, "ui.label", connection.end.center.toString)
            var endNodeClass: String = connection.end.center.toString
            if (connection.end.isEntranceOrExit)
                endNodeClass += ", entranceexit"
            addNodeAttribute(connection.end, "ui.class", endNodeClass)
            if (!nodeLocation.contains(connection.end))
                setNodeLocation(connection.end, getRotatedPoint(nodeLocation(connection.start), connection.side))

            addEdge(connection)
            if (connection.isTraversable)
                addEdgeAttribute(connection, "ui.class", "traversable")
        } else {
            codeOpt = codeMap.connections.flatMap(_.codeArr).distinct.find(!addedNodes.contains(_))
            if (codeOpt.isDefined) {
                addNode(codeOpt.get)
                index += 1
                if (largestX > MAX_X) {
                    largestX = 0
                    yAnchor = lowestY - GRAPH_X_SPACE
                }
                setNodeLocation(codeOpt.get, largestX + GRAPH_X_SPACE, yAnchor)
            }
        }
        connectionOpt.isDefined || codeOpt.isDefined
    }

    def end(): Unit = {

    }

    def addNode(code: Code): Unit = {
        if (!addedNodes.contains(code)) {
            println(s"Adding Node: ${code.checksum}")
            sendNodeAdded(code.checksum, code.checksum)
            addedNodes += code
        }
    }

    def addNodeAttribute(code: Code, attribute: String, value: Any): Unit = {
        sendNodeAttributeAdded(code.checksum, code.checksum, attribute, value)
    }

    def setNodeLocation(code: Code, loc: (Double, Double)): Unit = setNodeLocation(code, loc._1, loc._2)

    def setNodeLocation(code: Code, x: Double, y: Double): Unit = {
        nodeLocation += (code -> (x, y))
        addNodeAttribute(code, "x", x)
        addNodeAttribute(code, "y", y)
        if (x > largestX)
            largestX = x
        if (y < lowestY)
            lowestY = y

    }

    def addEdge(connection: Connection): Unit = {
        if (!addedConnections.contains(connection)) {
            println(s"Adding Connection: $connection")
            sendEdgeAdded(connection.edgeId, connection.edgeId, connection.start.checksum, connection.end.checksum, false)
            addedConnections += connection
        }
    }

    def addEdgeAttribute(connection: Connection, attribute: String, value: Any): Unit = {
        sendEdgeAttributeAdded(connection.edgeId, connection.edgeId, attribute, value)
    }

    def getRotatedPoint(s: (Double, Double), side: Int): (Double, Double) = getRotatedPoint(s._1, s._2, side)

    def getRotatedPoint(sx: Double, sy: Double, side: Int): (Double, Double) = {
        (sx + (Math.cos(SIDE_DELTA * side + DEG_90) * NODE_SPACE), sy + (Math.sin(SIDE_DELTA * side + DEG_90) * NODE_SPACE))
    }

}

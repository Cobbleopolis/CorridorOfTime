package com.cobble.corridor

import org.graphstream.algorithm.generator.Generator
import org.graphstream.stream.SourceBase

import scala.collection.mutable.ArrayBuffer

class HexGenerator(codeMap: CodeMap) extends SourceBase with Generator {

    var index: Int = 0

    val addedNodes: ArrayBuffer[Code] = ArrayBuffer()

    override def begin(): Unit = {
        val startingCode: Code = codeMap.connections(index).start
        addNode(startingCode)
        addNodeAttribute(startingCode, "x", 0)
        addNodeAttribute(startingCode, "y", 0)
        index += 1
    }

    override def nextEvents(): Boolean = {
        val connectionOpt: Option[Connection] = codeMap.connections.find(con => addedNodes.exists(n => con.isPartOfConnection(n)))
        connectionOpt.isDefined //TODO actually do a thing
    }

    override def end(): Unit = {

    }

    def addNode(code: Code): Unit = {
        if (!addedNodes.contains(code)) {
            sendNodeAdded(code.checksum, code.checksum)
            addedNodes += code
        }
    }

    def addNodeAttribute(code: Code, attribute: String, value: Any*): Unit = {
        sendNodeAttributeAdded(code.checksum, code.checksum, attribute, value)
    }

}

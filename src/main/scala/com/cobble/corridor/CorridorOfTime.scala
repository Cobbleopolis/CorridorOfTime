package com.cobble.corridor

import scala.jdk.CollectionConverters._
import java.net.URL

import com.cobble.corridor.CodeSymbol.CodeSymbol
import org.graphstream.graph.{Edge, Node}
import org.graphstream.graph.implementations.{MultiGraph, MultiNode}
import org.graphstream.ui.swingViewer.{View, Viewer}
import org.graphstream.ui.swingViewer.Viewer.CloseFramePolicy
import org.graphstream.ui.swingViewer.util.Camera
import play.api.libs.json._

import scala.io.Source


object CorridorOfTime {

    var codeMap: CodeMap = _

    val graph: MultiGraph = new MultiGraph("The fuck Bungie")

    final val GRAPH_SPACE: Int = 1000

    final val NODE_SPACE: Int = 400

    final val SIDE_COUNT: Int = 6

    final val SIDE_DELTA: Double = (2 * Math.PI) / SIDE_COUNT

    def main(args: Array[String]): Unit = {
        generateData()
        generateGraph()
        setupStyle()
        val viewer: Viewer = graph.display(false)
        viewer.setCloseFramePolicy(CloseFramePolicy.EXIT)
        val view: View = viewer.getDefaultView
        view.resizeFrame(800, 600)
        val camera: Camera = view.getCamera
//        camera.setViewCenter(440000,2503000, 0)
        camera.setViewPercent(0.25)

    }

    def generateData(): Unit = {
        val codeJson: JsValue = Json.parse(Source.fromResource("codes.json").getLines.mkString("\n"))
        implicit val codeSymbolFormat: Format[CodeSymbol] = new Format[CodeSymbol] {
            def reads(json: JsValue): JsResult[CodeSymbol] = {
                val str: String = json.as[String].trim.toUpperCase
                if (CodeSymbol.values.exists(_.toString == str))
                    JsSuccess(CodeSymbol.withName(str))
                else
                    JsSuccess(CodeSymbol.UNKNOWN)
            }
            def writes(codeSymbol: CodeSymbol): JsValue = JsString(codeSymbol.toString)
        }
        implicit val codeFormat: Format[Code] = Json.format[Code]
        val codes: Array[Code] = codeJson("codes").as[Array[Code]].filter(_.isValid).distinct.take(1000) //TODO remove limit
        codeMap = new CodeMap(codes)
    }

    def generateGraph(): Unit = {
//        println("Generating Map...")
//        val generatedMap = codeMap.generateMap()
//        println("Generated Map")
//        generatedMap.map(i => (i._1, i._2.mkString("[", ",", "]"))).foreach(println)
//        val uniqueCodes: Array[Code] = (generatedMap.map(_._1) ++ generatedMap.flatMap(_._2.map(_._1))).distinct
//        uniqueCodes.foreach(println)
//        uniqueCodes.foreach(c => {
//            println(s"Adding node: ${c.checksum}")
//            graph.addNode(c.checksum).asInstanceOf[Node]
//                .addAttributes(Map(
//                    "ui.class" -> c.center.toString.asInstanceOf[AnyRef],
//                    "ui.label" -> c.center.toString.asInstanceOf[AnyRef]
//                ).asJava)
//        })
//        var graphOffset: Int = 0
//        generatedMap.foreach(i => {
//            i._2.foreach(j => {
//                val edgeStr: String = i._1.checksum + "|" + j._1.checksum
//                println(s"Adding edge: $edgeStr")
//                val edge: Edge = graph.addEdge(edgeStr, i._1.checksum, j._1.checksum).asInstanceOf[Edge]
//                edge.addAttribute("side", j._2)
//                val iNode: Node = graph.getNode[MultiNode](i._1.checksum)
//                val jNode: Node = graph.getNode[MultiNode](j._1.checksum)
//                if (!iNode.hasAttribute("x") || !iNode.hasAttribute("y")) {
//                    iNode.setAttribute("x", GRAPH_SPACE * graphOffset)
//                    iNode.setAttribute("y", 0)
//                    graphOffset += 1
//                }
//                val iPoint: (Int, Int) = (iNode.getAttribute[Int]("x"), iNode.getAttribute[Int]("y"))
//                val rotatedPoint: (Int, Int) = getRotatedPoint(iPoint._1, iPoint._2, j._2)
//                println(s"$iPoint, $rotatedPoint, ${j._2}")
//                jNode.setAttribute("x", rotatedPoint._1)
//                jNode.setAttribute("y", rotatedPoint._2)
//            })
//        })
    }

    def getRotatedPoint(sx: Int, sy: Int, side: Int): (Int, Int) = {
        (sx + (Math.cos(SIDE_DELTA * side) * NODE_SPACE).toInt, sy + (Math.sin(SIDE_DELTA * side) * NODE_SPACE).toInt)
    }

    def setupStyle(): Unit = {
        val cssUrl: URL = getClass.getClassLoader.getResource("Graph.css")
        graph.addAttribute("ui.stylesheet", s"url('$cssUrl')")
        graph.addAttribute("ui.quality")
        graph.addAttribute("ui.antialias")
    }

}

package com.cobble.corridor

import java.awt.Dimension
import java.awt.event.{WindowAdapter, WindowEvent}
import java.net.URL

import scala.jdk.CollectionConverters._
import com.cobble.corridor.CodeSymbol.CodeSymbol
import javax.swing.{JFrame, SwingUtilities}
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.swingViewer.{View, Viewer}
import org.graphstream.ui.swingViewer.Viewer.CloseFramePolicy
import org.graphstream.ui.swingViewer.util.{Camera, DefaultShortcutManager}
import play.api.libs.json._

import scala.io.{BufferedSource, Source}


object CorridorOfTime {

    var codeMap: CodeMap = _

    val graph: MultiGraph = new MultiGraph("The Fuck Bungie")

    def main(args: Array[String]): Unit = {
//        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")
        generateData()
        generateGraph()
        setupStyle()
        println(Thread.currentThread())
        SwingUtilities.invokeLater(new Runnable {
            println(Thread.currentThread())
            override def run(): Unit = createJframe()
        })
    }

    def generateData(): Unit = {
        val codesSource: BufferedSource = Source.fromFile("./codes.json")
        val codeJson: JsValue = Json.parse(codesSource.getLines.mkString("\n"))
        codesSource.close()
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
        codeMap.generateMap()
        val generator: HexGenerator = new HexGenerator(codeMap)
        generator.addSink(graph)
        generator.begin()
        var added: Boolean = false
        do {
            added = generator.nextEvents()
        } while(added)
        generator.end()
    }

    def setupStyle(): Unit = {
        val cssUrl: URL = getClass.getClassLoader.getResource("Graph.css")
        graph.addAttribute("ui.stylesheet", s"url('$cssUrl')")
        graph.addAttribute("ui.quality")
        graph.addAttribute("ui.antialias")
    }

    def createJframe(): Unit = {
        println(Thread.currentThread())
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler {
            override def uncaughtException(t: Thread, e: Throwable): Unit = {
                println(s"Unhandled Exception due to $t throwing $e")
                println("************START STACKTRACE************")
                e.printStackTrace()
                println("************END STACKTRACE************")
                t.getThreadGroup.list()
            }
        })
        val viewer: Viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD)
        viewer.setCloseFramePolicy(CloseFramePolicy.EXIT)
        val view: View = viewer.addDefaultView(false)
        view.setShortcutManager(new DefaultShortcutManager())
        val camera: Camera = view.getCamera
        val frame: JFrame = new JFrame("The fuck Bungie")
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        frame.addWindowListener(new WindowAdapter {
            override def windowClosing(e: WindowEvent): Unit = {
                viewer.close()
            }
        })
        frame.add(view)
        frame.setPreferredSize(new Dimension(800, 600))
        frame.pack()
        frame.setVisible(true)
    }

}

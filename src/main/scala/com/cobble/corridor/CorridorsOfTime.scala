package com.cobble.corridor

import java.awt.Dimension
import java.awt.event.{WindowAdapter, WindowEvent}
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

import scala.jdk.CollectionConverters._

import com.cobble.corridor.CodeSymbol.CodeSymbol
import javax.swing.{JFrame, SwingUtilities}
import org.apache.commons.csv.{CSVFormat, CSVParser, CSVRecord}
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.swing_viewer.{SwingViewer, ViewPanel}
import org.graphstream.ui.view.{View, Viewer}
import org.graphstream.ui.view.Viewer.CloseFramePolicy
import play.api.libs.json._

import scala.io.{BufferedSource, Source}


object CorridorsOfTime {

    final val TITLE: String = "The Fuck Bungie"

    var codeMap: CodeMap = _

    val graph: MultiGraph = new MultiGraph(TITLE)

    val codesJsonPaths: Array[Path] = Array(
        Paths.get(".", "Master.csv"),
        Paths.get(".", "Batch.csv"),
        Paths.get(".", "codes.json"),
        Paths.get("..", "codes.json")
    )

    val cssPaths: Array[Path] = Array(
        Paths.get(".", "Graph.css"),
        Paths.get("..", "Graph.css")
    )

    def main(args: Array[String]): Unit = {
        System.setProperty("org.graphstream.ui", "swing")
        generateData(args.headOption)
        generateGraph()
        setupStyle()
        SwingUtilities.invokeLater(() => createJframe())
    }

    def generateData(argPathOpt: Option[String]): Unit = {
        val existingPathOpt: Option[Path] = if (argPathOpt.isDefined)
            argPathOpt.map(Paths.get(_))
        else
            codesJsonPaths.find(Files.exists(_))

        if (existingPathOpt.isDefined) {

            val codes: Array[Code] = if (existingPathOpt.get.endsWith(".json"))
                getCodesFromJSON(existingPathOpt.get.toString)
            else
                getCodesFromCSV(existingPathOpt.get)
            codeMap = new CodeMap(codes)
        } else {
            System.err.println("No codes json file found. Looking for files:")
            codesJsonPaths.foreach(p => System.out.println(s"\t- $p"))
            System.exit(2)
        }
    }

    def generateGraph(): Unit = {
        codeMap.generateMap()
        val generator: HexGenerator = new HexGenerator(codeMap)
        generator.addSink(graph)
        generator.begin()
        var added: Boolean = false
        do {
            added = generator.nextEvents()
        } while (added)
        generator.end()
    }

    def setupStyle(): Unit = {
        val cssPathOpt: Option[URI] = cssPaths.find(Files.exists(_)).map(_.toUri)

        if (cssPathOpt.isDefined) {
            val cssSource: Source = Source.fromFile(cssPathOpt.get)
            var cssString: String = cssSource.getLines.mkString("\n")
            cssSource.close()
            if(cssPathOpt.get.toString.contains(".."))
                cssString = cssString.replaceAll("fill-image: url\\('\\.", "fill-image: url\\('\\.\\.")
            graph.setAttribute("ui.stylesheet", cssString)
        } else {
            println("Cannot find css file. Not adding styling. Looking for files: ")
            cssPaths.foreach(p => println(s"\t- $p"))
        }

        graph.setAttribute("ui.quality")
        graph.setAttribute("ui.antialias")
    }

    def createJframe(): Unit = {
        Thread.setDefaultUncaughtExceptionHandler((t: Thread, e: Throwable) => {
            println(s"Unhandled Exception due to $t throwing $e")
            println("************START STACKTRACE************")
            e.printStackTrace()
            println("************END STACKTRACE************")
            t.getThreadGroup.list()
        })
        val viewer: Viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD)
        viewer.setCloseFramePolicy(CloseFramePolicy.EXIT)
        val view: View = viewer.addDefaultView(false)
        view.setShortcutManager(new CorridorShortcutManager(graph))
        val frame: JFrame = new JFrame(TITLE)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        frame.addWindowListener(new WindowAdapter {
            override def windowClosing(e: WindowEvent): Unit = {
                super.windowClosing(e)
                viewer.close()
                frame.dispose()
                System.exit(0)
            }
        })
        frame.add(view.asInstanceOf[ViewPanel])
        frame.setPreferredSize(new Dimension(800, 600))
        frame.pack()
        frame.setVisible(true)
        frame.requestFocus()
    }

    def getCodesFromJSON(path: String): Array[Code] = {
        val codesSource: BufferedSource = Source.fromFile(path)
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
        codeJson("codes").as[Array[Code]].filter(_.isValid).distinct
    }

    def getCodesFromCSV(path: Path): Array[Code] = {
        val format: CSVFormat = CSVFormat.EXCEL.withHeader("Image Link", "Center", "Openings", "Link 1", "Link 2", "Link 3", "Link 4", "Link 5", "Link 6")
        val parser: Iterator[CSVRecord] = CSVParser.parse(path.toFile, StandardCharsets.UTF_8, format).iterator.asScala.drop(1)
        parser.map(record => {
            val wallArr: Array[Int] = record.get("Openings").split("\\D").filter(_.nonEmpty).map(_.toInt)
            val openingArr: Array[Boolean] = (1 to 6).map(i => !wallArr.contains(i)).toArray
            Code(CodeSymbol.fromFullName(record.get("Center")),
                openingArr,
                Array(
                    record.get("Link 1").trim.map(c => CodeSymbol.fromFullName(c.toString.toUpperCase)).toArray,
                    record.get("Link 2").trim.map(c => CodeSymbol.fromFullName(c.toString.toUpperCase)).toArray,
                    record.get("Link 3").trim.map(c => CodeSymbol.fromFullName(c.toString.toUpperCase)).toArray,
                    record.get("Link 4").trim.map(c => CodeSymbol.fromFullName(c.toString.toUpperCase)).toArray,
                    record.get("Link 5").trim.map(c => CodeSymbol.fromFullName(c.toString.toUpperCase)).toArray,
                    record.get("Link 6").trim.map(c => CodeSymbol.fromFullName(c.toString.toUpperCase)).toArray
                )
            )
        }).toArray.filter(_.isValid).distinct
    }

}

package com.cobble.corridor

import scala.jdk.CollectionConverters._
import java.net.URL
import java.nio.charset.StandardCharsets

import org.apache.commons.csv.{CSVFormat, CSVParser, CSVRecord}
import org.graphstream.graph.{Edge, Node}
import org.graphstream.graph.implementations.MultiGraph


object CorridorOfTime {

    var codeMap: CodeMap = _

    val graph: MultiGraph = new MultiGraph("The fuck Bungie")

    def main(args: Array[String]): Unit = {
        generateData()
        generateGraph()
        setupStyle()
        val view = graph.display()
        view.enableAutoLayout()
    }

    def generateData(): Unit = {
        val codeUrl: URL = getClass.getClassLoader.getResource("Codes.csv")
        println(codeUrl)
        val format: CSVFormat = CSVFormat.EXCEL.withHeader("Image Link", "Center", "Openings", "Link 1", "Link 2", "Link 3", "Link 4", "Link 5", "Link 6")
        val parser: Iterator[CSVRecord] = CSVParser.parse(codeUrl, StandardCharsets.UTF_8, format).iterator.asScala.drop(1)
        val codes: Array[Code] = parser.map(record => {
            val imgText: String = record.get("Image Link")
            Code(
                if(imgText.isEmpty) None else Some(imgText),
                CodeSymbol.fromFullName(record.get("Center")),
                record.get("Openings").split("\\D").filter(_.nonEmpty).map(_.toInt),
                record.get("Link 1").trim.map(c => CodeSymbol.withName(c.toString.toLowerCase)).toArray,
                record.get("Link 2").trim.map(c => CodeSymbol.withName(c.toString.toLowerCase)).toArray,
                record.get("Link 3").trim.map(c => CodeSymbol.withName(c.toString.toLowerCase)).toArray,
                record.get("Link 4").trim.map(c => CodeSymbol.withName(c.toString.toLowerCase)).toArray,
                record.get("Link 5").trim.map(c => CodeSymbol.withName(c.toString.toLowerCase)).toArray,
                record.get("Link 6").trim.map(c => CodeSymbol.withName(c.toString.toLowerCase)).toArray
            )
        }).toArray.distinct
        codeMap = new CodeMap(codes)
    }

    def generateGraph(): Unit = {
        graph.addAttribute("ui.quality")
        graph.addAttribute("ui.antialias")
        val generatedMap = codeMap.generateMap()
        generatedMap.map(i => (i._1, i._2.mkString("[", ",", "]"))).foreach(println)
        val uniqueCodes: Array[Code] = (generatedMap.map(_._1) ++ generatedMap.flatMap(_._2.map(_._1))).distinct
        uniqueCodes.foreach(println)
        uniqueCodes.foreach(c => graph.addNode(c.checksum).asInstanceOf[Node]
            .addAttributes(Map(
                "ui.class" -> c.centerSymbol.toString.asInstanceOf[AnyRef],
                "ui.label" -> c.centerSymbol.toString.asInstanceOf[AnyRef]
            ).asJava))
        generatedMap.foreach(i => {
            i._2.foreach(j =>
                graph.addEdge(i._1.checksum + "|" + j._1.checksum, i._1.checksum, j._1.checksum).asInstanceOf[Edge].addAttribute("layout.weight", 2)
            )
        })
    }

    def setupStyle(): Unit = {
        val cssUrl: URL = getClass.getClassLoader.getResource("Graph.css")
        graph.addAttribute("ui.stylesheet", s"url('${cssUrl}')")
    }

}

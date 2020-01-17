package com.cobble.corridor

import java.awt.event.KeyEvent
import java.io.File
import java.nio.file.{Path, Paths}

import org.graphstream.graph.Graph
import org.graphstream.ui.swing_viewer.util.DefaultShortcutManager
import org.joda.time.DateTime

class CorridorShortcutManager(graph: Graph) extends DefaultShortcutManager {

    val screenshotFolder: File = new File(Paths.get(".", "screenshots").toUri)

    override def keyTyped(event: KeyEvent): Unit = {
        super.keyTyped(event)
        if (event.getKeyChar == 'R')
            view.getCamera.resetView()
        else if (event.getKeyChar == ' ')
            takeScreenshot()
    }

    def takeScreenshot(): Unit = {
        if (!screenshotFolder.exists())
            screenshotFolder.mkdir()
        val dateTime: DateTime = DateTime.now()
        val path: Path = Paths.get(screenshotFolder.getCanonicalPath, s"Screenshot_$dateTime.png"
            .replaceAll(":", "-")
            .replaceAll("T", "_")
        )
        graph.setAttribute("ui.screenshot", path.toString)
        println(s"Screenshot saved to: $path")
    }

}

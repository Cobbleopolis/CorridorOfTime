package com.cobble.corridor

import com.cobble.corridor.CodeSymbol.CodeSymbol

case class Code(center: CodeSymbol, walls: Array[Boolean], nodes: Array[Array[CodeSymbol]]) {

    val checksum: String = center + "#" +
        walls.map(b => if (b) "1" else "0").mkString + "#" +
        nodes.map(_.mkString).mkString("#")

    lazy val isValid: Boolean = !nodes.flatten.contains(CodeSymbol.UNKNOWN)

    def isSideOpen(side: Int): Boolean = walls(side)

    def isSideEmpty(side: Int): Boolean = nodes(side).forall(_ == CodeSymbol.BLANK)

    def canConnect(other: Code): Option[Int] = {
        if (!this.isSideEmpty(0) && this.nodes(0).sameElements(other.nodes(3)))
            Some(0)
        else if(!this.isSideEmpty(1) && this.nodes(1).sameElements(other.nodes(4)))
            Some(1)
        else if (!this.isSideEmpty(2) && this.nodes(2).sameElements(other.nodes(5)))
            Some(2)
        else if (!this.isSideEmpty(3) && this.nodes(3).sameElements(other.nodes(0)))
            Some(3)
        else if (!this.isSideEmpty(4) && this.nodes(4).sameElements(other.nodes(1)))
            Some(4)
        else if (!this.isSideEmpty(5) && this.nodes(5).sameElements(other.nodes(2)))
            Some(5)
        else
            None
    }

    override def toString: String = checksum

    override def equals(obj: Any): Boolean = {
        obj match {
            case code: Code => checksum == code.checksum
            case _ => super.equals(obj)
        }
    }

    override def hashCode(): Int = checksum.hashCode
}

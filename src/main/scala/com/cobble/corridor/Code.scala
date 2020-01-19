package com.cobble.corridor

import com.cobble.corridor.CodeSymbol.CodeSymbol

case class Code(center: CodeSymbol, walls: Array[Boolean], nodes: Array[Array[CodeSymbol]]) {

    val checksum: String = center + "#" +
        walls.map(b => if (b) "1" else "0").mkString + "#" +
        nodes.map(_.mkString).mkString("#")

    lazy val isValid: Boolean = !(nodes.flatten.contains(CodeSymbol.UNKNOWN) || center == CodeSymbol.UNKNOWN)

    def isSideOpen(side: Int): Boolean = !walls(side)

    def isSideEmpty(side: Int): Boolean = nodes(side).forall(_ == CodeSymbol.BLANK)

    val isEntranceOrExit: Boolean = walls.zipWithIndex.exists(i => !i._1 && nodes(i._2).forall(_ == CodeSymbol.BLANK))

    def canConnect(other: Code): Option[Int] = {
        (0 to 5).find(i => !this.isSideEmpty(i) && this.nodes(i).sameElements(other.nodes(CorridorUtils.getOppositeSide(i))))
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

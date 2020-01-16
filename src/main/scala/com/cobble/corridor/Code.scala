package com.cobble.corridor

import com.cobble.corridor.CodeSymbol.CodeSymbol

case class Code(imgLink: Option[String], centerSymbol: CodeSymbol, openings: Array[Int],
           link1: Array[CodeSymbol],
           link2: Array[CodeSymbol],
           link3: Array[CodeSymbol],
           link4: Array[CodeSymbol],
           link5: Array[CodeSymbol],
           link6: Array[CodeSymbol]) {

    lazy val linkArray: Array[Array[CodeSymbol]] = Array(link1, link2, link3, link4, link5, link6)

    val checksum: String = centerSymbol + "#" +
        openings.mkString + "#" +
        link1.mkString + "#" +
        link2.mkString + "#" +
        link3.mkString + "#" +
        link4.mkString + "#" +
        link5.mkString + "#" +
        link6.mkString

    def isSideEmpty(side: Int): Boolean = linkArray(side - 1).forall(_ == CodeSymbol.BLANK)

    def canConnect(other: Code): Option[Int] = {
        if (!this.isSideEmpty(1) && this.link1.sameElements(other.link4))
            Some(1)
        else if(!this.isSideEmpty(2) && this.link2.sameElements(other.link5))
            Some(2)
        else if (!this.isSideEmpty(3) && this.link3.sameElements(other.link6))
            Some(3)
        else if (!this.isSideEmpty(4) && this.link4.sameElements(other.link1))
            Some(4)
        else if (!this.isSideEmpty(5) && this.link5.sameElements(other.link2))
            Some(5)
        else if (!this.isSideEmpty(6) && this.link6.sameElements(other.link3))
            Some(6)
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

package com.cobble.corridor

case class Connection(start: Code, end: Code, side: Int) {

    def isPartOfConnection(code: Code): Boolean = code == start || code == end

    def getOther(code: Code): Option[Code] = {
        if (code == start)
            Some(end)
        else if (code == end)
            Some(start)
        else
            None
    }

    val isValid: Boolean = {
        val conSideOpt: Option[Int] = start.canConnect(end)
        conSideOpt.isDefined && conSideOpt.get == side &&
        !start.isSideEmpty(conSideOpt.get) && !end.isSideEmpty(CorridorUtils.getOppositeSide(conSideOpt.get))
    }

    lazy val edgeId: String = start.checksum + "|" + end.checksum

    lazy val codeArr: Array[Code] = Array(start, end)

    lazy val isTraversable: Boolean = start.isSideOpen(side) && end.isSideOpen(CorridorUtils.getOppositeSide(side))
}

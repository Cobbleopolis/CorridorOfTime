package com.cobble.corridor

object CorridorUtils {
    def getOppositeSide(side: Int): Int = side match {
        case 0 => 3
        case 1 => 4
        case 2 => 5
        case 3 => 0
        case 4 => 1
        case 5 => 2
        case _ => -1
    }
}

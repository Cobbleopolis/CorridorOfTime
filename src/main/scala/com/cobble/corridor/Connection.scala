package com.cobble.corridor

case class Connection(start: Code, end: Code, side: Int) {

    def isPartOfConnection(code: Code): Boolean = code == start || code == end
}

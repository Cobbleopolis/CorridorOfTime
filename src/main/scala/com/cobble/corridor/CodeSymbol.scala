package com.cobble.corridor

object CodeSymbol extends Enumeration {
    type CodeSymbol = Value
    val BLANK: CodeSymbol = Value("B")
    val HEX: CodeSymbol = Value("H")
    val CLOVER: CodeSymbol = Value("C")
    val SNAKE: CodeSymbol = Value("S")
    val DIAMOND: CodeSymbol = Value("D")
    val PLUS: CodeSymbol = Value("P")
    val CAULDRON: CodeSymbol = Value("T")
    val UNKNOWN: CodeSymbol = Value("?")

    def fromFullName(fullName: String): CodeSymbol = CodeSymbol.withName(fullName.toLowerCase.head.toString)
}

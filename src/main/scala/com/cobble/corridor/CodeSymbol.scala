package com.cobble.corridor

object CodeSymbol extends Enumeration {
    type CodeSymbol = Value
    val BLANK: CodeSymbol = Value("b")
    val HEX: CodeSymbol = Value("h")
    val CLOVER: CodeSymbol = Value("c")
    val SNAKE: CodeSymbol = Value("s")
    val DIAMOND: CodeSymbol = Value("d")
    val PLUS: CodeSymbol = Value("p")
    val CAULDRON: CodeSymbol = Value("t")
    val UNKNOWN: CodeSymbol = Value("?")

    def fromFullName(fullName: String): CodeSymbol = CodeSymbol.withName(fullName.toLowerCase.head.toString)
}

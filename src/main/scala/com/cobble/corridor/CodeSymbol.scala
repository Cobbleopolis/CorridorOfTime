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

    def fromFullName(fullName: String): CodeSymbol = {
        val name: String = fullName.toUpperCase.head.toString
        if (CodeSymbol.values.exists(_.toString == name))
            CodeSymbol.withName(name)
        else
            CodeSymbol.UNKNOWN
    }
}

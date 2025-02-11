package io.johnedquinn.kanonic.machine

import de.m3y.kformat.table
import io.johnedquinn.kanonic.Grammar
import kotlin.text.StringBuilder

data class ParseTable(
    val actionTable: List<List<Action?>>,
    val goToTable: List<List<Action?>>,
    val nonTerminals: List<String>
) {
    internal fun prettify(grammar: Grammar): String {
        val table = table {
            val headers = mutableListOf<String>()
            headers.add("STATE")
            grammar.tokens.forEach { token -> headers.add(token.name) }
            nonTerminals.forEach { nonTerminal -> headers.add(nonTerminal) }
            header(headers)

            actionTable.forEachIndexed { rowIndex, actionRow ->
                val rowValues = mutableListOf<String>()
                rowValues.add(rowIndex.toString())
                actionRow.forEach { action -> rowValues.add(action?.prettify() ?: "null") }
                goToTable[rowIndex].forEach { action -> rowValues.add(action?.prettify() ?: "null") }
                row(*rowValues.toTypedArray())
            }
        }
        val builder = StringBuilder()
        table.render(builder)
        return builder.toString()
    }
}

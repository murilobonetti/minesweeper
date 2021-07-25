package model

import java.util.*
import kotlin.collections.ArrayList

class Board(val numberOfRows: Int, val numberOfColumns: Int, private val numberOfMines: Int) {
    private val fields = ArrayList<ArrayList<Field>>()
    private val callbacks = ArrayList<(BoardEvent) -> Unit>()

    init {
        generateFields()
        connectNeighbors()
        sortMines()
    }

    private fun generateFields() {
        for (row in 0 until numberOfRows) {
            fields.add(ArrayList())
            for (column in 0 until numberOfColumns) {
                val newField = Field(row, column)
                newField.onEvent(this::verifyVictoryOrDefeat)
                fields[row].add(newField)
            }
        }
    }

    private fun connectNeighbors() {
        forEachField { connectNeighbors(it) }
    }

    private fun connectNeighbors(field: Field) {
        val (row, column) = field
        val rows = arrayOf(row - 1, row, row + 1)
        val columns = arrayOf(column - 1, column, column + 1)

        rows.forEach { r ->
            columns.forEach { c ->
                val current = fields.getOrNull(r)?.getOrNull(c)
                current?.takeIf { field != it }?.let { field.addNeighbor(it) }
            }
        }
    }

    private fun sortMines() {
        val generator = Random()

        var drawnRow: Int
        var drawnColumn: Int
        var currentAmountOfMines = 0

        while (currentAmountOfMines < this.numberOfMines) {
            drawnRow = generator.nextInt(numberOfRows)
            drawnColumn = generator.nextInt(numberOfColumns)

            val drawnField = fields[drawnRow][drawnColumn]
            if (drawnField.safe) {
                drawnField.setupMine()
                currentAmountOfMines++
            }
        }
    }

    private fun goalAchieved(): Boolean {
        var playerWon = true
        forEachField {
            if (!it.goalAchieved) {
                playerWon = false
            }
        }
        return playerWon
    }

    private fun verifyVictoryOrDefeat(field: Field, event: FieldEvent) {
        if (event == FieldEvent.EXPLOSION) {
            callbacks.forEach { it(BoardEvent.DEFEAT) }
        } else if (goalAchieved()) {
            callbacks.forEach { it(BoardEvent.VICTORY) }
        }
    }

    fun forEachField(callback: (Field) -> Unit) {
        fields.forEach { row -> row.forEach(callback) }
    }

    fun onEvent(callback: (BoardEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun restart() {
        forEachField { it.restart() }
        sortMines()
    }
}
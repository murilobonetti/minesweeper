package view

import model.Board
import model.BoardEvent
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

fun main() {
    MainScreen()
}

class MainScreen : JFrame() {
    private val board = Board(numberOfRows = 16, numberOfColumns = 30, numberOfMines = 15)
    private val boardPanel = BoardPanel(board)

    init {
        board.onEvent(this::showResult)
        add(boardPanel)

        setSize(690, 438)
        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
        title = "Mine Sweeper"
        isVisible = true
    }

    private fun showResult(event: BoardEvent) {
        SwingUtilities.invokeLater {
            val msg = when (event) {
                BoardEvent.VICTORY -> "You won!"
                BoardEvent.DEFEAT -> "You lose!"
            }

            JOptionPane.showMessageDialog(this, msg)
            board.restart()

            boardPanel.repaint()
            boardPanel.validate()
        }
    }
}
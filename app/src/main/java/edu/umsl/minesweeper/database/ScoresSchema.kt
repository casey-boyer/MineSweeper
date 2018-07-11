package edu.umsl.minesweeper.database

object ScoresSchema {
    const val NAME = "scores" /*Name of the table*/
    object Cols {
        const val ID = "id" /*ID of each row*/
        const val GAME_NUMBER = "game_number" /*The high score of this game*/
        const val SCORE = "score" /*The high score*/
        const val MINUTES = "minutes"
        const val SECONDS = "seconds"
        const val MILLISECONDS = "milliseconds"
    }
}
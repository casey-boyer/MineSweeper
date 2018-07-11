package edu.umsl.minesweeper.database

object RoundSchema {
    const val NAME = "rounds" /*Name of the table*/
    object Cols {
        const val ID = "id" /*ID of each row*/
        const val ROUND_NUMBER = "round_number" /*Round of the game*/
        const val GAME_NUMBER = "game_number" /*The game number*/
        const val SCORE = "score" /*Score of this round*/
        const val MINUTES = "minutes"
        const val SECONDS = "seconds"
        const val MILLISECONDS = "milliseconds"
    }
}
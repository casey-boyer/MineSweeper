package edu.umsl.minesweeper.game.data

data class HighScore(val id: Int, val gameNumber: Int, val score: Int, val minutes: Int,
                     val seconds: Int, val milliseconds: Int)
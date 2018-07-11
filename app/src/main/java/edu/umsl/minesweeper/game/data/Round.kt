package edu.umsl.minesweeper.game.data

data class Round(val id: Int, val round_number: Int, val game_number: Int,
                 val score: Int, val minutes: Int, val seconds: Int, val milliseconds: Int)
package edu.umsl.minesweeper.game.model

import android.util.Log
import edu.umsl.minesweeper.database.ScoresPersistence
import edu.umsl.minesweeper.game.data.HighScore
import edu.umsl.minesweeper.game.data.Round

class ScoresModel(private val persistence: ScoresPersistence) {
    private var idKey: Int = 0
    private var gameNumber: Int = 1

    //Total rounds and high scores
    var rounds: List<Round> = persistence.getAllRounds()
    var highScores: List<HighScore>? = persistence.getHighScores()

    init {
        //Get the current game number
        if (persistence.getRecentHighScore() != null)
            gameNumber = persistence.getRecentHighScore()?.gameNumber!! + 1
        else
            gameNumber = 1

        //Get the primary key
        idKey = persistence.getPrimaryKey()
    }

    //Update the array referencing all rounds in the database
    fun updateRounds() {
        rounds = persistence.getAllRounds()
    }

    //Insert the round
    fun insertRound(roundNumber: Int, score: Int, minutes: Int, seconds: Int, milliseconds: Int) {
        val round = Round(idKey, roundNumber, gameNumber, score, minutes, seconds, milliseconds)

        persistence.insertRound(round)
        idKey++
    }

    //Check if a high score may be inserted
    fun insertHighScore(score: Int) {
        var totalTime = 0

        Log.e("scoresModel", "insertHighScore, score=$score")

        if (score > 0) {
            //Get the total time

            val gameRoundsArray = persistence.getRoundsAtGame(gameNumber)

            if (gameRoundsArray != null) {
                for (i in 0 until gameRoundsArray?.size) {
                    totalTime += (gameRoundsArray[i].minutes * 60000)
                    totalTime += (gameRoundsArray[i].seconds * 1000)
                    totalTime += gameRoundsArray[i].milliseconds
                }
            }

            //Get the id of the last high score inserted and insert the high score
            if (persistence.getRecentHighScore() == null) {
                val highScore = formatHighScore(0, gameNumber, score, totalTime.toLong())
                persistence.insertHighScore(highScore)
            }
            else {
                val recentHighScore: HighScore? = persistence.getRecentHighScore()

                val highScore = formatHighScore(recentHighScore!!.id + 1,
                        recentHighScore.gameNumber + 1, score, totalTime.toLong())

                Log.e("insertHighScore", "score: ${highScore.toString()}")
                Log.e("insertHighScore", "recent: ${recentHighScore.toString()}")

                //Check time as well
                if (score > recentHighScore.score)
                    insertHighScore(highScore)
                else {
                    if (highScore.minutes < recentHighScore.minutes)
                        insertHighScore(highScore)
                    else if (highScore.minutes == recentHighScore.minutes) {
                        if (highScore.seconds == recentHighScore.seconds)
                            insertHighScore(highScore)

                        else if (highScore.seconds == recentHighScore.seconds) {
                            if (highScore.milliseconds < recentHighScore.milliseconds)
                                insertHighScore(highScore)
                        }
                    }
                }
            }
        }
    }

    //Insert a high score into the database
    private fun insertHighScore(highScore: HighScore) {
        persistence.insertHighScore(highScore)
    }

    //Format the total minutes, seconds, and milliseconds for a high score
    private fun formatHighScore(id: Int, gameNumber: Int, score: Int, totalTime: Long): HighScore {
        val minutes = ( (totalTime / 1000) / 60)
        val seconds = ( (totalTime / 1000) % 60)
        val milliseconds = (totalTime % 1000)

        return HighScore(id, gameNumber, score, minutes.toInt(), seconds.toInt(), milliseconds.toInt())
    }


    fun destroy() {
        //Delete all rounds not associated with a high score,
        //update the list of rounds and high scores

        persistence.deleteRounds()
        highScores = persistence.getHighScores()
        rounds = persistence.getAllRounds()
        idKey = 0
    }
}
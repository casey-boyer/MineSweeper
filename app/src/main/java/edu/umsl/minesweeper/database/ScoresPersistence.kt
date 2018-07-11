package edu.umsl.minesweeper.database

import edu.umsl.minesweeper.game.data.Round
import edu.umsl.minesweeper.game.data.HighScore
import android.util.Log
import org.jetbrains.anko.db.*

class ScoresPersistence(private val dbHelper: DBHelper) {

    //Return all the rounds in the database
    fun getAllRounds(): List<Round> {
        val db = dbHelper.writableDatabase

        val cursor = db.query(RoundSchema.NAME, null, null, null,
                null, null, "${RoundSchema.Cols.ID} ASC")

        val roundsArray = arrayListOf<Round>()

        if (cursor.count == 0) {
            roundsArray.add(Round(0, 0, 0, 0, 0, 0, 0))
            return roundsArray
        }
        else {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.ID))
                val round_number = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.ROUND_NUMBER))
                val game_number = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.GAME_NUMBER))
                val score = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.SCORE))
                val minutes = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.MINUTES))
                val seconds = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.SECONDS))
                val milliseconds = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.MILLISECONDS))

                roundsArray.add(Round(id, round_number, game_number, score, minutes, seconds, milliseconds))
            }

            cursor.close()

            return roundsArray
        }
    }

    //Return all the high scores in the database
    fun getHighScores(): List<HighScore>? {
        val db = dbHelper.writableDatabase

        val cursor = db.query(ScoresSchema.NAME, null, null, null,
                null, null, "${ScoresSchema.Cols.ID} DESC")

        val highScoresArray = arrayListOf<HighScore>()

        Log.e("getHighScores", "cursor.count = ${cursor.count}")

        if (cursor.count == 0) {
            Log.e("getHighScores", "cursor.count == 0")
            return null
        }
        else {
        while(cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(ScoresSchema.Cols.ID))
            val game_number = cursor.getInt(cursor.getColumnIndex(ScoresSchema.Cols.GAME_NUMBER))
            val score = cursor.getInt(cursor.getColumnIndex(ScoresSchema.Cols.SCORE))
            val minutes = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.MINUTES))
            val seconds = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.SECONDS))
            val milliseconds = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.MILLISECONDS))

            highScoresArray.add(HighScore(id, game_number, score, minutes, seconds, milliseconds))
        }

        cursor.close()

        return highScoresArray
        }
    }

    //Return all the rounds associated with a specific game
    fun getRoundsAtGame(gameNumber: Int): List<Round>? {
        val db = dbHelper.writableDatabase

        Log.e("scoresPersistence", "getting rounds at game $gameNumber")

        val selectionArgs = arrayOf("$gameNumber")

        val roundsArray = arrayListOf<Round>()

        val cursor = db.query(RoundSchema.NAME, null, "${RoundSchema.Cols.GAME_NUMBER} = ?",
                selectionArgs, null, null, "${RoundSchema.Cols.ROUND_NUMBER} DESC")

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.ID))
            val round_number = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.ROUND_NUMBER))
            val game_number = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.GAME_NUMBER))
            val score = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.SCORE))
            val minutes = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.MINUTES))
            val seconds = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.SECONDS))
            val milliseconds = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.MILLISECONDS))

            roundsArray.add(Round(id, round_number, game_number, score, minutes, seconds, milliseconds))
        }

        cursor.close()

        if (roundsArray.isEmpty())
            return null
        else
            return roundsArray
    }

    //Return the most recent high score
    fun getRecentHighScore(): HighScore? {
        val db = dbHelper.writableDatabase

        //Get the most recently inserted record
        val cursor = db.query(ScoresSchema.NAME, null, null, null, null, null,
                "${ScoresSchema.Cols.ID} DESC", "1")

        var highScore: HighScore? = null

        while(cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(ScoresSchema.Cols.ID))
            val game_number = cursor.getInt(cursor.getColumnIndex(ScoresSchema.Cols.GAME_NUMBER))
            val score = cursor.getInt(cursor.getColumnIndex(ScoresSchema.Cols.SCORE))
            val minutes = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.MINUTES))
            val seconds = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.SECONDS))
            val milliseconds = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.MILLISECONDS))

            highScore = HighScore(id, game_number, score, minutes, seconds, milliseconds)
        }

        cursor.close()

        return highScore
    }

    //Return the current primary key of the rounds table
    fun getPrimaryKey(): Int {
        val db = dbHelper.writableDatabase

        val cursor = db.query(RoundSchema.NAME, null, null, null, null, null,
                "${RoundSchema.Cols.ID} DESC", "1")

        var primaryKey = 0

        if (cursor.count == 0) {
            cursor.close()
            return primaryKey
        }
        else {
            while (cursor.moveToNext()) {
                primaryKey = cursor.getInt(cursor.getColumnIndex(RoundSchema.Cols.ID))
            }

            cursor.close()

            return (primaryKey + 1)
        }
    }

    //Insert a round into the database
    fun insertRound(round: Round) {
        dbHelper.use {
            beginTransaction()

            insert(RoundSchema.NAME,
                    RoundSchema.Cols.ID to round.id,
                    RoundSchema.Cols.SCORE to round.score,
                    RoundSchema.Cols.GAME_NUMBER to round.game_number,
                    RoundSchema.Cols.ROUND_NUMBER to round.round_number,
                    RoundSchema.Cols.MINUTES to round.minutes,
                    RoundSchema.Cols.SECONDS to round.seconds,
                    RoundSchema.Cols.MILLISECONDS to round.milliseconds
                    )

            setTransactionSuccessful()
            endTransaction()
        }
    }

    //Insert a high score into the database
    fun insertHighScore(highScore: HighScore) {
        Log.e("insertHighScore", "inserting high score")
        dbHelper.use {
            beginTransaction()

            insert(ScoresSchema.NAME,
                    ScoresSchema.Cols.ID to highScore.id,
                    ScoresSchema.Cols.GAME_NUMBER to highScore.gameNumber,
                    ScoresSchema.Cols.SCORE to highScore.score,
                    ScoresSchema.Cols.MINUTES to highScore.minutes,
                    ScoresSchema.Cols.SECONDS to highScore.seconds,
                    ScoresSchema.Cols.MILLISECONDS to highScore.milliseconds
                    )

            setTransactionSuccessful()
            endTransaction()
        }
    }

    //Delete all the rounds in the database
    fun deleteRounds() {
        dbHelper.use {
            beginTransaction()

            delete(RoundSchema.NAME, "1", null)
            setTransactionSuccessful()
            endTransaction()
        }
    }

    //Delete all high scores in the database
    fun deleteAllHighScores() {
        dbHelper.use {
            beginTransaction()

            delete(ScoresSchema.NAME, "1", null)

            setTransactionSuccessful()
            endTransaction()
        }
    }

}
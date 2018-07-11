package edu.umsl.minesweeper.database


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DBHelper(context: Context): ManagedSQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    //Create the high scores and rounds tables
    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(RoundSchema.NAME, true,
                RoundSchema.Cols.ID to INTEGER + PRIMARY_KEY,
                RoundSchema.Cols.ROUND_NUMBER to INTEGER,
                RoundSchema.Cols.GAME_NUMBER to INTEGER,
                RoundSchema.Cols.SCORE to INTEGER,
                RoundSchema.Cols.MILLISECONDS to INTEGER,
                RoundSchema.Cols.SECONDS to INTEGER,
                RoundSchema.Cols.MINUTES to INTEGER
        )

        db?.createTable(ScoresSchema.NAME, true,
                ScoresSchema.Cols.ID to INTEGER + PRIMARY_KEY,
                ScoresSchema.Cols.GAME_NUMBER to INTEGER,
                ScoresSchema.Cols.SCORE to INTEGER,
                ScoresSchema.Cols.MILLISECONDS to INTEGER,
                ScoresSchema.Cols.SECONDS to INTEGER,
                ScoresSchema.Cols.MINUTES to INTEGER)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        const val DB_NAME = "MinesweeperDB.db"
        const val DB_VERSION = 1
    }
}
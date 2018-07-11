package edu.umsl.minesweeper.game.model

import android.util.Log
import edu.umsl.minesweeper.game.data.TilePosition
import java.util.*

class MinesweeperModel {
    private val rows = 6
    private val columns = 6
    private val minMines = 5
    private val maxMines = 10
    private val gameOverDelay: Long = 1500
    private var mines: Int = 0
    private var score: Int = 0
    private var round: Int = 1
    private var minePositions = arrayListOf<TilePosition>()
    private var freePositions = arrayListOf<TilePosition>()
    private var userInput = arrayListOf<Int>()
    private var gameStarted: Boolean = false
    private var gameOver: Boolean = false
    private var randomGenerator = Random()
    var minesweeperBoard = Array(rows) { IntArray(columns) }

    private fun generateMinePositions() {
        var randomOuterSubscript = 0
        var randomInnerSubscript = 0


        for (i in 0 until mines) {
            //Generate the position of the mine in the game board
            randomOuterSubscript = randomGenerator.nextInt(rows)
            randomInnerSubscript = randomGenerator.nextInt(rows)

            while (randomInnerSubscript == 0 && randomOuterSubscript == 0) {
                randomOuterSubscript = randomGenerator.nextInt(rows)
                randomInnerSubscript = randomGenerator.nextInt(rows)
            }

            //If this position is already present, continue trying to generate a unique position
            while (minePositions.contains(TilePosition(randomOuterSubscript, randomInnerSubscript))) {
                randomOuterSubscript = randomGenerator.nextInt(rows)
                randomInnerSubscript = randomGenerator.nextInt(rows)
            }

            //Add this position to the minePositions array
            minePositions.add(TilePosition(randomOuterSubscript, randomInnerSubscript))

            //Place the mine in the position
            minesweeperBoard[randomOuterSubscript][randomInnerSubscript] = -1

            Log.e("minesweeper", "bombPositions[$i]=${minePositions[i]}")
        }

    }

    //Free space is 0, number > 0 indicates mine near it, -1 is a bomb space
    fun createBoard() {
        //Randomly generate amount of mines from 5-10
        mines = randomGenerator.nextInt((maxMines - minMines) + 1) + minMines

        //Counter to identify adjacent mines in a specific position
        var mineCount = 0

        //Generate the mine positions
        generateMinePositions()

        for (i in 0 until rows) {
            for (j in 0 until columns) {
                mineCount = 0
                //Look for any mines near this space
                if (minesweeperBoard[i][j] != -1) {
                    //Look for any mines to the left
                    if (j > 0) {
                        if (minesweeperBoard[i][j - 1] == -1)
                            mineCount++
                    }
                    //Look for any mines to the right
                    if (j < (columns - 1)) {
                        if (minesweeperBoard[i][j + 1] == -1)
                            mineCount++
                    }
                    //Look for any mines to the top
                    if (i > 0) {
                        if (minesweeperBoard[i - 1][j] == -1)
                            mineCount++
                    }
                    //Look for any mines to the bottom
                    if (i < (rows - 1)) {
                        if (minesweeperBoard[i + 1][j] == -1)
                            mineCount++
                    }
                    //Look for any mines to the top left
                    if (i > 0 && j > 0) {
                        if (minesweeperBoard[i - 1][j - 1] == -1)
                            mineCount++
                    }
                    //Look for any mines to the top right
                    if (i > 0 && (j < (columns - 1))) {
                        if (minesweeperBoard[i - 1][j + 1] == -1)
                            mineCount++
                    }
                    //Look for any mines to the bottom left
                    if ((i < (rows - 1)) && (j > 0)) {
                        if (minesweeperBoard[i + 1][j - 1] == -1)
                            mineCount++
                    }
                    //Look for any mines to the bottom right
                    if ((i < (rows - 1)) && (j < (columns - 1))) {
                        if (minesweeperBoard[i + 1][j + 1] == -1)
                            mineCount++
                    }

                    minesweeperBoard[i][j] = mineCount
                }
            }
        }

        //Display the board in the log
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                Log.e("minesweeperMod", "minesweeperBoard$i$j = ${minesweeperBoard[i][j]}")
            }
        }

        //Initialize the freePositions array
        setFreePositions()
    }

    //Initialize the position of each free space
    private fun setFreePositions() {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                if (minesweeperBoard[i][j] == 0) {
                    freePositions.add(TilePosition(i, j))
                }
            }
        }

        Log.e("setFreePos", "freePositions.size=${freePositions.size}")
    }

    fun isGameOver(): Boolean {
        return gameOver
    }

    fun isGameStarted(): Boolean {
        return gameStarted
    }

    fun getGameDelay(): Long {
        return gameOverDelay
    }

    fun startGame() {
        gameStarted = true
    }

    //Test if the user has cleared all non-mine pieces of the board, or if the
    //user has pressed a mine.
    fun isGameEnded(): Boolean {
        Log.e("isGameEnded", "userInput.size=${userInput.size}, minePositions.size=${minePositions.size}")

        if (!gameStarted) //If game is not started
            return true
        else if ( ((userInput.size + minePositions.size) == 36) && !gameOver) {
            //If the user has clicked all spaces not containing a mine, then the
            //game has ended but they won the round
            gameStarted = false
            return true
        }
        else if(gameOver) {
            gameStarted = false
            return true
        }
        else
            return false
    }

    fun updateScore() {
        score++
    }

    fun updateRound() {
        //If the game has ended and it is not game over,
        //increment the round
        if (isGameEnded() && !isGameOver())
            round++
    }

    //Reset the board for a new game
    fun resetBoard() {
        for (i in 0 until rows) {
            for (j in 0 until columns)
                minesweeperBoard[i][j] = 0
        }

        userInput.clear()
        minePositions.clear()
        freePositions.clear()

        if (gameOver) {
            score = 0
            round = 1
        }

        gameStarted = false
        gameOver = false

        createBoard()
    }

    //Get the user's input thus far
    fun getInput(): List<Int> {
        return userInput.toList()
    }

    //Return all of the free spaces on the board
    fun getFreeSpaces(): List<Int> {
        val freeSpacePos = arrayListOf<Int>()

        for (i in 0 until freePositions.size) {
            freeSpacePos.add((freePositions[i].outerSubscript * rows) + freePositions[i].innerSubscript)
        }

        userInput.addAll(freeSpacePos)
        return freeSpacePos.toList()
    }

    //Return all of the mine spaces on the board
    fun getMineSpaces(): List<Int> {
        val mineSpacePos = arrayListOf<Int>()

        for (i in 0 until minePositions.size) {
            mineSpacePos.add((minePositions[i].outerSubscript * rows) + minePositions[i].innerSubscript)
        }

        userInput.addAll(mineSpacePos)

        return mineSpacePos.toList()
    }

    fun getScore(): Int {
        return score
    }

    fun getRound(): Int {
        return round
    }

    //Update the user's input.
    fun updateInput(position: Int) {
        if (getPosition(position) > 0)
            userInput.add(position)
    }

    //Method to get the position of a grid cell
    //Use this to test user's input and subsequently reveal cells
    fun getPosition(position: Int): Int {
        var value = 0

        for (i in 0 until rows) {
           for (j in 0 until columns) {
               if (position == (i * rows) + j) {
                   value = minesweeperBoard[i][j]
               }
           }
        }

        if (value == -1)
            gameOver = true

        return value
    }

    //Get the positions of the board as incremental values used by the recyclerView
    fun getPositionsList(): List<Int> {
        val listOfNum = arrayListOf<Int>()

        for (i in 0 until 36) {
            listOfNum.add(i)
        }

        return listOfNum.toList()
    }
}
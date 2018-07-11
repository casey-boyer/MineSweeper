package edu.umsl.minesweeper.game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import edu.umsl.minesweeper.ClassHolder
import edu.umsl.minesweeper.R
import edu.umsl.minesweeper.database.DBHelper
import edu.umsl.minesweeper.database.ScoresPersistence
import edu.umsl.minesweeper.game.model.MinesweeperModel
import edu.umsl.minesweeper.game.model.ScoresModel
import edu.umsl.minesweeper.game.model.TimerModel
import edu.umsl.minesweeper.game.views.GameDataViewFragment
import edu.umsl.minesweeper.game.views.MinesweeperFragment
import kotlinx.android.synthetic.main.fragment_minesweeper.*

class MinesweeperActivity : Activity(), GameDataViewFragment.GameDataViewListener {

    private var minesweeperFragment: MinesweeperFragment? = null
    private var gameDataViewFragment: GameDataViewFragment? = null
    private var timerFragment: TimerFragment? = null
    private var minesweeperModel: MinesweeperModel? = null
    private var timerModel: TimerModel? = null
    private var scoresModel: ScoresModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minesweeper)

        val dbHelper = DBHelper(this@MinesweeperActivity)
        val persistence = ScoresPersistence(dbHelper)

        if (savedInstanceState != null) {
            //If the screen was rotated, get the models and fragments

            minesweeperModel = ClassHolder.instance.getClass("minesweeperModel") as? MinesweeperModel
            timerModel = ClassHolder.instance.getClass("timerModel") as? TimerModel
            scoresModel = ClassHolder.instance.getClass("scoresModel") as? ScoresModel

            if (minesweeperModel == null) {
                minesweeperModel = MinesweeperModel()
            }

            if (timerModel == null) {
                timerModel = TimerModel()
            }

            if (scoresModel == null) {
                scoresModel = ScoresModel(persistence)
            }

            gameDataViewFragment = fragmentManager
                    .getFragment(savedInstanceState, "gameDataViewFragment") as? GameDataViewFragment

            if (gameDataViewFragment != null) {
                Log.e("onCreate", "gameDataViewFrag is not null")
                if (gameDataViewFragment?.isAdded!!)
                    fragmentManager.beginTransaction().show(gameDataViewFragment).commit()
            }

            if (savedInstanceState.get("minesweeperFragment") != null) {
                minesweeperFragment = fragmentManager
                        .getFragment(savedInstanceState, "minesweeperFragment") as? MinesweeperFragment
            }

            if (minesweeperFragment != null) {
                if (minesweeperFragment?.isAdded!!)
                    fragmentManager.beginTransaction().show(minesweeperFragment).commit()
            }
        }
        else {
            //Otherwise, intialize the models and fragments

            minesweeperModel = MinesweeperModel()
            timerModel = TimerModel()
            scoresModel = ScoresModel(persistence)
            scoresModel?.destroy()

            minesweeperFragment = fragmentManager.findFragmentById(R.id.fragmentContainer) as? MinesweeperFragment

            if (minesweeperFragment == null) {
                minesweeperFragment = MinesweeperFragment()

                fragmentManager.beginTransaction()
                        .replace(R.id.recyclerMinesweeper, minesweeperFragment)
                        .commit()

                minesweeperFragment?.listener = object : MinesweeperFragment.MinesweeperListener {
                    override var positionList: List<Int> = minesweeperModel!!.getPositionsList()
                        get() = minesweeperModel!!.getPositionsList()

                    override fun selectedItemAtPosition(position: Int): Int {
                        //Update the user's input thus far
                        minesweeperModel!!.updateInput(position)

                        if (minesweeperModel?.isGameEnded()!!) {
                            timerFragment?.stopTimer()

                            //If the game is not over, update the user's score, insert the round into
                            //the database, and update the view so the user may play again
                            if (!minesweeperModel!!.isGameOver()) {
                                minesweeperModel?.updateScore()

                                scoresModel?.insertRound(minesweeperModel?.getRound()!!,
                                        minesweeperModel!!.getScore(),
                                        timerModel?.getMinutes()!!,
                                        timerModel?.getSeconds()!!,
                                        timerModel?.getMilliseconds()!!)

                                minesweeperModel?.updateRound()

                                gameDataViewFragment?.updateScore(minesweeperModel?.getScore(),
                                        minesweeperModel?.getRound())
                                gameDataViewFragment?.isGameEnded(minesweeperModel?.isGameOver()!!)

                                scoresModel?.updateRounds()
                            }
                            else {
                                //Otherwise, insert the round and high score and go the
                                //game over screen
                                scoresModel?.insertRound(minesweeperModel?.getRound()!!,
                                        minesweeperModel!!.getScore(),
                                        timerModel?.getMinutes()!!,
                                        timerModel?.getSeconds()!!,
                                        timerModel?.getMilliseconds()!!)

                                scoresModel?.insertHighScore(minesweeperModel?.getScore()!!)

                                val handler = Handler()
                                handler.postDelayed({
                                    startActivityForResult(Intent(this@MinesweeperActivity,
                                            GameOverActivity::class.java), 0)
                                }, minesweeperModel?.getGameDelay()!!)
                            }
                        }

                        return minesweeperModel!!.getPosition(position)
                    }

                    //If the game is over, the user should not continue playing/clicking
                    //the board for response
                    override fun isBoardUnclickable(): Boolean {
                        return minesweeperModel!!.isGameEnded()
                    }

                    //Get the value of the space at the position
                    override fun getSpace(position: Int): Int {
                        return minesweeperModel!!.getPosition(position)
                    }

                    //Get all free spaces
                    override fun clearFreeSpaces(): List<Int> {
                        return minesweeperModel!!.getFreeSpaces()
                    }

                    //Get all mine spaces
                    override fun showMines(): List<Int> {
                        return minesweeperModel!!.getMineSpaces()
                    }

                    //Get the user's input
                    override fun getAllPositions(): List<Int> {
                        return minesweeperModel!!.getInput()
                    }
                }
            }

            gameDataViewFragment = fragmentManager.findFragmentById(R.id.fragmentContainer) as? GameDataViewFragment

            if (gameDataViewFragment == null) {
                gameDataViewFragment = GameDataViewFragment()

                fragmentManager.beginTransaction().replace(R.id.gameDataLayout, gameDataViewFragment).commit()
            }

            timerFragment = fragmentManager.findFragmentByTag(TimerFragment.TAG) as? TimerFragment

            if (timerFragment == null) {
                timerFragment = TimerFragment()

                fragmentManager.beginTransaction().add(timerFragment, TimerFragment.TAG).commit()
            }

            gameDataViewFragment?.delegate = this
            timerFragment?.timerFragmentListener = timerFragmentListener

            minesweeperModel?.createBoard()
        }
    }

    //During a configuration chage, save the models and the adapter of
    //the recyclerView
    override fun onRetainNonConfigurationInstance(): Any {
        val classHolder = ClassHolder

        ClassHolder.instance.saveClass("minesweeperModel", minesweeperModel)
        ClassHolder.instance.saveClass("timerModel", timerModel)

        ClassHolder.instance.saveClass("adapter", minesweeperFragmentView?.adapter)

        return classHolder
    }

    //Save the fragments during a configuration change
    override fun onSaveInstanceState(outState: Bundle?) {
        fragmentManager.putFragment(outState, "minesweeperFragment", minesweeperFragment)
        fragmentManager.putFragment(outState, "gameDataViewFragment", gameDataViewFragment)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            scoresModel = null
            minesweeperModel = null
            timerModel = null

            //Destroy the rounds
            scoresModel?.destroy()

            timerFragment = null
            minesweeperFragment = null
            gameDataViewFragment = null
        }
    }

    //If the user wants to play another round after visiting the game over activity,
    //restart this activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intent = this.intent
        finish()
        startActivity(intent)
    }

    private val timerFragmentListener = object: TimerFragment.TimerFragmentListener {
        //Update the current time and view of the current time
        override fun timeElapsed(elapsedTime: Long) {
            timerModel?.updateTime(elapsedTime)
            gameDataViewFragment?.updateTime(timerModel?.getTimeStamp()!!)
        }
    }

    override fun gameStatus(): Boolean {
        return minesweeperModel!!.isGameOver()
    }

    //Start the game and timer
    override fun startGame() {
        minesweeperModel?.startGame()
        timerFragment?.startTimer(timerModel!!.getStartTime())
    }

    override fun quitGame(isQuiting: Boolean) {
        if (isQuiting) {
            //If the user is quiting and has already started the game,
            //stop the timer
            if (minesweeperModel?.isGameStarted()!!) {
                timerFragment?.stopTimer()

                //If the user is quiting and it is not game over, insert this round
                //to record the time taken for this round
                if (!minesweeperModel?.isGameOver()!!) {
                    scoresModel?.insertRound(minesweeperModel?.getRound()!!,
                            minesweeperModel?.getScore()!!,
                            timerModel?.getMinutes()!!,
                            timerModel?.getSeconds()!!,
                            timerModel?.getMilliseconds()!!)
                }
            }

            //Insert the high score
            scoresModel?.insertHighScore(minesweeperModel?.getScore()!!)

            //Start the game over activity
            startActivityForResult(Intent(this, GameOverActivity::class.java),
                    0)
        }
        else //If the user chooses not to quit, start the timer
            timerFragment?.startTimer(timerModel!!.getStartTime())
    }

    override fun pauseGame() {
        timerFragment?.stopTimer()
    }

    override fun newGame() {
        minesweeperFragment?.isStarting(minesweeperModel!!.isGameEnded())

        //Insert high score, reset the timer, update the time view
        if (minesweeperModel?.isGameOver()!!) {
            timerFragment?.resetTimer()
            timerModel?.reset()
            gameDataViewFragment?.updateTime(timerModel?.getTimeStamp())
            scoresModel?.insertHighScore(minesweeperModel?.getScore()!!)
        }
        else {
            timerFragment?.resetTimer()
            timerModel?.reset()
            gameDataViewFragment?.updateTime(timerModel?.getTimeStamp())
        }

        //Reset the board
        minesweeperModel?.resetBoard()

        //Redraw the tiles
        minesweeperFragmentView?.adapter?.notifyDataSetChanged()
    }

    override fun updateScore(): Int? {
        return minesweeperModel?.getScore()
    }

    override fun updateRound(): Int? {
        return minesweeperModel?.getRound()
    }

    override fun updateTimer(): String? {
        return timerModel?.getTimeStamp()
    }
}

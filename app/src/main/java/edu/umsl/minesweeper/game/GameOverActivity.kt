package edu.umsl.minesweeper.game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import edu.umsl.minesweeper.ClassHolder
import edu.umsl.minesweeper.R
import edu.umsl.minesweeper.database.DBHelper
import edu.umsl.minesweeper.database.ScoresPersistence
import edu.umsl.minesweeper.game.data.HighScore
import edu.umsl.minesweeper.game.data.Round
import edu.umsl.minesweeper.game.model.ScoresModel
import edu.umsl.minesweeper.game.views.GameOverFragment
import edu.umsl.minesweeper.game.views.HighScoresFragment
import kotlinx.android.synthetic.main.fragment_high_scores_listing.*

class GameOverActivity : Activity(), GameOverFragment.GameOverFragmentListener {

    private var scoresModel: ScoresModel? = null
    private var gameOverFragment: GameOverFragment? = null
    private var highScoresFragment: HighScoresFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val dbHelper = DBHelper(this@GameOverActivity)
        val persistence = ScoresPersistence(dbHelper)

        //For screen-configuration changes
        if (savedInstanceState != null) {
            scoresModel = ClassHolder.instance.getClass("scoresModel") as? ScoresModel

            if (scoresModel == null) {
                scoresModel = ScoresModel(persistence)
            }

            gameOverFragment = fragmentManager
                    .getFragment(savedInstanceState, "gameOverFragment") as? GameOverFragment

            if (gameOverFragment != null) {
                if (gameOverFragment?.isAdded!!) {
                    fragmentManager.beginTransaction().show(gameOverFragment).commit()
                }
            }

            highScoresFragment = fragmentManager
                    .getFragment(savedInstanceState, "highScoresFragment") as? HighScoresFragment

            if (highScoresFragment != null) {
                if (highScoresFragment?.isAdded!!) {
                    fragmentManager.beginTransaction().show(highScoresFragment).commit()
                }
            }
        }
        else {
            scoresModel = ScoresModel(persistence)

            gameOverFragment = fragmentManager.findFragmentById(R.id.game_over_fragment_container) as? GameOverFragment

            if (gameOverFragment == null) {
                gameOverFragment = GameOverFragment()

                fragmentManager.beginTransaction().replace(R.id.gameOverLayout, gameOverFragment).commit()
            }

            highScoresFragment = fragmentManager.findFragmentById(R.id.game_over_fragment_container) as? HighScoresFragment

            if (highScoresFragment == null) {
                highScoresFragment = HighScoresFragment()

                fragmentManager.beginTransaction().replace(R.id.highScoresLayout, highScoresFragment).commit()

                //The recylerView will display the list of high scores
                highScoresFragment?.listener = object : HighScoresFragment.HighScoresListener {
                    override var highScores: List<HighScore> = scoresModel?.highScores ?: listOf()
                        get() = scoresModel?.highScores ?: listOf()

                    override fun selectedItemAtPosition(position: Int) {
                        Log.e("postion=$position", "${scoresModel?.highScores?.get(position)}")
                    }
                }
            }

            gameOverFragment?.gameOverListener = this
        }
    }

    //Save the scoresModel and adapter for configuration changes
    override fun onRetainNonConfigurationInstance(): Any {
        val classHolder = ClassHolder

        ClassHolder.instance.saveClass("scoresModel", scoresModel)

        ClassHolder.instance.saveClass("adapter", highScoresListingFragmentView?.adapter)

        return classHolder
    }

    //Save the fragments for configuration changes
    override fun onSaveInstanceState(outState: Bundle?) {
        fragmentManager.putFragment(outState, "gameOverFragment", gameOverFragment)
        fragmentManager.putFragment(outState, "highScoresFragment", highScoresFragment)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()

        //If the screen is being destroyed, remove the rounds not associated with a
        //high score
        if (isFinishing) {
            scoresModel?.destroy()
            scoresModel = null
            highScoresFragment = null
            gameOverFragment = null
        }
    }

    override fun playAgain() {
        //Remove rounds that are not associated with a high score
        scoresModel?.destroy()

        //Return to the minesweeper activity
        this.setResult(0, Intent())
        this.finish()
    }

    //Return the most recent round
    override fun getGameStats(): Round? {
        return scoresModel?.rounds?.get(scoresModel?.rounds?.lastIndex!!)
    }

    //Return if high scores are present
    override fun initializeText(): Boolean {
        if (scoresModel?.highScores == null)
            return false
        else
            return true
    }
}

package edu.umsl.minesweeper.game.views


import android.os.Bundle
import android.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils

import edu.umsl.minesweeper.R
import edu.umsl.minesweeper.game.data.Round
import kotlinx.android.synthetic.main.fragment_game_over.*

class GameOverFragment : Fragment() {

    var gameOverListener: GameOverFragmentListener? = null

    interface GameOverFragmentListener {
        fun getGameStats(): Round? //Get the score and time for most recent round
        fun playAgain() //Play another game
        fun initializeText(): Boolean //Initialize the textviews
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        retainInstance = true
        return inflater!!.inflate(R.layout.fragment_game_over, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindButton()

        if (savedInstanceState == null)
            animateGameOver()

        setScoreAndTimeText(gameOverListener?.getGameStats())
        setTextViews(gameOverListener?.initializeText()!!)
    }

    private fun bindButton() {
        playAgainButton.setOnClickListener {
            gameOverListener?.playAgain()
        }
    }

    //Animate the gameOver textView
    private fun animateGameOver() {
        gameOverTextView.bringToFront()

        gameOverTextView.elevation = 1000f

        val gameOverAnim = AnimationSet(true)
        gameOverAnim.addAnimation(AnimationUtils.loadAnimation(activity, R.anim.animate_game_over))
        gameOverAnim.addAnimation(AnimationUtils.loadAnimation(activity, R.anim.animate_scale_game_over))

        gameOverTextView.startAnimation(gameOverAnim)
    }

    private fun setScoreAndTimeText(stats: Round?) {
        gameScoreTextView.text = resources.getString(R.string.game_score_text,
                stats?.score, stats?.minutes, stats?.seconds, stats?.milliseconds)
    }

    //If there are no high scores, display a textView indicating so
    private fun setTextViews(isHighScores: Boolean) {
        if (!isHighScores) {
            gameNumberTextView.visibility = View.INVISIBLE
            gameScoreHeader.visibility = View.INVISIBLE
            gameTimeHeader.visibility = View.INVISIBLE

            noHighScoresTextView.visibility = View.VISIBLE
        }
    }

}

package edu.umsl.minesweeper.game.views

import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.umsl.minesweeper.R
import kotlinx.android.synthetic.main.fragment_game_data_view.*
import org.jetbrains.anko.alert


class GameDataViewFragment : Fragment() {

    var delegate: GameDataViewListener? = null

    interface GameDataViewListener {
        fun gameStatus(): Boolean

        fun startGame() //Start button
        fun pauseGame() //When the user clicks quit
        fun newGame() //When the user selects to play again
        fun quitGame(isQuiting: Boolean) //If the user is quiting the game
        fun updateTimer(): String?
        fun updateScore(): Int?
        fun updateRound(): Int?
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        retainInstance = true
        return inflater?.inflate(R.layout.fragment_game_data_view, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        //Save the state of the view during screen configuration changes
        if (quitButton.visibility == View.VISIBLE) {
            outState?.putString("quitButton", "true")
        }
        else
            outState?.putString("quitButton", "false")

        if (startButton.visibility == View.VISIBLE)
            outState?.putString("startButton", "true")
        else
            outState?.putString("startButton", "false")

        if (continueButton.visibility == View.VISIBLE) {
            outState?.putString("continueButton", "true")
            outState?.putString("continueText", "${continueButton.text}")
        }
        else
            outState?.putString("continueButton", "false")
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindButtons()
        updateTime(delegate?.updateTimer())
        updateScore(delegate?.updateScore(), delegate?.updateRound())

        //If a screen configuration took place, update the view accordingly
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("quitButton") == "true") {
                quitButton.visibility = View.VISIBLE
                quitButton.isClickable = true
            }
            else {
                quitButton.visibility = View.INVISIBLE
            }

            if (savedInstanceState.getString("startButton") == "true") {
                startButton.visibility = View.VISIBLE
                startButton.isClickable = true
            }
            else {
                startButton.visibility = View.INVISIBLE
            }

            if (savedInstanceState.getString("continueButton") == "true") {
                continueButton.visibility = View.VISIBLE
                continueButton.text = savedInstanceState.getString("continueText")
                continueButton.isClickable = true
            }
            else {
                continueButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun bindButtons() {
        quitButton.setOnClickListener{
            delegate?.pauseGame() //Pause the timer

            //Alert the user
            alert(resources.getString(R.string.quit_alert)) {
                positiveButton(resources.getString(R.string.quit_alert_end)) {
                    delegate?.quitGame(true)
                }
                negativeButton(resources.getString(R.string.quit_alert_resume)) {
                    delegate?.quitGame(false)
                }
            }.show()
        }

        startButton.setOnClickListener {
            delegate?.startGame() //Start the game

            startButton.visibility = View.INVISIBLE
            quitButton.visibility = View.VISIBLE
            quitButton.isClickable = true
        }

        continueButton.setOnClickListener{
            continueButton.visibility = View.INVISIBLE
            startButton.visibility = View.VISIBLE
            delegate?.newGame() //Play another game
        }
    }

    //Display the continue button only when the user wins a round
    fun isGameEnded(gameOver: Boolean) {
        if (!gameOver) {
            continueButton.visibility = View.VISIBLE
            quitButton.visibility = View.VISIBLE
            continueButton.text = resources.getString(R.string.continue_text)
        }
    }

    fun updateTime(currentTime: String?) {
        timerTextView.text = currentTime
    }

    fun updateScore(currentScore: Int?, currentRound: Int?) {
        currentScoreTextView.text = resources.getString(R.string.current_score_text, currentScore, currentRound)
    }
}

package edu.umsl.minesweeper.game.views


import android.os.Bundle
import android.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import edu.umsl.minesweeper.ClassHolder
import edu.umsl.minesweeper.R
import edu.umsl.minesweeper.game.data.HighScore
import kotlinx.android.synthetic.main.fragment_high_scores_listing.*

class HighScoresFragment : Fragment() {
    private var recyclerAdapter: HighScoresAdapter? = null

    var listener: HighScoresListener? = null

    interface HighScoresListener {
        //The array list of high scores
        var highScores: List<HighScore>

        fun selectedItemAtPosition(position: Int)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        retainInstance = true
        return inflater!!.inflate(R.layout.fragment_high_scores_listing, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Check if the adapter for the recyclerview already exists
        recyclerAdapter = ClassHolder.instance.getClass("adapter") as? HighScoresAdapter

        //Initialize the recyclerview
        if (savedInstanceState == null) {
            highScoresListingFragmentView.apply {
                layoutManager = LinearLayoutManager(activity)
                recyclerAdapter = HighScoresAdapter()
                adapter = recyclerAdapter
                setHasFixedSize(true)
            }
        }
        else {
            highScoresListingFragmentView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = recyclerAdapter
                setHasFixedSize(true)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        //Save the adapter during configuration changes
        ClassHolder.instance.saveClass("adapter", recyclerAdapter)
    }

    inner class HighScoresAdapter: RecyclerView.Adapter<ScoresHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ScoresHolder {
            val inflater = LayoutInflater.from(activity)
            val view: View = inflater.inflate(R.layout.high_scores_item_layout, parent, false)

            return ScoresHolder(view)
        }

        override fun getItemCount(): Int {
            return listener?.highScores?.size ?: 0
        }

        override fun onBindViewHolder(holder: ScoresHolder?, position: Int) {
            holder?.bindScore(listener?.highScores?.get(position))
        }
    }

    inner class ScoresHolder(view: View?): RecyclerView.ViewHolder(view), View.OnClickListener {
        private val gameNumberView: TextView? = view?.findViewById(R.id.gameNumber)
        private val scoreView: TextView? = view?.findViewById(R.id.score)
        private val timeView: TextView? = view?.findViewById(R.id.time)

        init {
            view?.setOnClickListener(this)
        }

        //For each high score, display the game number, score, and time associated with it
        fun bindScore(highScore: HighScore?) {
            gameNumberView?.text = resources.getString(R.string.game_number, highScore?.gameNumber)
            scoreView?.text = resources.getString(R.string.high_score, highScore?.score)
            timeView?.text = resources.getString(R.string.timestamp,
                    highScore?.minutes, highScore?.seconds, highScore?.milliseconds)
        }

        override fun onClick(v: View) {
            Log.e("onClick", "v")
            listener?.selectedItemAtPosition(adapterPosition)
        }
    }
}
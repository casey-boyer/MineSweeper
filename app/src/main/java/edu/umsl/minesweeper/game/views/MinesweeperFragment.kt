package edu.umsl.minesweeper.game.views


import android.animation.*
import android.os.Bundle
import android.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.umsl.minesweeper.ClassHolder
import edu.umsl.minesweeper.R
import kotlinx.android.synthetic.main.fragment_minesweeper.*

class MinesweeperFragment : Fragment() {

    private var recyclerAdapter: MinesweeperAdapter? = null

    var listener: MinesweeperListener? = null

    interface MinesweeperListener {
        var positionList: List<Int>

        fun isBoardUnclickable(): Boolean //If the game is over or not
        fun selectedItemAtPosition(position: Int): Int
        fun getSpace(position: Int): Int //Get the value of the space at this position
        fun clearFreeSpaces(): List<Int> //Get all free spaces
        fun showMines(): List<Int> //Get all mine spaces
        fun getAllPositions(): List<Int> //The user input (user clicked positions thus far)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        return inflater?.inflate(R.layout.fragment_minesweeper, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerAdapter = ClassHolder.instance.getClass("adapter") as? MinesweeperAdapter

        if (savedInstanceState == null) {
            minesweeperFragmentView?.apply {
                //spanCount is the number of columns in the grid
                layoutManager = GridLayoutManager(activity, 6)
                recyclerAdapter = MinesweeperAdapter()
                adapter = recyclerAdapter
                setHasFixedSize(true)
            }
        }
        else {
            //Screen orientation took place
            recyclerAdapter?.isStarting = true
            recyclerAdapter?.isScreenChange = true

            minesweeperFragmentView?.apply {
                layoutManager = GridLayoutManager(activity, 6)
                adapter = recyclerAdapter
                setHasFixedSize(true)
            }

        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        //Save the adapter during screen configuration changes
        ClassHolder.instance.saveClass("adapter", recyclerAdapter)
    }

    override fun onDestroy() {
        recyclerAdapter = null

        super.onDestroy()
    }

    fun isStarting(gameStatus: Boolean) {
        if (gameStatus)
            recyclerAdapter?.isStarting = true
    }

    inner class MinesweeperAdapter: RecyclerView.Adapter<MinesweeperHolder>() {

        var isStarting: Boolean = true //If the game has begun
        var isScreenChange: Boolean = false //If the screen was rotated

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MinesweeperHolder {
            val inflater = LayoutInflater.from(activity)
            val view: View

            //Display the correct layout for portrait and landscape
            if (resources.displayMetrics.widthPixels > resources.displayMetrics.heightPixels) {
                view = inflater.inflate(R.layout.minesweeper_tiles_landscape, parent, false)
            }
            else {
                view = inflater.inflate(R.layout.minesweeper_tiles, parent, false)
            }

            return MinesweeperHolder(view)
        }

        override fun getItemCount(): Int {
            return listener?.positionList?.size ?: 0
        }

        override fun onBindViewHolder(holder: MinesweeperHolder?, position: Int) {
            holder?.bindTile(listener?.positionList?.get(position))
        }
    }

    inner class MinesweeperHolder(view: View?): RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            view?.setOnClickListener(this)
        }

        fun bindTile(position: Int?) {
            //If the game has already been started, then draw the board view appropriately
            if (!recyclerAdapter!!.isStarting) {
                updateTile(this.itemView.findViewById<BoardDrawView>(R.id.tileCanvas),
                        this.itemView, listener!!.getSpace(position!!))
            }
            else if (recyclerAdapter!!.isScreenChange) {
                //Otherwise, redraw the user positions
                if (listener!!.getAllPositions().contains(position)) {
                    updateTile(this.itemView.findViewById<BoardDrawView>(R.id.tileCanvas),
                            this.itemView, listener!!.getSpace(position!!))
                }
            }

        }

        override fun onClick(v: View?) {

            if (!listener!!.isBoardUnclickable()) {
                //isStarting is false, because if the user clicks a tile then clearly
                //the board has been rendered
                recyclerAdapter?.isStarting = false
                recyclerAdapter?.isScreenChange = false

                listener!!.selectedItemAtPosition(adapterPosition)

                //If the space is a free space, notify the adapter so all surrounding free tiles
                //may be drawn appropriately
                if (listener!!.getSpace(adapterPosition) == 0) {
                    for (i in listener!!.clearFreeSpaces())
                        recyclerAdapter?.notifyItemChanged(i)
                } else if (listener!!.getSpace(adapterPosition) == -1) {
                    //If the space is a mine, notify the adapter to reveal all the mines on the
                    //board
                    for (i in listener!!.showMines())
                        recyclerAdapter?.notifyItemChanged(i)
                } else {
                    updateTile(this.itemView.findViewById<BoardDrawView>(R.id.tileCanvas),
                            this.itemView,
                            listener!!.getSpace(adapterPosition))
                }
            }
        }

        //Reveal a tile when it is clicked
        private fun updateTile(v: BoardDrawView, gridTile: View, spaceValue: Int) {
            setClickable(false, gridTile) //Mark the tile as unclickable
            animateTile(v, spaceValue) //Animate the tile
            v.drawSpace(spaceValue, true) //Draw the value of this space
        }

        private fun setClickable(clickable: Boolean, v: View) {
            v.isClickable = clickable
        }

        //Animate the reveal of a numeric space or a mine.
        private fun animateTile(v: View?, value: Int) {
            val tileAnimation: AnimatorSet

            if (value > 0)
                tileAnimation = AnimatorInflater.loadAnimator(activity, R.animator.background_anim_space) as AnimatorSet
            else if (value == 0)
                tileAnimation = AnimatorInflater.loadAnimator(activity, R.animator.background_anim_free) as AnimatorSet
            else
                tileAnimation = AnimatorInflater.loadAnimator(activity, R.animator.background_anim_mine) as AnimatorSet

            //If the tiles are being re-drawn because of a screen change, do not animate them
            if (!recyclerAdapter!!.isScreenChange) {
                tileAnimation.setTarget(v)
                tileAnimation.start()
            }
        }
    }
}


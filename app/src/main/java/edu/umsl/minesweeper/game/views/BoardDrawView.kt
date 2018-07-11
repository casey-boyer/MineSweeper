package edu.umsl.minesweeper.game.views

import android.content.Context
import android.graphics.*
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.View
import edu.umsl.minesweeper.R

class BoardDrawView: View {

    constructor(context: Context): this(context, null) {
        this.setWillNotDraw(false)
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        this.setWillNotDraw(false)
    }

    private val tilePaint = Paint()
    private val textPaint = Paint()
    private val freeSpacePaint = Paint()
    private val minePaint = Paint()
    private val textDivider = 1/2f
    private val mineRadius = 35f
    private val paintStrokeWidth = resources.displayMetrics.density * 4
    private var started = false
    private var spaceValue = -1
    private val freeSpace = "FREE"

    init {
        //Initialize the colors, strokeWidth, styles, etc of each Paint object
        textPaint.color = Color.GREEN
        tilePaint.color = Color.WHITE
        minePaint.color = Color.BLACK
        freeSpacePaint.color = Color.YELLOW

        tilePaint.strokeWidth = paintStrokeWidth
        textPaint.strokeWidth = paintStrokeWidth
        freeSpacePaint.strokeWidth = paintStrokeWidth

        tilePaint.style = Paint.Style.STROKE
        textPaint.style = Paint.Style.FILL
        freeSpacePaint.style = Paint.Style.FILL

        textPaint.isAntiAlias = true
        freeSpacePaint.isAntiAlias = true

        textPaint.textAlign = Paint.Align.CENTER
        freeSpacePaint.textAlign = Paint.Align.CENTER

        textPaint.textSize = 50f
        freeSpacePaint.textSize = 30f
        textPaint.typeface = ResourcesCompat.getFont(this.context, R.font.vcrosdmono1001)
        freeSpacePaint.typeface = ResourcesCompat.getFont(this.context, R.font.vcrosdmono1001)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //If the game is started, draw the value of the space
        if (started) {
            when(spaceValue) {
                //When the space is 0, draw a free tile;
                //when the space is -1, draw a mine;
                //otherwise, draw the value associated with the tile
                0 -> drawFreeTile(canvas)
                -1 -> drawMine(canvas)
                else -> {
                    drawNumber(spaceValue, canvas)
                }
            }
        }
        else
            drawTileUnclicked(canvas)
    }

    fun drawSpace(value: Int, isStarting: Boolean) {
        started = isStarting
        spaceValue = value

        invalidate()
    }

    //Draw a number to represent adjacent mines
    private fun drawNumber(number: Int, canvas: Canvas) {
        canvas.drawText(number.toString(), width * textDivider, height * textDivider, textPaint)
    }

    //Method to give an unclicked tile a 'beveled' effect.
    private fun drawTileUnclicked(canvas: Canvas) {
        //Draw white lines on the top and left of the cell
        canvas.drawLine(0f, 0f, width *1f, 0f, tilePaint)
        canvas.drawLine(0f, 0f, 0f, height *1f, tilePaint)

        tilePaint.color = Color.DKGRAY

        //Draw dark gray lines on the bottom and right of the cell
        canvas.drawLine(0f, height * 1f, width * 1f, height * 1f, tilePaint)
        canvas.drawLine(width * 1f, 0f, width * 1f, height * 1f, tilePaint)

        tilePaint.color = Color.WHITE
    }

    private fun drawFreeTile(canvas: Canvas) {
        //Draw the text "FREE" to indicate free space
        canvas.drawText(freeSpace, (width * textDivider), (height * textDivider), freeSpacePaint)
    }

    private fun drawMine(canvas: Canvas) {
        //Draw a black circle with a smaller red circle within it
        canvas.drawCircle(width * textDivider, height * textDivider, mineRadius, minePaint)

        minePaint.color = Color.RED

        canvas.drawCircle(width * textDivider, height * textDivider, mineRadius * textDivider,
                minePaint)

        minePaint.color = Color.BLACK
    }
}
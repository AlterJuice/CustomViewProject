package com.edu.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import kotlin.experimental.and
import kotlin.experimental.or


/**
 * TODO: document your custom view class.
 */
class CustomShapeView : View {
    object Corners {
        const val NONE: Int = 1
        const val TOP_LEFT = 2
        const val TOP_RIGHT = 4
        const val BOTTOM_LEFT = 8
        const val BOTTOM_RIGHT = 16

        val TOP = (TOP_LEFT or TOP_RIGHT)
        val BOTTOM = (BOTTOM_LEFT or BOTTOM_RIGHT)
        val LEFT = (TOP_LEFT or BOTTOM_LEFT)
        val RIGHT = (TOP_RIGHT or BOTTOM_RIGHT)

        val ALL = (TOP_LEFT or TOP_RIGHT or BOTTOM_RIGHT or BOTTOM_LEFT)
    }

    private var _strokeWidth = 0f
    private var _strokeColor = Color.BLACK
    private var _cornerRadius = 0f
    private var _corners: Int = Corners.NONE

    var strokeWidth: Float
        get() = _strokeWidth
        set(value) {
            _strokeWidth = value
            invalidateTextPaintAndMeasurements()
        }

    var corners: Int
        get() = _corners
        set(value) {
            _corners = value
        }

    var strokeColor: Int
        get() = _strokeColor
        set(value) {
            _strokeColor = value
            invalidateTextPaintAndMeasurements()
        }

    var cornerRadius: Float
        get() = _cornerRadius
        set(value) {
            _cornerRadius = value
            invalidateTextPaintAndMeasurements()
        }

    /**
     * In the example view, this drawable is drawn above the text.
     */
    var exampleDrawable: Drawable? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }
    // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
    // values that should fall on pixel boundaries.

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.CustomShapeView, defStyle, 0
        ).apply {
            try {
                cornerRadius = getFloat(R.styleable.CustomShapeView_cornerRadius, -1f)
                strokeWidth = getDimension(R.styleable.CustomShapeView_strokeColor, 0f)
                strokeColor = getColor(R.styleable.CustomShapeView_strokeColor, Color.BLACK)
                corners = getInteger(R.styleable.CustomShapeView_corners, Corners.NONE)

            } finally {
                recycle()
            }
        }


        if (a.hasValue(R.styleable.CustomShapeView_exampleDrawable)) {
            exampleDrawable = a.getDrawable(
                R.styleable.CustomShapeView_exampleDrawable
            )
            exampleDrawable?.callback = this
        }

        a.recycle()
    }

    private fun invalidateTextPaintAndMeasurements() {
        textPaint.let {
            it.textSize = exampleDimension
            it.color = exampleColor
            textWidth = it.measureText(exampleString)
            textHeight = it.fontMetrics.bottom
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom
        val paint = Paint()
        paint.isAntiAlias = true

        var topLeft = 0f
        var topRight= 0f
        var bottomRight= 0f
        var bottomLeft = 0f

        if (Flags.hasFlag(corners, Corners.TOP_LEFT))
            topLeft = cornerRadius
        if (Flags.hasFlag(corners, Corners.TOP_RIGHT))
            topRight = cornerRadius
        if (Flags.hasFlag(corners, Corners.BOTTOM_RIGHT))
            bottomRight = cornerRadius
        if (Flags.hasFlag(corners, Corners.BOTTOM_LEFT))
            bottomLeft = cornerRadius



        canvas.drawRoundRect(
            RectF(0f, 0f, contentWidth.toFloat(), contentHeight.toFloat()),
            80f,
            80f,
            paint
        )

        exampleString?.let {
            // Draw the text.
            canvas.drawText(
                it,
                paddingLeft + (contentWidth - textWidth) / 2,
                paddingTop + (contentHeight + textHeight) / 2,
                textPaint
            )
        }

        // Draw the example drawable on top of the text.
        exampleDrawable?.let {
            it.setBounds(
                paddingLeft, paddingTop,
                paddingLeft + contentWidth, paddingTop + contentHeight
            )
            it.draw(canvas)
        }
    }
}
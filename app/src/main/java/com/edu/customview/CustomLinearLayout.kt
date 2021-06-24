package com.edu.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding


class CustomLinearLayout@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): LinearLayout(context, attrs, defStyle) {

    private lateinit var textPaint: TextPaint


    fun addItem(text: String){
        val textView = generateTextView(text)
        addView(textView)
    }

    private fun generateTextView(text: String): TextView{
        val textView = TextView(context)
        with(textView){
            setText(text)
            setBackgroundColor(Color.parseColor("#000000"))
            setTextColor(Color.parseColor("#ffffffff"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                4f,
                resources.displayMetrics
            )
            textView.setPadding(px.toInt())
        }
        return textView
    }

    init {
        init(attrs, defStyle)
        orientation = VERTICAL
    }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.CustomLinearLayout, defStyle, 0
        )

        a.recycle()
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
    }

}
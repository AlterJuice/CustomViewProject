package com.edu.customview

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.marginTop
import androidx.core.view.setPadding

val Int.dp
    get() = (Resources.getSystem().displayMetrics.density * this).toInt()

class CustomLinearLayout@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): LinearLayout(context, attrs, defStyle) {


    init {
        init(attrs, defStyle)
        orientation = VERTICAL
    }

    fun addItem(text: String){
        val textView = generateTextView(text)
        addView(textView)
    }

    private fun generateTextView(text: String): TextView{
        val textView = TextView(context)
        configureTextView(textView, text)
        return textView
    }

    private fun configureTextView(textView: TextView, text: String){
        textView.text = text
        textView.setBackgroundColor(Color.parseColor("#000000"))
        textView.setTextColor(Color.parseColor("#ffffffff"))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        textView.setPadding(4.dp)
    }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.CustomLinearLayout, defStyle, 0
        )
        a.recycle()
    }

}
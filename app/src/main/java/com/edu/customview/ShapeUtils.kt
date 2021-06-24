package com.edu.customview

import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.view.View
import androidx.databinding.BindingAdapter

class ShapeUtils {

    fun getRoundedShape(view: View, radius: Float): ShapeDrawable {
        return getRoundedShape(view, radius, radius, radius, radius)
    }

    fun getRoundedShape(view: View, topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float): ShapeDrawable{
        val path = Path()
        path.addRoundRect(RectF(0f, 0f, view.width.toFloat(), view.height.toFloat()),
            getCornersFloatArray(topLeft, topRight, bottomLeft, bottomRight), Path.Direction.CW)

        return ShapeDrawable().apply { draw(Canvas().apply { drawPath(path, Paint().apply { color = Color.GREEN }) }) }
    }

    private fun getCornersFloatArray(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float): FloatArray{
        return floatArrayOf(
            topLeft, topLeft,   // Top left radius in px
            topRight, topRight,  // Top right radius in px
            bottomRight, bottomRight,     // Bottom right radius in px
            bottomLeft, bottomLeft      // Bottom left radius in px
        )

    }

    @BindingAdapter("drawableX")
    fun goneUnless(view: View, radius: Float) {
        view.background = getRoundedShape(view, radius)
    }
}
package com.edu.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import kotlin.math.max


class CustomShapeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): View(context, attrs, defStyle) {
    // passing arguments into view on place

    private var strokeWidth = 0f
    private var strokeColor = Color.BLACK

    private var defaultRadius: Float = 0f
    private var defaultOuterRadius: Float = defaultRadius
    private var defaultInnerRadius: Float = defaultRadius

    private var shapeColor = Color.WHITE
    private var innerRadius = ShapeRadius(defaultOuterRadius)
    private var outerRadius = ShapeRadius(defaultInnerRadius)

    private var innerPath: Path = Path()
    private var outerPath: Path = Path()

    private var paint: Paint = Paint()
    private var metric = DisplayMetrics()

    private var rectF: RectF = RectF()
    private var compareInnerWithOuter: Boolean = false

    private var innerPadding: Float = 0f
    private var innerPaddingTop: Float = 0f
    private var innerPaddingBottom: Float = 0f
    private var innerPaddingLeft: Float = 0f
    private var innerPaddingRight: Float = 0f


    init { init(attrs, defStyle) }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.CustomShapeView, defStyle, 0
        ).apply {
            try {
                defaultRadius = getDimension(R.styleable.CustomShapeView_radius, 0f)
                defaultOuterRadius = getDimension(R.styleable.CustomShapeView_outerRadius, defaultRadius)
                defaultInnerRadius = getDimension(R.styleable.CustomShapeView_innerRadius, defaultRadius)

                outerRadius = ShapeRadius(defaultOuterRadius)
                outerRadius.topLeft = getDimension(R.styleable.CustomShapeView_outerRadiusTopLeft, defaultOuterRadius)
                outerRadius.topRight = getDimension(R.styleable.CustomShapeView_outerRadiusTopRight, defaultOuterRadius)
                outerRadius.bottomLeft = getDimension(R.styleable.CustomShapeView_outerRadiusBottomLeft, defaultOuterRadius)
                outerRadius.bottomRight = getDimension(R.styleable.CustomShapeView_outerRadiusBottomRight, defaultOuterRadius)

                innerRadius = ShapeRadius(defaultOuterRadius)
                innerRadius.topLeft = getDimension(R.styleable.CustomShapeView_innerRadiusTopLeft, defaultInnerRadius)
                innerRadius.topRight = getDimension(R.styleable.CustomShapeView_innerRadiusTopRight, defaultInnerRadius)
                innerRadius.bottomLeft = getDimension(R.styleable.CustomShapeView_innerRadiusBottomLeft, defaultInnerRadius)
                innerRadius.bottomRight = getDimension(R.styleable.CustomShapeView_innerRadiusBottomRight, defaultInnerRadius)

                shapeColor = getColor(R.styleable.CustomShapeView_shapeColor, Color.WHITE)

                strokeWidth = getDimension(R.styleable.CustomShapeView_strokeWidth, 0f)
                strokeColor = getColor(R.styleable.CustomShapeView_strokeColor, Color.BLACK)

                innerPadding = getDimension(R.styleable.CustomShapeView_innerPadding, 0f)
                innerPaddingTop = getDimension(R.styleable.CustomShapeView_innerPaddingTop, innerPadding)
                innerPaddingBottom = getDimension(R.styleable.CustomShapeView_innerPaddingBottom, innerPadding)
                innerPaddingLeft = getDimension(R.styleable.CustomShapeView_innerPaddingLeft, innerPadding)
                innerPaddingRight = getDimension(R.styleable.CustomShapeView_innerPaddingRight, innerPadding)

                compareInnerWithOuter = getBoolean(R.styleable.CustomShapeView_compareInnerWithOuter, false)
                if (compareInnerWithOuter) {
                    normalizeInnerWithOuterRadius()
                }

            } finally {
                recycle()
            }
        }
    }

    fun setOuterCornerRadius(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float){
        val shapeRadius = ShapeRadius(topLeft, topRight, bottomLeft, bottomRight)
        setOuterCornerRadius(shapeRadius)
    }

    fun setOuterCornerRadius(radius: Float){
        setOuterCornerRadius(radius, radius, radius, radius)
    }

    fun setOuterCornerRadius(shapeRadius: ShapeRadius){
        outerRadius = shapeRadius
        invalidate()
    }

    fun setInnerCornerRadius(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float){
        val shapeRadius = ShapeRadius(topLeft, topRight, bottomLeft, bottomRight)
        setInnerCornerRadius(shapeRadius)
    }

    fun setInnerCornerRadius(radius: Float){
        setInnerCornerRadius(radius, radius, radius, radius)
    }

    fun setInnerCornerRadius(shapeRadius: ShapeRadius){
        innerRadius = shapeRadius
        invalidate()
    }

    fun setUpStrokeConfigs(strokeColor: Int, strokeWidth: Float){
        this.strokeColor = strokeColor
        this.strokeWidth = strokeWidth
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        //Get the pixel density of the current mobile device
        display.getRealMetrics(metric)
        val mDensity = metric.density
        // qCalculate the border thickness value
        val thickness: Float = strokeWidth * mDensity


        // creating outer shape with sizes
        rectF.left = paddingLeft.toFloat()
        rectF.top = paddingTop.toFloat()
        rectF.right = width.toFloat() - paddingRight
        rectF.bottom = height.toFloat() - paddingBottom

        paint.color = strokeColor

        // painting outer shape into outerPath and adding to canvas
        outerPath.addRoundRect(rectF, getOuterCornersFloatArray(), Path.Direction.CW)
        canvas.drawPath(outerPath, paint)

        // Stroke width
        rectF.left = thickness + paddingLeft
        rectF.top = thickness + paddingTop
        rectF.right = width - thickness - paddingRight
        rectF.bottom = height - thickness - paddingBottom

        // Paddings
        rectF.left += innerPaddingLeft
        rectF.top += innerPaddingTop
        rectF.right -= innerPaddingRight
        rectF.bottom -= innerPaddingBottom

        paint.color = shapeColor
        innerPath.addRoundRect(rectF, getInnerCornersFloatArray(), Path.Direction.CW)
        canvas.drawPath(innerPath, paint)

        super.onDraw(canvas)
    }

    private fun normalizeInnerWithOuterRadius(){
        innerRadius.topLeft = max(innerRadius.topLeft, outerRadius.topLeft)
        innerRadius.topRight = max(innerRadius.topRight, outerRadius.topRight)
        innerRadius.bottomLeft = max(innerRadius.bottomLeft, outerRadius.bottomLeft)
        innerRadius.bottomRight = max(innerRadius.bottomRight, outerRadius.bottomRight)
    }


    private fun getInnerCornersFloatArray(): FloatArray {
        return floatArrayOf(
            innerRadius.topLeft, innerRadius.topLeft,   // Top left radius in px
            innerRadius.topRight, innerRadius.topRight,   // Top right radius in px
            innerRadius.bottomRight, innerRadius.bottomRight,     // Bottom right radius in px
            innerRadius.bottomLeft, innerRadius.bottomLeft      // Bottom left radius in px

        )
    }
    private fun getOuterCornersFloatArray(): FloatArray{
        return floatArrayOf(
            outerRadius.topLeft, outerRadius.topLeft,
            outerRadius.topRight, outerRadius.topRight,
            outerRadius.bottomRight, outerRadius.bottomRight,
            outerRadius.bottomLeft, outerRadius.bottomLeft
        )

    }
    class ShapeRadius constructor(
        var topLeft: Float,
        var topRight: Float,
        var bottomLeft: Float,
        var bottomRight: Float
    ) {
        constructor(radius: Float) : this(radius, radius, radius, radius)
        private fun normalizeValue(value: Float): Float = if (value <= 0) 0f else value
    }
}
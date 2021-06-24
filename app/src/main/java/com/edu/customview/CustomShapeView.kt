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


    private var _strokeWidth = 0f
    private var _strokeColor = Color.BLACK

    var defaultRadius: Float = 0f
    var defaultOuterRadius: Float = defaultRadius
    var defaultInnerRadius: Float = defaultRadius

    private var _shapeColor = Color.WHITE
    private var _innerRadius = ShapeRadius(defaultOuterRadius)
    private var _outerRadius = ShapeRadius(defaultInnerRadius)


    private var innerPath: Path = Path()
    private var outerPath: Path = Path()

    private lateinit var paint: Paint
    private var metric = DisplayMetrics()

    private var rectF: RectF = RectF()
    private var compareInnerWithOuter: Boolean = false

    private var innerPadding: Float = 0f
    private var innerPaddingTop: Float = 0f
    private var innerPaddingBottom: Float = 0f
    private var innerPaddingLeft: Float = 0f
    private var innerPaddingRight: Float = 0f


    class ShapeRadius constructor(
        var topLeft: Float,
        var topRight: Float,
        var bottomLeft: Float,
        var bottomRight: Float
    ) {
        constructor(radius: Float) : this(radius, radius, radius, radius)

        init {
            topLeft = normalizeValue(topLeft)
            topRight = normalizeValue(topRight)
            bottomLeft = normalizeValue(bottomLeft)
            bottomRight = normalizeValue(bottomRight)
        }

        private fun normalizeValue(value: Float): Float = if (value <= 0) 0f else value
    }

    var strokeWidth: Float
        get() = _strokeWidth
        set(value) {
            _strokeWidth = value
            updateShapeMeasurements()
        }

    var strokeColor: Int
        get() = _strokeColor
        set(value) {
            _strokeColor = value
            updateShapeMeasurements()
        }

    var shapeColor: Int
        get() = _shapeColor
        set(value) {
            _shapeColor = value
            updateShapeMeasurements()
        }

    var outerRadius: ShapeRadius
        get() = _outerRadius
        private set(value) = setOuterCornerRadius(value)


    var innerRadius: ShapeRadius
        get() = _innerRadius
        private set(value) = setInnerCornerRadius(value)


    fun setOuterCornerRadius(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float){
        val shapeRadius = ShapeRadius(topLeft, topRight, bottomLeft, bottomRight)
        setOuterCornerRadius(shapeRadius)
    }

    fun setOuterCornerRadius(radius: Float){
        setOuterCornerRadius(radius, radius, radius, radius)
    }

    fun setOuterCornerRadius(shapeRadius: ShapeRadius){
        _outerRadius = shapeRadius
        updateShapeMeasurements()
    }

    fun setInnerCornerRadius(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float){
        val shapeRadius = ShapeRadius(topLeft, topRight, bottomLeft, bottomRight)
        setInnerCornerRadius(shapeRadius)
    }

    fun setInnerCornerRadius(radius: Float){
        setInnerCornerRadius(radius, radius, radius, radius)
    }

    fun setInnerCornerRadius(shapeRadius: ShapeRadius){
        _innerRadius = shapeRadius
        updateShapeMeasurements()
    }

    fun updateShapeMeasurements(){

    }

    init {
        init(attrs, defStyle)
    }


    // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
    // values that should fall on pixel boundaries.

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes

        paint = Paint()
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.CustomShapeView, defStyle, 0
        ).apply {
            try {

                // innerRadius = ShapeRadius(getDimension(R.styleable.CustomShapeView_innerRadius, 0f))
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

    private fun normalizeInnerWithOuterRadius(){
        _innerRadius.topLeft = max(_innerRadius.topLeft, outerRadius.topLeft)
        _innerRadius.topRight = max(_innerRadius.topRight, outerRadius.topRight)
        _innerRadius.bottomLeft = max(_innerRadius.bottomLeft, outerRadius.bottomLeft)
        _innerRadius.bottomRight = max(_innerRadius.bottomRight, outerRadius.bottomRight)
    }


    override fun onDraw(canvas: Canvas) {
        //Get the pixel density of the current mobile device
        display.getRealMetrics(metric)
        val mDensity = metric.density
        // qCalculate the border thickness value
        val thickness: Float = strokeWidth * mDensity


        // creating outer shape with sizes
        rectF.left = 0f
        rectF.top = 0f
        rectF.right = width.toFloat()
        rectF.bottom = height.toFloat()

        paint.color = strokeColor

        // outerPath.addRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), getOuterCornersFloatArray(), Path.Direction.CW)

        // painting outer shape into outerPath and adding to canvas
        outerPath.addRoundRect(rectF, getOuterCornersFloatArray(), Path.Direction.CW)
        canvas.drawPath(outerPath, paint)


        // Stroke width
        rectF.left = thickness
        rectF.top = thickness
        rectF.right = width - thickness
        rectF.bottom = height - thickness

        // Paddings
        rectF.left += innerPaddingLeft
        rectF.top += innerPaddingTop
        rectF.right -= innerPaddingRight
        rectF.bottom -= innerPaddingBottom

        paint.color = shapeColor
        // innerPath.addRoundRect(RectF(thickness, thickness, width-thickness, height-thickness), getInnerCornersFloatArray(), Path.Direction.CW)
        innerPath.addRoundRect(rectF, getInnerCornersFloatArray(), Path.Direction.CW)
        canvas.drawPath(innerPath, paint)

        super.onDraw(canvas)
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
            outerRadius.topLeft, outerRadius.topLeft,   // Top left radius in px
            outerRadius.topRight, outerRadius.topRight,   // Top right radius in px
            outerRadius.bottomRight, outerRadius.bottomRight,     // Bottom right radius in px
            outerRadius.bottomLeft, outerRadius.bottomLeft      // Bottom left radius in px
        )

    }
}
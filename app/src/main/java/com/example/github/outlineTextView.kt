package com.example.github

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class outlineTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var strokeColor: Int
    private var strokeWidthVal: Float

    init {
        // Attributes 가져오기
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.outlineTextView)
        strokeWidthVal = typedArray.getFloat(R.styleable.outlineTextView_textStrokeWidth, 3f)
        strokeColor = typedArray.getColor(
            R.styleable.outlineTextView_textStrokeColor,
            0
        ) // Default stroke color (you can replace with your preferred color)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        // Draw stroke
        val originalTextColor = textColors
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidthVal
        setTextColor(strokeColor)
        super.onDraw(canvas)

        // Draw fill
        paint.style = Paint.Style.FILL
        setTextColor(originalTextColor)
        super.onDraw(canvas)
    }
}
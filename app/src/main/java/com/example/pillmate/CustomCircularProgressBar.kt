package com.example.pillmate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CustomCircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0
    private var maxProgress = 100
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(8f) // 두께를 11dp로 설정
        strokeCap = Paint.Cap.ROUND // 라운드 처리
    }
    private val rectF = RectF()

    private var gradient: LinearGradient? = null

    init {
        updateGradient()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val size = min(w, h)
        val padding = paint.strokeWidth / 2
        rectF.set(padding, padding, size - padding, size - padding)
        updateGradient()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background circle
        paint.shader = null
        paint.color = Color.LTGRAY
        canvas.drawArc(rectF, 0f, 360f, false, paint)

        // Draw gradient progress arc
        paint.shader = gradient
        val sweepAngle = (progress / maxProgress.toFloat()) * 360
        canvas.drawArc(rectF, -90f, sweepAngle, false, paint)
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        updateGradient()
        invalidate()
    }

    fun setMaxProgress(maxProgress: Int) {
        this.maxProgress = maxProgress
        updateGradient()
        invalidate()
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    private fun updateGradient() {
        val colors = intArrayOf(
            Color.parseColor("#1E54DF"),
            Color.parseColor("#1E54DF"),
            Color.parseColor("#88A4EE"),
            Color.parseColor("#BECDF6"),
        )
        val sweepAngle = (progress / maxProgress.toFloat()) * 360
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY)

        val startX = centerX
        val startY = centerY - radius
        val endX = centerX
        val endY = centerY + radius

        gradient = LinearGradient(startX, startY, endX, endY, colors, null, Shader.TileMode.REPEAT)
    }
}
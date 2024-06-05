package com.example.pillmate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
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
        strokeWidth = dpToPx(11f) // 두께를 11dp로 설정
        strokeCap = Paint.Cap.ROUND // 라운드 처리
    }
    private val rectF = RectF()

    private lateinit var gradient: SweepGradient

    init {
        val colors = intArrayOf(Color.parseColor("#1E54DF"),Color.parseColor("#B1EDEA"),Color.parseColor("#1E54DF"))
        gradient = SweepGradient(width / 2f, height / 2f, colors, null)
        paint.shader = gradient
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val size = min(w, h)
        val padding = paint.strokeWidth / 2
        rectF.set(padding, padding, size - padding, size - padding)
        gradient = SweepGradient(size / 2f, size / 2f, intArrayOf(Color.parseColor("#1E54DF"), Color.parseColor("#B1EDEA"), Color.parseColor("#1E54DF")), null)
        paint.shader = gradient
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
        invalidate()
    }

    fun setMaxProgress(maxProgress: Int) {
        this.maxProgress = maxProgress
        invalidate()
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}
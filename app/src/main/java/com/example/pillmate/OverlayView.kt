package com.example.pillmate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class OverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint().apply {
        color = 0x80000000.toInt() // 반투명한 회색
        isAntiAlias = true
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 배경 전체를 그린다
        val layerId = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        // 투명한 사각형 영역을 만든다
        val centerX = width / 2f
        val centerY = height / 2f
        val size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics)
        val left = centerX - size / 2
        val top = centerY - size / 2
        val right = centerX + size / 2
        val bottom = centerY + size / 2
        canvas.drawRect(left, top, right, bottom, clearPaint)

        canvas.restoreToCount(layerId)
    }
}
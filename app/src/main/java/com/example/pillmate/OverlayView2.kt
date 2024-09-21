package com.example.pillmate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View

class OverlayView2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint().apply {
        color = 0x80000000.toInt() // Semi-transparent gray
        isAntiAlias = true
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    private var overlayGuideRect = Rect()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the semi-transparent background
        val layerId = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        // Draw the transparent rectangle based on overlayGuideRect
        canvas.drawRect(
            overlayGuideRect.left.toFloat(),
            overlayGuideRect.top.toFloat(),
            overlayGuideRect.right.toFloat(),
            overlayGuideRect.bottom.toFloat(),
            clearPaint
        )

        canvas.restoreToCount(layerId)
    }

    // Call this method to update the transparent area dynamically
    fun updateOverlayGuide(rect: Rect) {
        Log.d("OverlayView2", "Received Rect: left=${rect.left}, top=${rect.top}, right=${rect.right}, bottom=${rect.bottom}")
        overlayGuideRect = rect
        invalidate() // Re-draw the view with the new transparent area
    }

    // Add a getter method for overlayGuideRect
    fun getOverlayGuideRect(): Rect {
        return overlayGuideRect
    }
}

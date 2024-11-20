package com.example.pillmate

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.CharacterStyle

class CustomTypefaceSpan(private val typeface: Typeface) : CharacterStyle() {
    override fun updateDrawState(tp: TextPaint) {
        tp.typeface = typeface
    }
}
package com.sulu.kotlin.utils

import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.View

/**
 * @作者:XLEO
 * @创建日期: 2017/8/22 18:07
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class SpanBuilder {
    private val ssb: SpannableStringBuilder by lazy {
        SpannableStringBuilder()
    }

    fun init(content: String): SpanBuilder {
        reset()
        ssb.append(content, 0, content.length)
        return this
    }

    fun setLinkSpan(start: Int, end: Int, color: Int, func: (() -> Unit)?): SpanBuilder {
        val linkSpan = object : ClickableSpan() {
            override fun onClick(widget: View?) {
                func?.invoke()
            }

            override fun updateDrawState(ds: TextPaint?) {
                ds?.setColor(color)
                ds?.setDither(false)
            }
        }
        ssb.setSpan(linkSpan, start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        return this
    }

    fun setImageSpan(start: Int, end: Int, img: Drawable): SpanBuilder {
        val imageSpan = ImageSpan(img)
        ssb.setSpan(imageSpan, start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        return this
    }

    fun result(): SpannableStringBuilder {
        return  ssb
    }

    private fun reset() {
        ssb.clear()
    }
}
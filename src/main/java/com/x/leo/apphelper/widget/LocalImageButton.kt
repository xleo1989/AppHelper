package com.x.leo.apphelper.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import com.x.leo.apphelper.R

/**
 * @作者:XLEO
 * @创建日期: 2017/9/4 10:16
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
open class LocalImageButton(ctx: Context, attributeSet: AttributeSet? ): ImageButton(ctx,attributeSet){
    private var actionName: String = "no name set"

    init {
        if (attributeSet != null) {
            val attrs = ctx.obtainStyledAttributes(attributeSet, R.styleable.LocalImageButton)
            if (attrs.hasValue(R.styleable.LocalTextView_tActionName)) {
                actionName = attrs.getString(R.styleable.LocalTextView_tActionName)
            }
            attrs.recycle()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(LocalOnClickListener(actionName, l))
    }
}
package com.x.leo.apphelper.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.x.leo.apphelper.R

/**
 * @作者:XLEO
 * @创建日期: 2017/9/1 14:29
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
open class LocalTextView(ctx:Context,attributeSet: AttributeSet?):TextView(ctx,attributeSet){
    private  var actionName: String =  "notSeted"
    var textChangeFunc:((text:CharSequence)->Unit)? = null

    init {
        if (attributeSet != null) {
            val attrs = ctx.obtainStyledAttributes(attributeSet, R.styleable.LocalTextView)
            if (attrs.hasValue(R.styleable.LocalTextView_tActionName)) {
                actionName = attrs.getString(R.styleable.LocalTextView_tActionName)
            }
            attrs.recycle()
        }
    }

    override fun setText(text:CharSequence,type:BufferType){
        if (actionName != null) {
            ActionTraceManager.textChange(actionName,this.text,text,this)
        }
        textChangeFunc?.invoke(text)
        super.setText(text, type)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(LocalOnClickListener(actionName, l))
    }
}
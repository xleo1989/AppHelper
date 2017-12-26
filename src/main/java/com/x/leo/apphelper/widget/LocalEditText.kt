package com.x.leo.apphelper.widget

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import com.x.leo.apphelper.R
import java.util.regex.Pattern

/**
 * @作者:XLEO
 * @创建日期: 2017/9/1 14:45
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
open class LocalEditText(ctx: Context, attributeSet: AttributeSet?) : EditText(ctx, attributeSet) {
    private var actionName: String = "no name set"
    private var pattern: String? = null
    private var aleartColor: Int = Color.RED
    private var normalColor: Int = textColors.defaultColor
    private var scrollParent: Int = -1
    var textChangerWatcher: ((text: Editable?) -> Unit)? = null

    private var inputPattern: String? = null

    init {
        if (attributeSet != null) {
            val attrs = ctx.obtainStyledAttributes(attributeSet, R.styleable.LocalEditText)
            if (attrs.hasValue(R.styleable.LocalEditText_eActionName)) {
                actionName = attrs.getString(R.styleable.LocalTextView_tActionName)
            }

            if (attrs.hasValue(R.styleable.LocalEditText_legalInputPattern)) {
                inputPattern = attrs.getString(R.styleable.LocalEditText_legalInputPattern)
            }

            if (attrs.hasValue(R.styleable.LocalEditText_legalPattern)) {
                pattern = attrs.getString(R.styleable.LocalEditText_legalPattern)
                aleartColor = attrs.getColor(R.styleable.LocalEditText_alertColor, Color.RED)
                setCheckMode()
            }
            if (attrs.hasValue(R.styleable.LocalEditText_scrollParent)) {
                scrollParent = attrs.getResourceId(R.styleable.LocalEditText_scrollParent, -1)
            }
            attrs.recycle()
        }
        onFocusChangeListener = LocalOnFocusChangeListener(this@LocalEditText, actionName, null, scrollParent)

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                textChangerWatcher?.invoke(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    private var onFocusChangeListenerList: ArrayList<OnFocusChangeListener>? = null
    final override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        if (onFocusChangeListenerList == null) {
            onFocusChangeListenerList = ArrayList()
            if (l != null) {
                onFocusChangeListenerList?.add(l)
            }
            super.setOnFocusChangeListener { v, hasFocus ->
                onFocusChangeListenerList!!.forEach {
                    it.onFocusChange(v, hasFocus)
                }
            }
        } else {
            if (l != null) {
                onFocusChangeListenerList?.add(l)
            }
        }
    }

    private fun setCheckMode() {
        addTextChangedListener(object : LocalTextWatcher(this@LocalEditText, this@LocalEditText.actionName) {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                val result = Pattern.matches(if (inputPattern != null) inputPattern!! else pattern!!, s?.toString())
                if (!result) {
                    this@LocalEditText.setTextColor(aleartColor)
                } else {
                    this@LocalEditText.setTextColor(normalColor)
                }
            }
        })
        if (inputPattern != null) {
            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    val result = Pattern.matches(pattern!!, (v as EditText).text?.toString())
                    if (!result) {
                        this@LocalEditText.setTextColor(aleartColor)
                    } else {
                        this@LocalEditText.setTextColor(normalColor)
                    }
                }
            }
        }
    }


    fun setCheckMode(regex: String, errorColor: Int) {
        pattern = regex
        aleartColor = errorColor
        setCheckMode()
    }

    fun checkLegal(): Boolean {
        return pattern == null || Pattern.matches(pattern!!, this.editableText.toString())
    }
}
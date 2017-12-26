package com.x.leo.apphelper.widget

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet

import com.x.leo.apphelper.R
import java.util.regex.Pattern

/**
 * Created by XLEO on 2017/12/7.
 */
class SpacedEditText(ctx:Context,attributeSet: AttributeSet?):SelfSplitEditText(ctx,attributeSet){
    private var actionName: String = "no name set"
    private var pattern: String? = null
    private var aleartColor: Int = Color.RED
    private var normalColor: Int = textColors.defaultColor
    private var scrollParent:Int = -1
    var textChangerWatcher:((text: Editable?)->Unit)? = null

    init {
        if (attributeSet != null) {
            val attrs = ctx.obtainStyledAttributes(attributeSet, R.styleable.LocalEditText)
            if (attrs.hasValue(R.styleable.LocalEditText_eActionName)) {
                actionName = attrs.getString(R.styleable.LocalTextView_tActionName)
            }
            if (attrs.hasValue(R.styleable.LocalEditText_legalPattern)) {
                pattern = attrs.getString(R.styleable.LocalEditText_legalPattern)
                aleartColor = attrs.getColor(R.styleable.LocalEditText_alertColor, Color.RED)
                setCheckMode()
            }
            if(attrs.hasValue(R.styleable.LocalEditText_scrollParent)){
                scrollParent = attrs.getResourceId(R.styleable.LocalEditText_scrollParent,-1)
            }
            attrs.recycle()
        }
        onFocusChangeListener = LocalOnFocusChangeListener(this@SpacedEditText, actionName, null,scrollParent)

        addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                textChangerWatcher?.invoke(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        if (l is LocalOnFocusChangeListener) {
            super.setOnFocusChangeListener(l)
        } else {
            super.setOnFocusChangeListener(LocalOnFocusChangeListener(this@SpacedEditText, actionName, l,scrollParent))
        }
    }

    private fun setCheckMode() {
        addTextChangedListener(object : LocalTextWatcher(this@SpacedEditText, this@SpacedEditText.actionName) {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                val result = Pattern.matches(pattern!!, s?.toString()?.replace(" ",""))
                if (!result) {
                    this@SpacedEditText.setTextColor(aleartColor)
                } else {
                    this@SpacedEditText.setTextColor(normalColor)
                }
            }
        })
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
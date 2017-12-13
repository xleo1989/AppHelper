package com.x.leo.apphelper.widget

import android.content.Context
import android.text.*
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.widget.EditText
import com.x.leo.apphelper.R


/**
 * Created by XLEO on 2017/12/7.
 */
open class SelfSplitEditText(ctx: Context, attributeSet: AttributeSet?) : EditText(ctx, attributeSet) {
//    private var firstSplitIndex: Int = 0
//    private var splitLength: Int = Int.MAX_VALUE
    private var contentType: Int = 0
    private var digits: String = ""

    private var start = 0
    private var count = 0
    private var before = 0
    private val textWatcher: TextWatcher by lazy {
        object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s == null) {
                    return
                }
                val isMiddle = start + count < s.length
                val isNeedSpace = !isMiddle && isSpace(s.length)
                if (isMiddle || isNeedSpace || count > 1) {
                    val newStr = s.toString().replace(" ", "")
                    val sb = StringBuilder()
                    var spaceCount = 0
                    for (i in 0 until newStr.length) {
                        sb.append(newStr.substring(i, i + 1))
                        if (isSpace(i + 2 + spaceCount)) {
                            sb.append(" ")
                            spaceCount++
                        }
                    }
                    this@SelfSplitEditText.removeTextChangedListener(textWatcher)
                    s.replace(0, s.length, sb)
                    if (!isMiddle || count > 1){
                        setSelection(s.length)
                    }else if(isMiddle){
                        if (count == 0){
                            if(isSpace(start - before + 1))
                                setSelection(if(start - before > 0) start - before else 0)
                            else
                                setSelection(if (start - before + 1 > s.length) s.length else start - before + 1)
                        }
                    }else{
                        if (isSpace(start - before + count)) {
                            setSelection(if(start + count - before + 1 < s.length) start + count -before + 1 else s.length )
                        }else{
                            setSelection(start + count - before)
                        }
                    }
                    this@SelfSplitEditText.addTextChangedListener(textWatcher)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@SelfSplitEditText.start = start
                this@SelfSplitEditText.before = before
                this@SelfSplitEditText.count = count
            }
        }

    }

    companion object {
        const val TYPE_PHONE = 0
        const val TYPE_CARD = 1
        const val TYPE_IDCARD = 2
    }

    init {
        if (attributeSet != null) {
            val attrs = ctx.obtainStyledAttributes(attributeSet, R.styleable.SelfSplitEditText)
//            if (attrs.hasValue(R.styleable.SelfSplitEditText_firstSplitIndex)) {
//                firstSplitIndex = attrs.getInteger(R.styleable.SelfSplitEditText_firstSplitIndex, 0)
//            }
//            if (attrs.hasValue(R.styleable.SelfSplitEditText_splitLength)) {
//                splitLength = attrs.getInteger(R.styleable.SelfSplitEditText_splitLength, Int.MAX_VALUE)
//            }
            if (attrs.hasValue(R.styleable.SelfSplitEditText_contentInputType)) {
                contentType = attrs.getInt(R.styleable.SelfSplitEditText_contentInputType, 0)
            }
            attrs.recycle()
        }
        initType()
        addTextChangedListener(textWatcher)
    }

    private fun initType() {
        when (contentType) {
            TYPE_PHONE -> {
                digits = "0123456789 "
                inputType = InputType.TYPE_CLASS_NUMBER
            }
            TYPE_IDCARD -> {
                digits = "0123456789Xx "
                inputType = InputType.TYPE_CLASS_TEXT
            }
            TYPE_CARD -> {
                digits = "0123456789 "
                inputType = InputType.TYPE_CLASS_NUMBER
            }
            else -> {
            }
        }
    }

    override fun setInputType(type: Int) {
        super.setInputType(type)
        if (!TextUtils.isEmpty(digits))
            keyListener = DigitsKeyListener.getInstance(digits)
    }


    private fun isSpace(length: Int): Boolean {
        return when (contentType) {
            TYPE_CARD -> {
                length >= 7 && (length - 2) % 5 == 0
            }
            TYPE_PHONE -> {
                length >= 4 && (length == 4 || (length + 1) % 5 == 0)
            }
            TYPE_IDCARD -> {
                length > 6 && (length == 7 || (length - 2) % 5 == 0)
            }
            else -> {
                false
            }
        }
    }

}

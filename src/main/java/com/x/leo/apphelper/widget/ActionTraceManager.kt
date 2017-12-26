package com.x.leo.apphelper.widget


import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*

/**
 * @作者:XLEO
 * @创建日期: 2017/9/1 14:34
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object ActionTraceManager {
    fun textChange(name: String, old: CharSequence?, new: CharSequence?, localTextView: TextView) {
        //XLeoToast.showMessage("text change,old:" + old + ";new:" + new  + ";@viewModel:" + localTextView.id)

    }

    fun editText(name: String, old: CharSequence?, new: CharSequence?, localTextView: TextView, startTime: Long, endTime: Long) {
        //XLeoToast.showMessage("text change,old:" + old + ";new:" + new  + ";@viewModel:" + localTextView.id)
    }

    fun focusChange(view: View, old: CharSequence?, new: CharSequence?, startTime: Long, endTime: Long, name: String) {
        //XLeoToast.showMessage("focus change,reSet:" + startTime + ";end:" + endTime + ";@viewModel:" + viewModel.id)
        when (view) {
            is EditText -> {

            }
            else -> {
            }
        }
    }

    fun buttonClick(actionName: String?, v: View) {
        when (v) {
            is Button -> {
            }
            is ImageButton -> {
            }
            is TextView -> {
            }
            is ImageView -> {
            }
            else -> {
            }
        }
    }
}

open class LocalTextWatcher(val view: TextView, val name: String) : TextWatcher {
    var old: CharSequence? = null
    var startTime: Long = 0
    override fun afterTextChanged(s: Editable?) {
        ActionTraceManager.editText(name, old!!, s!!.toString(), view, startTime, SystemClock.elapsedRealtime())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        old = view.text
        startTime = SystemClock.elapsedRealtime()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

}

class LocalOnFocusChangeListener(val view: View, val name: String, val l: View.OnFocusChangeListener?, val scrollView: Int) : View.OnFocusChangeListener {
    var startTime: Long = 0
    var oldText: String? = null
    var newText: String? = null
    val softListener:SoftSpanPopEditText = SoftSpanPopEditText()


    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        l?.onFocusChange(v, hasFocus)
        if (hasFocus) {
            startTime = SystemClock.elapsedRealtime()
            if (v is EditText) {
                oldText = v.text.toString()
            }
            softListener.register(view,scrollView)
        } else {
            if (v is EditText) {
                newText = v.text.toString()
            }
            softListener.unRegister(view)
            ActionTraceManager.focusChange(view, oldText, newText, startTime, SystemClock.elapsedRealtime(), name)
        }
    }

}

class LocalOnClickListener(val name: String?, val l: View.OnClickListener?) : View.OnClickListener {
    override fun onClick(v: View?) {
        l?.onClick(v)
        ActionTraceManager.buttonClick(name, v!!)
    }

}
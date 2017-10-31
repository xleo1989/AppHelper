package com.x.leo.apphelper.log

import android.util.Log

/**
 * @作者:XLEO
 * @创建日期: 2017/9/14 17:00
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object XLog {

    val currentPrioriety: Int = 0 //0-OO
    val isStrict: Boolean = false //限定唯一值

    val VERBOSE = 2
    val DEBUG = 3
    val INFO = 4
    val WARN = 5
    val ERROR = 6
    val ASSERT = 7

    val currentLogLevel:Int = VERBOSE

    private val TOP_LEFT_CORNER = '╔'
    private val BOTTOM_LEFT_CORNER = '╚'
    private val MIDDLE_CORNER = '╟'
    private val HORIZONTAL_DOUBLE_LINE = '║'
    private val DOUBLE_DIVIDER = "════════════════════════════════════════════"
    private val SINGLE_DIVIDER = "────────────────────────────────────────────"
    private val methodIndex = 4

    @JvmStatic
    fun d(message:String,prioriety:Int){
        if (currentLogLevel <= DEBUG && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)){
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.d(pre + tag, message) }
        }
    }
    @JvmStatic
    fun v(message: String,prioriety: Int){
        if (currentLogLevel <= VERBOSE && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)){
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.v(pre + tag, message) }
        }
    }
    @JvmStatic
    fun i(message: String,prioriety: Int){
        if (currentLogLevel <= INFO && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)){
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.i(pre + tag, message) }
        }
    }
    @JvmStatic
    fun w(message: String,prioriety: Int){
        if (currentLogLevel <= WARN && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)){
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.w(pre + tag, message) }
        }
    }
    @JvmStatic
    fun e(message: String,prioriety: Int){
        if (currentLogLevel <= ERROR && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)){
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.e(pre + tag, message) }
        }
    }
    @JvmStatic
    fun a(message: String,prioriety: Int){
        if (currentLogLevel <= ASSERT && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)){
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.wtf(pre + tag, message) }
        }
    }
    @JvmStatic
    private fun logout(func:(pre:String)->Unit){
        try {
            Log.v("" + TOP_LEFT_CORNER, DOUBLE_DIVIDER)
            Log.v("" + HORIZONTAL_DOUBLE_LINE, "Thread:" + Thread.currentThread().name + "(" + Thread.currentThread().id + ")____" + StackInfoUtils.getMethodName(methodIndex + 1) + "()(" + StackInfoUtils.getFileName(methodIndex + 1) + ":" + StackInfoUtils.getLineNumber(methodIndex + 1) + ")")
            Log.v("" + HORIZONTAL_DOUBLE_LINE, SINGLE_DIVIDER)
            func.invoke("" + MIDDLE_CORNER)
            Log.v("" + BOTTOM_LEFT_CORNER, DOUBLE_DIVIDER)
        }catch (e:Exception){
            Log.e("XLog","logout error",e)
        }
    }
}
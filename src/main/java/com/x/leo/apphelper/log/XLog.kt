package com.x.leo.apphelper.log

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import com.x.leo.apphelper.BuildConfig

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

    private val currentPrioriety: Int = 0 //0-OO
    private val isStrict: Boolean = false //限定唯一值

    private val VERBOSE = 0x0020
    val DEBUG = 0x0030
    private val INFO = 0x0040
    private val WARN = 0x0050
    private val ERROR = 0x0060
    private val ASSERT = 0x0070

    private val currentLogLevel: Int = VERBOSE

    private val TOP_LEFT_CORNER = '╔'
    private val BOTTOM_LEFT_CORNER = '╚'
    private val MIDDLE_CORNER = '╟'
    private val HORIZONTAL_DOUBLE_LINE = '║'
    private val DOUBLE_DIVIDER = "════════════════════════════════════════════"
    private val SINGLE_DIVIDER = "────────────────────────────────────────────"
    private val methodIndex = 4
    private var doDebug:Int = 0
    private val DO_LOG = 0x0011
    private val NOT_DO_LOG = 0x0022
    fun initStatus(ctx: Context){
        if (doDebug == 0) {
            doDebug = if (isInDebug(ctx)) {
                DO_LOG
            }else{
                NOT_DO_LOG
            }
        }
    }
    private fun isLogable():Boolean{
       return doDebug == DO_LOG
    }
    private fun isInDebug(ctx:Context):Boolean{
        return if (BuildConfig.DEBUG) {
            true
        }else{
            try {
                ctx.applicationInfo.flags.and(ApplicationInfo.FLAG_DEBUGGABLE) != 0
            }catch (e:Exception){
                false
            }
        }
    }

    fun d(message: String?, prioriety: Int) {
        if (isLogable()&&currentLogLevel <= DEBUG && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)) {
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.d(pre + tag, message) }
        }
    }


    fun v(message: String?, prioriety: Int) {
        if (isLogable()&&currentLogLevel <= VERBOSE && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)) {
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.v(pre + tag, message) }
        }
    }


    fun i(message: String, prioriety: Int) {
        if (isLogable()&&currentLogLevel <= INFO && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)) {
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.i(pre + tag, message) }
        }
    }


    fun w(message: String?, prioriety: Int) {
        if (isLogable()&&currentLogLevel <= WARN && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)) {
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.w(pre + tag, message) }
        }
    }


    fun e(message: String?, e: Throwable?, prioriety: Int) {
        if (isLogable()&&currentLogLevel <= ERROR && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)) {
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre ->
                val builder = StringBuilder(message + "\n")
                e?.stackTrace?.forEach {
                    try {
                        builder.append(it.className + "_" + it.methodName + "() :" + it.lineNumber + "\n")
                    }catch (e:Throwable){
                        e.printStackTrace()
                    }
                }
                Log.e(pre + tag, builder.toString())
            }
        }
    }


    fun a(message: String?, prioriety: Int) {
        if (isLogable()&&currentLogLevel <= ASSERT && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)) {
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.wtf(pre + tag, message) }
        }
    }


    private fun logout(func: (pre: String) -> Unit) {
        try {
            Log.v("" + TOP_LEFT_CORNER, DOUBLE_DIVIDER)
            Log.v("" + HORIZONTAL_DOUBLE_LINE, "Thread:" + Thread.currentThread().name + "(" + Thread.currentThread().id + ")____" + StackInfoUtils.getMethodName(methodIndex + 1) + "()(" + StackInfoUtils.getFileName(methodIndex + 1) + ":" + StackInfoUtils.getLineNumber(methodIndex + 1) + ")")
            Log.v("" + HORIZONTAL_DOUBLE_LINE, SINGLE_DIVIDER)
            func.invoke("" + MIDDLE_CORNER)
            Log.v("" + BOTTOM_LEFT_CORNER, DOUBLE_DIVIDER)
        } catch (e: Exception) {
            Log.e("XLog", "logout error", e)
        }
    }
}
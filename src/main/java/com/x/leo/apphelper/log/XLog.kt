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

    val currentPrioriety: Int = 10 //0-OO
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

    fun d(tag:String,message:String,prioriety:Int){
        if (currentLogLevel <= DEBUG && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)){
            logout { pre -> Log.d(pre + tag, message) }
        }
    }
    fun v(tag: String,message: String,prioriety: Int){
        if (currentLogLevel <= VERBOSE && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)){
            logout { pre -> Log.v(pre + tag, message) }
        }
    }

    fun i(tag: String,message: String,prioriety: Int){
        if (currentLogLevel <= INFO && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)){
            logout { pre -> Log.i(pre + tag, message) }
        }
    }

    fun w(tag: String,message: String,prioriety: Int){
        if (currentLogLevel <= WARN && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)){
            logout { pre -> Log.w(pre + tag, message) }
        }
    }

    fun e(tag: String,message: String,prioriety: Int){
        if (currentLogLevel <= ERROR && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)){
            logout { pre -> Log.e(pre + tag, message) }
        }
    }
    fun a(tag: String,message: String,prioriety: Int){
        if (currentLogLevel <= ASSERT && (prioriety == currentPrioriety || prioriety >= currentPrioriety && !isStrict)){
            logout { pre -> Log.wtf(pre + tag, message) }
        }
    }

    private fun logout(func:(pre:String)->Unit){
        Log.v("" + TOP_LEFT_CORNER, DOUBLE_DIVIDER)
        func.invoke("" + MIDDLE_CORNER)
        Log.v("" + BOTTOM_LEFT_CORNER, DOUBLE_DIVIDER)
    }
}
package com.x.leo.apphelper.log

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import com.x.leo.apphelper.BuildConfig
import java.util.*

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

    private val currentPriority: Int = 0 //0-OO
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
    private var doDebug: Int = 0
    private val DO_LOG = 0x0011
    private val NOT_DO_LOG = 0x0022
    fun initStatus(ctx: Context) {
        if (doDebug == 0) {
            doDebug = if (isInDebug(ctx)) {
                DO_LOG
            } else {
                NOT_DO_LOG
            }
        }
    }

    private fun isLoggable(): Boolean {
        return doDebug == DO_LOG
    }

    private fun isInDebug(ctx: Context): Boolean {
        return if (BuildConfig.DEBUG) {
            true
        } else {
            try {
                ctx.applicationInfo.flags.and(ApplicationInfo.FLAG_DEBUGGABLE) != 0
            } catch (e: Exception) {
                false
            }
        }
    }

    fun d(message: String?, priority: Int) {
        d(message, priority, 1)
    }

    private fun d(message: String?, priority: Int, extra: Int) {
        if (isLoggable() && currentLogLevel <= DEBUG && (priority == currentPriority || priority >= currentPriority && !isStrict)) {
            val tag = StackInfoUtils.getFileName(methodIndex + extra)
            logout { pre -> Log.i(pre + tag, message) }
        }
    }

    fun d(priority: Int, vararg message: String?) {
        val messageResult: String = formatMessages(message)
        d(messageResult, priority, 1)
    }

    fun d(priority: Int, message: String?, vararg args: Any?) {
        val messageResult: String = formatMessages(message, args)
        d(messageResult, priority, 1)
    }

    fun v(message: String?, priority: Int) {
        if (isLoggable() && currentLogLevel <= VERBOSE && (priority == currentPriority || priority >= currentPriority && !isStrict)) {
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.v(pre + tag, message) }
        }
    }

    fun i(message: String?, priority: Int) {
        i(message, priority, 1)
    }

    private fun i(message: String?, priority: Int, extra: Int) {
        if (isLoggable() && currentLogLevel <= INFO && (priority == currentPriority || priority >= currentPriority && !isStrict)) {
            val tag = StackInfoUtils.getFileName(methodIndex + extra)
            logout { pre -> Log.i(pre + tag, message) }
        }
    }

    fun i(priority: Int, vararg message: String?) {
        val messageResult: String = formatMessages(message)
        i(messageResult, priority, 1)
    }

    fun i(priority: Int, message: String?, vararg args: Any?) {
        val messageResult: String = formatMessages(message, args)
        i(messageResult, priority, 1)
    }

    private fun formatMessages(message: Array<out String?>): String {
        return when {
            message.isEmpty() || message[0] == null -> "null"
            message.size > 1 -> try {
                String.format(message[0]!!, *Arrays.copyOfRange(message, 1, message.size))
            } catch (e: Throwable) {
                "string format error:" + e.message
            }
            else -> message.getOrNull(0) ?: "null"
        }
    }

    private fun formatMessages(message: String?, args: Array<out Any?>): String {
        return when {
            message == null -> "null"
            args.isNotEmpty() -> try {
                String.format(message, *args)
            } catch (e: Throwable) {
                "string format error:" + e.message
            }
            else -> message
        }
    }


    fun w(message: String?, priority: Int) {
        if (isLoggable() && currentLogLevel <= WARN && (priority == currentPriority || priority >= currentPriority && !isStrict)) {
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.w(pre + tag, message) }
        }
    }

    fun w(priority: Int, vararg message: String?) {
        val messageResult: String = formatMessages(message)
        w(messageResult, priority)
    }

    private fun e(message: String?, e: Throwable?, priority: Int, extra: Int) {
        if (isLoggable() && currentLogLevel <= ERROR && (priority == currentPriority || priority >= currentPriority && !isStrict)) {
            val tag = StackInfoUtils.getFileName(methodIndex + extra)
            logout { pre ->
                val builder = StringBuilder("" + message + "\n" + e?.javaClass?.name + "\n" + e?.message + "\n")
                e?.stackTrace?.forEach {
                    try {
                        builder.append(it.className + "_" + it.methodName + "() :(" + it.fileName + ":" + it.lineNumber + ")\n")
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
                Log.e(pre + tag, builder.toString())
            }
        }
    }

    fun e(message: String?, e: Throwable?, priority: Int) {
        e(message, e, priority, 1)
    }

    fun e(priority: Int, e: Throwable?, vararg message: String?) {
        val messageResult: String = formatMessages(message)
        e(messageResult, e, priority, 1)
    }

    fun e(priority: Int, e: Throwable?, message: String?, vararg args: Any?) {
        val messageResult: String = formatMessages(message, args)
        e(messageResult, e, priority, 1)
    }

    fun a(message: String?, priority: Int) {
        if (isLoggable() && currentLogLevel <= ASSERT && (priority == currentPriority || priority >= currentPriority && !isStrict)) {
            val tag = StackInfoUtils.getFileName(methodIndex)
            logout { pre -> Log.wtf(pre + tag, message) }
        }
    }

    fun a(priority: Int, vararg message: String?) {
        val messageResult: String = formatMessages(message)
        a(messageResult, priority)
    }

    private fun logout(func: (pre: String) -> Unit) {
        try {
            Log.i("" + TOP_LEFT_CORNER, DOUBLE_DIVIDER)
            Log.i("" + HORIZONTAL_DOUBLE_LINE, "Thread:" + Thread.currentThread().name + "(" + Thread.currentThread().id + ")____" + StackInfoUtils.getMethodName(methodIndex + 2) + "()(" + StackInfoUtils.getFileName(methodIndex + 2) + ":" + StackInfoUtils.getLineNumber(methodIndex + 2) + ")")
            Log.i("" + HORIZONTAL_DOUBLE_LINE, SINGLE_DIVIDER)
            func.invoke("" + MIDDLE_CORNER)
            Log.i("" + BOTTOM_LEFT_CORNER, DOUBLE_DIVIDER)
        } catch (e: Exception) {
            Log.e("XLog", "logout error", e)
        }
    }
}
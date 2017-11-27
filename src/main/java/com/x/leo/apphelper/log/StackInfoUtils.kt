package com.x.leo.apphelper.log

/**
 * @作者:XLEO
 * @创建日期: 2017/10/24 11:30
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object StackInfoUtils{
    fun getFileName(stackTraceIndex:Int):String?{
        val stackTrace = Thread.currentThread().stackTrace
        return stackTrace[if (stackTrace.size > stackTraceIndex) stackTraceIndex else stackTrace.size - 1]?.fileName
    }
    fun getClassName(stackTraceIndex:Int):String?{
        val stackTrace = Thread.currentThread().stackTrace
        return stackTrace[if (stackTrace.size > stackTraceIndex) stackTraceIndex else stackTrace.size - 1]?.className
    }
    fun getMethodName(stackTraceIndex:Int):String?{
        val stackTrace = Thread.currentThread().stackTrace
        return stackTrace[if (stackTrace.size > stackTraceIndex) stackTraceIndex else stackTrace.size - 1]?.methodName
    }
    fun getLineNumber(stackTraceIndex:Int):Int?{
        val stackTrace = Thread.currentThread().stackTrace
        return stackTrace[if (stackTrace.size > stackTraceIndex) stackTraceIndex else stackTrace.size - 1]?.lineNumber
    }
}
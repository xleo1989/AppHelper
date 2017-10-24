package com.x.leo.apphelper.time.test

import com.x.leo.apphelper.log.LocalPrinter
import com.x.leo.apphelper.log.StackInfoUtils

/**
 * @作者:XLEO
 * @创建日期: 2017/9/8 10:03
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class TimeTest {
    var startTime: Long
    val initTime: Long
    val doLog = true
    private val stackTraceIndex = 4
    val checkPoint: LinkedHashMap<String, String> by lazy {
        LinkedHashMap<String, String>()
    }

    init {
        startTime = System.currentTimeMillis()
        initTime = startTime
    }

    fun reSet() {
        startTime = System.currentTimeMillis()
    }

    fun check(detail: String?) {
        if (doLog) {
            val stopTime = System.currentTimeMillis()
            storeCheckPoint(StackInfoUtils.getMethodName(stackTraceIndex) + "() __" + detail, stopTime - startTime)
            reSet()
        }
    }

    private fun storeCheckPoint(method: String, l: Long) {
        checkPoint.put(method, l.toString())
    }

    fun stop() {
        if (doLog) {
            val stopTime = System.currentTimeMillis()
            LocalPrinter.INSTANCE.printTime(StackInfoUtils.getClassName(stackTraceIndex) + "_", stopTime - initTime, StackInfoUtils.getMethodName(stackTraceIndex) + "()_(" + StackInfoUtils.getFileName(stackTraceIndex) + ":" + StackInfoUtils.getLineNumber(stackTraceIndex) + ")", checkPoint)
        }
    }


}
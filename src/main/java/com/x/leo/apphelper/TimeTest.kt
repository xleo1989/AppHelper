package com.x.leo.apphelper

import android.util.Log

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
class TimeTest(val obj: String) {
    var startTime: Long
    val initTime: Long
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

    fun check(method: String) {
        val stopTime = System.currentTimeMillis()
        storeCheckPoint(method, stopTime - startTime)
        reSet()
    }

    private fun storeCheckPoint(method: String, l: Long) {
        checkPoint.put(method, l.toString())
    }

    fun stop(method: String) {
        val stopTime = System.currentTimeMillis()
        LocalPrinter.INSTANCE.printTime(obj,stopTime - initTime, method,checkPoint)
    }


}
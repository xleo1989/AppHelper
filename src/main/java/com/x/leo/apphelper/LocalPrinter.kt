package com.x.leo.apphelper

import android.util.Log

/**
 * @作者:XLEO
 * @创建日期: 2017/9/20 11:12
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class LocalPrinter{
    companion object {
        val INSTANCE = LocalPrinter()
    }
    fun printTime(obj:String,l: Long, method: String,checkPoint:LinkedHashMap<String,String>?) {

        val sb = StringBuilder("=")
        var maxLength = 0
        if (checkPoint != null) {
            for (mutableEntry in checkPoint) {
                val i = mutableEntry.key.length + mutableEntry.value.length
                if (i > maxLength) {
                    maxLength = i
                }
            }
        }
        if (maxLength < method.length + l.toString().length) {
            maxLength = method.length + l.toString().length
        }
        maxLength += (3 + "_usedTime:".length + " in ms".length)
        if (obj.length + method.length + 3 > maxLength) {
            maxLength = obj.length
        }
        for (i in 0..maxLength - 1) {
            sb.append("=")
        }
        sb.append("===")
        val withPre = sb.toString().replaceFirst("==", "||", true)
        Log.d("TimeTest", sb.toString())
        Log.d("TimeTest", "||" + obj + "." + method + "()")
        if (checkPoint != null && checkPoint.size > 0) {
            Log.d("TimeTest", withPre)
            for (mutableEntry in checkPoint) {
                Log.d("TimeTest", "||" + mutableEntry.key + "()" + "_usedTime:" + mutableEntry.value + " in ms")
            }
            Log.d("TimeTest", withPre)
        }
        Log.d("TimeTest", "||total_time:" + l)
        Log.d("TimeTest", sb.toString())
        checkPoint?.clear()
    }
}
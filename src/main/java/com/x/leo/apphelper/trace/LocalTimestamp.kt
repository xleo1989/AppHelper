package com.x.leo.apphelper.trace

import android.content.Context
import com.x.leo.apphelper.documented.DocumentMessage

/**
 * @作者:XLEO
 * @创建日期: 2017/9/7 11:22
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object LocalTimestamp {
    fun addTimestamp(ctx: Context, name: String, currentTimeMillis: Long?) {
        if (currentTimeMillis == null) {
            DocumentMessage.getDoc().putLong(name, System.currentTimeMillis()).reset()
        } else {
            DocumentMessage.getDoc().putLong(name, currentTimeMillis!!).reset()
        }
    }

    fun getTimestamp(ctx: Context, name: String): String {
        val doc = DocumentMessage.getDoc()
        val long = doc.getLong(name)
        doc.reset()
        return if (long == null || long == -1L) "" + 0L else "" + long
    }
}
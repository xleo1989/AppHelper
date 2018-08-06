package com.x.leo.apphelper.utils

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import com.x.leo.apphelper.log.xlog.XLog
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.regex.Pattern


/**
 * @作者:XLEO
 * @创建日期: 2017/10/24 15:34
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class SmsContentObserver(val ctx: Context, val handler: Handler) : ContentObserver(null) {
    companion object {
        private val SMS_SEND_NUMBER = "17721165644"
        private val PATTERN = "(?<!\\d)\\d{6}(?!\\d)"
        const val NEW_SMS_COME = 1120.shl(3)
        val threadPool: ExecutorService by lazy {
            Executors.newFixedThreadPool(1)
        }
    }

    override fun onChange(selfChange: Boolean,uri: Uri) {
        if (uri.toString().contains("sms/inbox")) {
            XLog.d("inbox in",10)
        }else{
            XLog.d(uri.toString(),10)
        }
        if (AppStateUtils.isRunOnUIThread()) {
            threadPool.execute {
                handleNewMessage()
            }
        } else {
            handleNewMessage()
        }

    }

    private fun handleNewMessage() {
        XLog.d("", 10)
        val inBoxUri = Uri.parse("content://sms/inbox")
        val c = ctx.getContentResolver().query(inBoxUri,
                arrayOf(
                        "address",
                        "body"
        )
        , null, null, "date DESC limit 3")
        if (c != null) {
            if (c.moveToNext()) {
                val number = c.getString(c.getColumnIndex("address"))
                val body = c.getString(c.getColumnIndex("body"))
                XLog.d("number:" + number + "|| body:" + body, 10)
                if (TextUtils.equals(SMS_SEND_NUMBER, number)) {
                    val verifyCode = patternCode(body)
                    val msg = Message.obtain()
                    msg.what = NEW_SMS_COME
                    msg.obj = verifyCode
                    XLog.d("verifyCode:" + verifyCode,10)
                    handler.sendMessage(msg)
                }
            }
            c.close()
        }
    }

    private fun patternCode(patternContent: String): String? {
        if (TextUtils.isEmpty(patternContent)) {
            return null
        }
        val p = Pattern.compile(PATTERN)
        val matcher = p.matcher(patternContent)
        return if (matcher.find()) {
            matcher.group()
        } else null
    }
}


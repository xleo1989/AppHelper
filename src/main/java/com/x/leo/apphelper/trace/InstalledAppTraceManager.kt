package com.x.leo.apphelper.trace

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.x.leo.apphelper.log.xlog.XLog
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * @作者:XLEO
 * @创建日期: 2017/9/6 16:51
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object InstalledAppTraceManager {
    fun obtainAndUpload(act: Activity) {
        TraceSender.sendInfo(act.applicationContext, getInstallApp(act))
    }

    private val UPDATETIME: String = "installed app trace update time"

    /**
     * Get the installed Apps info

     * @param mActivity
     * *
     * @return
     */
    fun getInstallApp(mActivity: Activity): JSONObject {
        XLog.d("begin get install app->",10)

        val installAppEntity = JSONObject()
        var totalNumber: Long = 0L

        val installAppArray = JSONArray()
        val packageInfoList = mActivity.packageManager.getInstalledPackages(0)

        for (packageInfo in packageInfoList) {
            if ((packageInfo.applicationInfo.flags.and(ApplicationInfo.FLAG_SYSTEM)) != 0) {
                continue
            }
            val launchIntentForPackage = mActivity.packageManager.getLaunchIntentForPackage(packageInfo.packageName)
            if (launchIntentForPackage == null || !launchIntentForPackage.hasCategory("android.intent.category.LAUNCHER") || !"android.intent.action.MAIN".equals(launchIntentForPackage.action)) {
                continue
            }
            val appInfo = JSONObject()
            try {
                totalNumber++
                appInfo.put("appName", packageInfo.applicationInfo.loadLabel(mActivity.packageManager).toString())
                appInfo.put("packageName", packageInfo.packageName)
                appInfo.put("versionName", packageInfo.versionName)
                appInfo.put("versionCode", packageInfo.versionCode)
                // getAppUseFrequence(packageInfo, mActivity)
                installAppArray.put(appInfo)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        try {
            val packageInfo = mActivity.packageManager.getPackageInfo(mActivity.packageName, 0)
            installAppEntity.put("protocolName", ProtocolName.INSTALLED_APP)
            installAppEntity.put("protocolVersion", ProtocolVersion.V_1_0)
            installAppEntity.put("versionName", packageInfo.versionName)

            installAppEntity.put("totalNumber", totalNumber)
            val currentTime = System.currentTimeMillis()
            installAppEntity.put("latestTime", currentTime)
            installAppEntity.put("earliestTime", LocalTimestamp.getTimestamp(mActivity.applicationContext,UPDATETIME))
            LocalTimestamp.addTimestamp(mActivity.applicationContext, UPDATETIME,currentTime)
            installAppEntity.put("data", installAppArray)
        } catch (ex: JSONException) {
            XLog.d("JSONException: " + ex.message,10)
            ex.printStackTrace()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            XLog.d("PackageManager.NameNotFoundException: " + e.message,10)
        }

        return installAppEntity

    }

    private fun getAppUseFrequence(packageInfo: PackageInfo, mActivity: Activity) {

    }
}
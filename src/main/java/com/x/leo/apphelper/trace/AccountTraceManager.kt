package com.x.leo.apphelper.trace
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context

import org.json.JSONArray
import org.json.JSONObject

/**
 * @作者:XLEO
 * @创建日期: 2017/9/7 11:09
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object AccountTraceManager {
    val UPLOADTIME:String = "account trace uploadTime"
    @SuppressLint("MissingPermission")
    fun getAccountList(act: Activity) {
        val accounts = (act.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager).accounts
        val accountArray = JSONArray()
        if (accounts != null) {
            for (account in accounts) {
                val accountObj = JSONObject()
                accountObj.put("account_name", account.name)
                accountObj.put("account_type", account.type)
                accountArray.put(accountObj)
            }
        }
        val resultObj = JSONObject()
        resultObj.put("protocolName", ProtocolName.ACCOUNTS)
        resultObj.put("protocolVersion", ProtocolVersion.CURRENT_VERSON.customName)
        try {
            val packageInfo = act.packageManager.getPackageInfo(act.packageName, 0)
            resultObj.put("versionName", packageInfo.versionName)
        }catch (e:Exception){
            resultObj.put("versionName","UNKNOW_ERROR")
        }

        resultObj.put("totalNumber", accountArray.length())
        val currentTimeMillis = System.currentTimeMillis()
        resultObj.put("latestTime", currentTimeMillis)
        resultObj.put("earliestTime", LocalTimestamp.getTimestamp(act.applicationContext, UPLOADTIME))
        LocalTimestamp.addTimestamp(act.applicationContext, UPLOADTIME, currentTimeMillis)
        resultObj.put("data", accountArray)
        TraceSender.sendInfo(act.applicationContext,resultObj)
    }
}
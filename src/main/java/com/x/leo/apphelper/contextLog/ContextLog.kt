@file:Suppress("DEPRECATION")

package com.x.leo.apphelper.contextLog

import android.Manifest
import android.annotation.SuppressLint
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.x.leo.apphelper.utils.BrandUtils
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*

/**
 * Created by XLEO on 2018/2/28.
 */
open class ContextLog {

    /**
     * 初始化环境信息
     */
    @SuppressLint("NewApi")
    fun writeDownContext(ctx: Context) {
        val applicationContext = ctx.applicationContext
        val sp = applicationContext.getSharedPreferences(_sp_file_name, Context.MODE_PRIVATE)
        val sdkINT = Build.VERSION.SDK_INT
        val deviceBrand = BrandUtils.getSystemInfo().os
        val heightPixels = applicationContext.resources.displayMetrics.heightPixels
        val widthPixels = applicationContext.resources.displayMetrics.widthPixels
        val packageInfo = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, PackageManager.GET_PERMISSIONS)
        val sb1 = StringBuffer("[")
        packageInfo.permissions?.forEach {
            sb1.append("{").append("name:").append(it.name).append(",status:")
                    .append(checkPermission(sdkINT, applicationContext, it.name))
                    .append("},")

        }
        sb1.deleteCharAt(sb1.lastIndexOf(","))
        sb1.append("]")
        sp.edit().putInt(ConfigHolder.DEVICE_SDK, sdkINT)
                .putString(ConfigHolder.APP_PERMISSIONS, sb1.toString())
                .putInt(ConfigHolder.DEVICE_SCREEN_WIDTH, widthPixels)
                .putInt(ConfigHolder.DEVICE_SCREEN_HEIGHT, heightPixels)
                .putString(ConfigHolder.DEVICE_BRAND, deviceBrand)
                .apply()
        val firstLunchTime = sp.getLong(ConfigHolder.APP_FIRST_OPEN_TIME, -1L)
        if (firstLunchTime == -1L || firstLunchTime < 1000000) {
            sp.edit().putLong(ConfigHolder.APP_FIRST_OPEN_TIME, System.currentTimeMillis()).apply()
        }

        initialIDs(applicationContext, sp)
        if (checkPermission(sdkINT, applicationContext, Manifest.permission.ACCESS_NETWORK_STATE)) {
            val connManager: ConnectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val allNetworks = connManager.allNetworks
            var isAvalable = false
            allNetworks?.forEach {
                val networkInfo = connManager.getNetworkInfo(it)
                if (networkInfo.state == NetworkInfo.State.CONNECTED && (networkInfo.type == ConnectivityManager.TYPE_WIFI || networkInfo.type == ConnectivityManager.TYPE_MOBILE)) {
                    isAvalable = true
                }
            }
            sp.edit().putBoolean(ConfigHolder.NETWORK_STATUS, isAvalable).apply()
        }
        determinationDeviceType(applicationContext, sp)
    }

    /**
     * 设备类型判定
     */
    private fun determinationDeviceType(ctx: Context, sp: SharedPreferences) {
        //TODO
    }

    /**
     * 权限检查
     */
    @SuppressLint("NewApi")
    private fun checkPermission(sdkINT: Int, ctx: Context, permission: String): Boolean {
        return if (sdkINT >= Build.VERSION_CODES.M) {
            ctx.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * 获取设备惟一化的签名
     */
    @Throws(PermissionDeniedException::class, IllegalStateException::class)
    fun getSingleSignature(ctx: Context): String {
        val sp = obtainLogSp(ctx)
        var sig = sp.getString(ConfigHolder.DEVICE_CUSTOME_SIGNATURE, null)
        if (TextUtils.isEmpty(sig)) {
            initialIDs(ctx.applicationContext, sp)
            val uuid = sp.getString(ConfigHolder.UUID_ID, null) ?: throw IllegalStateException("no uuid found in config file")
            val deviceId = sp.getString(ConfigHolder.DEVICE_ID, uuid)
            val serialID = sp.getString(ConfigHolder.DEVICE_BUILD_SERIAL, uuid)
            val androidId = sp.getString(ConfigHolder.ANDROID_ID, uuid)
            val serialSim = sp.getString(ConfigHolder.MAIN_SIM_SERIAL, uuid)
            val originResult = StringBuilder(uuid).append(deviceId).append(serialID)
                    .append(androidId)
                    .append(serialID)
                    .append(serialSim)
                    .toString()
            val md = MessageDigest.getInstance("MD5")
            md.update(originResult.toByteArray(Charset.forName("UTF-8")))
            val tempResult = md.digest()
            val result = StringBuffer()
            tempResult?.forEach {
                val temp: Int = 0xFF.and(it.toInt())
                if (temp <= 0xF) {
                    result.append("0")
                }
                result.append(Integer.toHexString(temp))
            }
            if (TextUtils.isEmpty(result.toString())) {
                throw IllegalStateException("digest result is empty")
            }
            sig = result.toString()
            sp.edit().putString(ConfigHolder.DEVICE_CUSTOME_SIGNATURE, sig).apply()
        }
        return sig
    }

    /**
     * 获取设备id信息和sim信息
     */
    @SuppressLint("NewApi")
    @Throws(PermissionDeniedException::class)
    private fun initialIDs(ctx: Context, sp: SharedPreferences) {
        var sdkINT = sp.getInt(ConfigHolder.DEVICE_SDK, -1)
        if (sdkINT == -1) {
            sdkINT = Build.VERSION.SDK_INT
            sp.edit().putInt(ConfigHolder.DEVICE_SDK, sdkINT)
        }

        var uuidID = sp.getString(ConfigHolder.UUID_ID, null)
        if (TextUtils.isEmpty(uuidID)) {
            uuidID = UUID.randomUUID().toString()
            sp.edit().putString(ConfigHolder.UUID_ID, uuidID).apply()
        }
        var androidId = sp.getString(ConfigHolder.ANDROID_ID, null)
        if (!TextUtils.isEmpty(androidId)) {
            androidId = Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID)
            if (TextUtils.isEmpty(androidId)) {
                androidId = UUID.randomUUID().toString()
            }
            sp.edit().putString(ConfigHolder.ANDROID_ID, androidId).apply()
        }
        if (checkPermission(sdkINT, ctx, Manifest.permission.READ_PHONE_STATE)) {
            val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var deviceId = sp.getString(ConfigHolder.DEVICE_ID, null)
            if (!TextUtils.isEmpty(deviceId)) {
                deviceId = tm.deviceId
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = UUID.randomUUID().toString()
                }
                sp.edit().putString(ConfigHolder.DEVICE_ID, deviceId).apply()
            }
            var simCount = 1
            if (sdkINT >= 23) {
                simCount = tm.phoneCount
            }
            when (simCount) {
                0 ->
                    return
                1 ->
                    initialDefaultSimInfo(tm, sp)
                else -> {
                    initialDefaultSimInfo(tm, sp)
                    initialExtraSimInfo(tm, sp)
                }
            }


        } else {
            throw PermissionDeniedException("no read phone state permission")
        }
    }

    /**
     * 获取sim2卡信息
     */
    private fun initialExtraSimInfo(tm: TelephonyManager, sp: SharedPreferences) {
        if (sp.getInt(ConfigHolder.DEVICE_SDK, -1) >= 23) {
            //TODO
        }
    }

    /**
     * 获取主sim卡信息
     */
    @SuppressLint("MissingPermission")
    private fun initialDefaultSimInfo(tm: TelephonyManager, sp: SharedPreferences) {
        var phoneType = sp.getString(ConfigHolder.DEVICE_MAIN_PHONE_TYPE, null)
        if (TextUtils.isEmpty(phoneType)) {
            phoneType = when (tm.phoneType) {
                TelephonyManager.PHONE_TYPE_CDMA ->
                    "CDMA"
                TelephonyManager.PHONE_TYPE_GSM ->
                    "GSM"
                TelephonyManager.PHONE_TYPE_SIP ->
                    "SIP"
                TelephonyManager.PHONE_TYPE_NONE ->
                    "NONE"
                else -> "NONE"
            }
            sp.edit().putString(ConfigHolder.DEVICE_MAIN_PHONE_TYPE, phoneType).apply()
        }

        if (!TextUtils.equals(phoneType, "NONE")) {
            var simSerial = sp.getString(ConfigHolder.MAIN_SIM_SERIAL, null)
            if (simSerial == null) {
                simSerial = tm.simSerialNumber
                if (simSerial != null) {
                    sp.edit().putString(ConfigHolder.MAIN_SIM_SERIAL, simSerial).apply()
                }
            }
        }
        var line1Number = sp.getString(ConfigHolder.DEVICE_MAIN_PHONE_NUMBEN, null)
        if (TextUtils.isEmpty(line1Number)) {
            line1Number = tm.line1Number
            if (line1Number != null && TextUtils.getTrimmedLength(line1Number) > 5) {
                sp.edit().putString(ConfigHolder.DEVICE_MAIN_PHONE_NUMBEN, line1Number).apply()
            }
        }

        var subscriberId = sp.getString(ConfigHolder.DEVICE_MAIN_PHONE_SERVER, null)
        if (TextUtils.isEmpty(subscriberId)) {
            subscriberId = tm.subscriberId
            if (!TextUtils.isEmpty(subscriberId)) {
                sp.edit().putString(ConfigHolder.DEVICE_MAIN_PHONE_SERVER, subscriberId).apply()
            }
        }
    }

    companion object {

        private val _sp_file_name = "app_context"
        /**
         * 获取配置文件sp
         */
        fun obtainLogSp(ctx: Context): SharedPreferences {
            return ctx.applicationContext.getSharedPreferences(_sp_file_name, Context.MODE_PRIVATE)
        }

    }

    /**
     * 获取全部的格式化的配置信（JSON）
     */
    fun logoutContext(ctx: Context): String {
        val sp = obtainLogSp(ctx.applicationContext)
        val sb2 = StringBuffer("[")
        val all = sp.all
        all?.keys?.forEach {
            sb2.append("{name:").append(it)
                    .append(",value:").append(all[it])
                    .append("},")
        }

        sb2.deleteCharAt(sb2.lastIndexOf(","))
        sb2.append("]")
        return sb2.toString()
    }
}
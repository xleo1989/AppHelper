@file:Suppress("DEPRECATION")

package com.x.leo.apphelper.contextLog

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.x.leo.apphelper.log.xlog.XLog
import com.x.leo.apphelper.utils.BrandUtils
import com.x.leo.apphelper.utils.ThreadPoolManager
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
        sp.edit().putInt(ConfigHolder.DEVICE_SDK, sdkINT)
                .putInt(ConfigHolder.DEVICE_SCREEN_WIDTH, widthPixels)
                .putInt(ConfigHolder.DEVICE_SCREEN_HEIGHT, heightPixels)
                .putString(ConfigHolder.DEVICE_BRAND, deviceBrand)
                .apply()
        val firstLunchTime = sp.getLong(ConfigHolder.APP_FIRST_OPEN_TIME, -1L)
        if (firstLunchTime == -1L || firstLunchTime < 1000000) {
            sp.edit().putLong(ConfigHolder.APP_FIRST_OPEN_TIME, System.currentTimeMillis()).apply()
        }
        permissionCollect(applicationContext, sp, sdkINT)

        try {
            initialIDs(applicationContext, sp)
        } catch (e: Exception) {
            XLog.e("initial ids error:" + e.message, e, 100)
        }

        collectNetworkInfo(sdkINT, applicationContext, sp)

        determinationDeviceType(applicationContext, sp)

        collectHardwareInfo(applicationContext, sp)
    }

    private fun collectNetworkInfo(sdkINT: Int, applicationContext: Context, sp: SharedPreferences) {
        if (checkPermission(sdkINT, applicationContext, Manifest.permission.ACCESS_NETWORK_STATE)) {
            val connManager: ConnectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val allNetworks = connManager.allNetworkInfo
            var isAvalable = false
            allNetworks?.forEach {
                if (it.state == NetworkInfo.State.CONNECTED && (it.type == ConnectivityManager.TYPE_WIFI || it.type == ConnectivityManager.TYPE_MOBILE)) {
                    isAvalable = true
                }
            }
            sp.edit().putBoolean(ConfigHolder.NETWORK_STATUS, isAvalable).apply()
        }
    }

    private fun collectHardwareInfo(applicationContext: Context, sp: SharedPreferences) {
        var cpuinfo = sp.getString(ConfigHolder.CPU_SERIAL, null)
        if (TextUtils.isEmpty(cpuinfo)) {

            ThreadPoolManager.runWithThread(Runnable {
                try {
                    cpuinfo = HardwareInfoReader().getCpuSerial()
                    if (cpuinfo != null) {
                        sp.edit().putString(ConfigHolder.CPU_SERIAL, cpuinfo).apply()
                        XLog.i(cpuinfo, 10)
                    }
                } catch (e: Exception) {
                    XLog.e("obtain cpu info error:" + e.message, e, 100)
                }
            })
        }
        var macAddress = sp.getString(ConfigHolder.MAC_ADDRESS, null)
        if (TextUtils.isEmpty(macAddress)) {
            try {
                macAddress = HardwareInfoReader().getMac()
                if (!TextUtils.isEmpty(macAddress)) {
                    sp.edit().putString(ConfigHolder.MAC_ADDRESS, macAddress).apply()
                }
            } catch (e: Exception) {
                XLog.e("obtain mac address error:" + e.message, e, 100)
            }
        }
        var hardwareSerial = sp.getString(ConfigHolder.HARDWARE_SERIAL, null)
        if (TextUtils.isEmpty(hardwareSerial)) {
            hardwareSerial = HardwareInfoReader().getHardwareSerial()
            if (!TextUtils.isEmpty(hardwareSerial)) {
                sp.edit().putString(ConfigHolder.HARDWARE_SERIAL, hardwareSerial).apply()
            }
        }
        var isRooted = sp.getBoolean(ConfigHolder.DEVICE_IS_ROOTED, false)
        if (!isRooted) {
            isRooted = HardwareInfoReader().isRooted()
            sp.edit().putBoolean(ConfigHolder.DEVICE_IS_ROOTED, isRooted).apply()
        }
    }

    /**
     * 权限信息收集
     */
    private fun permissionCollect(ctx: Context, sp: SharedPreferences, sdkINT: Int) {
        val allPermissionGroups = ctx.packageManager.getAllPermissionGroups(PackageManager.GET_META_DATA)
        val sb1 = StringBuffer("[")
        allPermissionGroups?.forEach {
            try {
                val desString = it.name
                if (desString != null) {
                    sb1.append("{").append("name:").append(desString).append(",status:")
                            .append(checkPermission(sdkINT, ctx, desString.toString()))
                            .append("},")
                }
            } catch (e: Exception) {
                XLog.e("permissionCollect:" + e.message, e, 100)
            }

        }
        if (sb1.contains(",")) {
            sb1.deleteCharAt(sb1.lastIndexOf(","))
        }
        sb1.append("]")
        sp.edit().putString(ConfigHolder.APP_PERMISSIONS, sb1.toString()).apply()
    }

    /**
     * 设备类型判定
     */
    private fun determinationDeviceType(ctx: Context, sp: SharedPreferences) {
        var type = sp.getString(ConfigHolder.DEVICE_TYPE, null)
        if (TextUtils.isEmpty(type)) {
            val hardwareInfoReader = HardwareInfoReader()
            if (hardwareInfoReader.isTablet(ctx)) {
                type = DeviceType.TABLET.name
            } else if (hardwareInfoReader.isTv(ctx)) {
                type = DeviceType.TV.name
            }else{
                type = DeviceType.PHONE.name
            }
        }
        if (!TextUtils.isEmpty(type)) {
            sp.edit().putString(ConfigHolder.DEVICE_TYPE, type).apply()
        }
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
            val deviceId = if (!TextUtils.isEmpty(sp.getString(ConfigHolder.DEVICE_ID, null))) {
                sp.getString(ConfigHolder.DEVICE_ID, null)
            } else {
                sp.getString(ConfigHolder.EXTRA_DEVICE_ID, UUID.randomUUID().toString())
            }
            val serialID = sp.getString(ConfigHolder.DEVICE_BUILD_SERIAL, UUID.randomUUID().toString())
            val androidId = sp.getString(ConfigHolder.ANDROID_ID, UUID.randomUUID().toString())
            val serialSim = if (!TextUtils.isEmpty(sp.getString(ConfigHolder.MAIN_SIM_SERIAL, null))) {
                sp.getString(ConfigHolder.MAIN_SIM_SERIAL, null)
            } else {
                sp.getString(ConfigHolder.EXTRA_SIM_SERIAL, UUID.randomUUID().toString())
            }
            val originResult = StringBuilder().append(deviceId).append(serialID)
                    .append(androidId)
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

        val clazz = tm.javaClass
        var subId = -1
        var subscriberId = sp.getString(ConfigHolder.DEVICE_EXTRA_PHONE_SERVER, null)
        if (TextUtils.isEmpty(subscriberId)) {
            try {
                val getSubscriberId = clazz.getDeclaredMethod("getSubscriberId", Int::class.java)
                val invoke = clazz.getDeclaredMethod("getSubId").invoke(tm)
                if (invoke != null && invoke is Int && invoke in 0 until 10) {
                    (0 until 10)
                            .filter { it != invoke }
                            .forEach {
                                try {
                                    val invoke1 = getSubscriberId.invoke(tm, it)
                                    if (invoke1 != null && invoke is String) {
                                        subId = it
                                        subscriberId = invoke
                                    }
                                } catch (e: Exception) {
                                    XLog.e("extra subscriberid obtain error:" + e.message, e, 100)
                                }
                            }

                }
                if (!TextUtils.isEmpty(subscriberId)) {
                    sp.edit().putString(ConfigHolder.DEVICE_MAIN_PHONE_SERVER, subscriberId).apply()
                }
            } catch (e: Exception) {
                XLog.e("obtain extra subscriberid error:" + e.message, e, 100)
            }
        }


        var phoneType = sp.getString(ConfigHolder.DEVICE_EXTRA_PHONE_TYPE, null)
        var slotID: Int = -1
        if (TextUtils.isEmpty(phoneType) && subId != -1) {
            try {
                val invoke1 = SubscriptionManager::class.java.getDeclaredMethod("getPhoneId", Int::class.java)
                        .invoke(null, subId)
                if (invoke1 != null && invoke1 is Int) {
                    slotID = invoke1
                    val invoke = clazz.getDeclaredMethod("getCurrentPhoneTypeForSlot", Int::class.java)
                            .invoke(tm, slotID)
                    if (invoke != null && invoke is Int) {
                        phoneType = when (invoke) {
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
                        sp.edit().putString(ConfigHolder.DEVICE_EXTRA_PHONE_TYPE, phoneType).apply()
                    }
                }
            } catch (e: Exception) {
                XLog.e("obtain extra phone type error:" + e.message, e, 100)
            }
        }


        if (!TextUtils.equals(phoneType, "NONE") && subId != -1) {
            var simSerial = sp.getString(ConfigHolder.EXTRA_SIM_SERIAL, null)
            if (TextUtils.isEmpty(simSerial)) {
                try {
                    val invoke = clazz.getDeclaredMethod("getSimSerialNumber", Int::class.java)
                            .invoke(tm, subId)
                    if (invoke != null && invoke is String) {
                        simSerial = invoke
                    }

                    if (!TextUtils.isEmpty(simSerial)) {
                        sp.edit().putString(ConfigHolder.EXTRA_SIM_SERIAL, simSerial).apply()
                    }
                } catch (e: Exception) {
                    XLog.e("obtain extra sim serial error:" + e.message, e, 100)
                }
            }
        }
        var line1Number = sp.getString(ConfigHolder.DEVICE_EXTRA_PHONE_NUMBER, null)
        if (TextUtils.isEmpty(line1Number) && subId != -1) {
            try {
                val invoke = clazz.getDeclaredMethod("getLine1Number", Int::class.java).invoke(tm, subId)
                if (invoke != null && invoke is String && TextUtils.getTrimmedLength(line1Number) > 5) {
                    line1Number = invoke
                    sp.edit().putString(ConfigHolder.DEVICE_EXTRA_PHONE_NUMBER, line1Number).apply()
                }
            } catch (e: Exception) {
                XLog.e("obtain extra phone number error:" + e.message, e, 100)
            }
        }
        if (!TextUtils.equals(phoneType, "NONE") && !TextUtils.isEmpty(phoneType) && slotID != -1) {
            try {
                val deviceId = clazz.getDeclaredMethod("getDeviceId", Int::class.java)
                        .invoke(tm, 1)
                if (deviceId != null && deviceId is String) {
                    sp.edit().putString(ConfigHolder.EXTRA_DEVICE_ID, deviceId).apply()
                }
            } catch (e: Exception) {
                XLog.e("obtain extra device id error:" + e.message, e, 100)
            }
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

        var deviceId = sp.getString(ConfigHolder.DEVICE_ID, null)
        if (!TextUtils.isEmpty(deviceId)) {
            deviceId = tm.deviceId
            if (!TextUtils.isEmpty(deviceId)) {
                sp.edit().putString(ConfigHolder.DEVICE_ID, deviceId).apply()
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
        if (sb2.contains(",")) {
            sb2.deleteCharAt(sb2.lastIndexOf(","))
        }
        sb2.append("]")
        return sb2.toString()
    }
}
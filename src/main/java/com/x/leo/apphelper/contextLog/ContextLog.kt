@file:Suppress("DEPRECATION")

package com.x.leo.apphelper.contextLog

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.x.leo.apphelper.utils.BrandUtils
import java.util.*

/**
 * Created by XLEO on 2018/2/28.
 */
open class ContextLog {
    private val _sp_file_name = "app_context"
    fun writeDownContext(ctx: Context) {
        val applicationContext = ctx.applicationContext
        val sp = applicationContext.getSharedPreferences(_sp_file_name, Context.MODE_PRIVATE)
        val sdkINT = Build.VERSION.SDK_INT
        val deviceBrand = BrandUtils.getSystemInfo().os

        sp.edit().putInt(ConfigHolder.DEVICE_SDK, sdkINT)
                .putString(ConfigHolder.DEVICE_BRAND, deviceBrand)
                .apply()
        initialIDs(applicationContext,sp)

    }

    @SuppressLint("NewApi")
    private fun checkPermission(sdkINT: Int, ctx: Context, permission: String): Boolean {
        return if (sdkINT >= Build.VERSION_CODES.M) {
            ctx.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    @Throws(PermissionDeniedException::class)
    fun getSingleSignature(ctx: Context):String{
        val sp = obtainLogSp(ctx)
        var sig = sp.getString(ConfigHolder.DEVICE_CUSTOME_SIGNATURE, null)
        if (TextUtils.isEmpty(sig)) {
            var uuid = sp.getString(ConfigHolder.UUID_ID,null)
            if (uuid == null) {
               uuid = UUID.randomUUID().toString()
                sp.edit().putString(ConfigHolder.UUID_ID,uuid).apply()
            }
            initialIDs(ctx.applicationContext,sp)
            val deviceId = sp.getString(ConfigHolder.DEVICE_ID,uuid)
            val serialID = sp.getString(ConfigHolder.DEVICE_BUILD_SERIAL,uuid)
            val androidId = sp.getString(ConfigHolder.ANDROID_ID,uuid)
        }
        return sig
    }

    @SuppressLint("NewApi")
    @Throws(PermissionDeniedException::class)
    private fun initialIDs(ctx: Context, sp: SharedPreferences) {
        var sdkINT = sp.getInt(ConfigHolder.DEVICE_SDK,-1)
        if (sdkINT == -1) {
            sdkINT = Build.VERSION.SDK_INT
            sp.edit().putInt(ConfigHolder.DEVICE_SDK,sdkINT)
        }

        var uuidID = sp.getString(ConfigHolder.UUID_ID,null)
        if (TextUtils.isEmpty(uuidID)) {
            uuidID = UUID.randomUUID().toString()
            sp.edit().putString(ConfigHolder.UUID_ID,uuidID).apply()
        }
        var androidId = sp.getString(ConfigHolder.ANDROID_ID,null)
        if (!TextUtils.isEmpty(androidId)) {
            androidId = Settings.Secure.getString(ctx.contentResolver,Settings.Secure.ANDROID_ID)
            if (TextUtils.isEmpty(androidId)) {
                androidId = UUID.randomUUID().toString()
            }
            sp.edit().putString(ConfigHolder.ANDROID_ID,androidId).apply()
        }
        if (checkPermission(sdkINT, ctx, Manifest.permission.READ_PHONE_STATE)) {
            val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var deviceId = sp.getString(ConfigHolder.DEVICE_ID,null)
            if (!TextUtils.isEmpty(deviceId)) {
                deviceId = tm.deviceId
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = UUID.randomUUID().toString()
                }
                sp.edit().putString(ConfigHolder.DEVICE_ID, deviceId).apply()
            }
            var simCount = 1
            if (sdkINT >= 23){
                simCount = tm.phoneCount
            }
            when(simCount){
                0->
                        return
                1->
                        initialDefaultSimInfo(tm,sp)
                else-> {
                    initialDefaultSimInfo(tm, sp)
                    initialExtraSimInfo(tm, sp)
                }
            }


        }else{
            throw PermissionDeniedException("no read phone state permission")
        }
    }

    private fun initialExtraSimInfo(tm: TelephonyManager, sp: SharedPreferences) {
    }

    @SuppressLint("MissingPermission")
    private fun initialDefaultSimInfo(tm: TelephonyManager, sp: SharedPreferences) {
        var phoneType = sp.getString(ConfigHolder.DEVICE_MAIN_PHONE_TYPE,null)
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

        if (!TextUtils.equals(phoneType,"NONE")) {
            var simSerial = sp.getString(ConfigHolder.SIM_SERIAL,null)
            if (simSerial == null) {
                simSerial = tm.simSerialNumber
                if (simSerial != null) {
                    sp.edit().putString(ConfigHolder.SIM_SERIAL,simSerial).apply()
                }
            }
        }
        val line1Number = tm.line1Number
        if (line1Number != null && TextUtils.getTrimmedLength(line1Number) > 5) {
            sp.edit().putString(ConfigHolder.DEVICE_MAIN_PHONE_NUMBEN,line1Number).apply()
        }
        val subscriberId = tm.subscriberId
    }

    fun obtainLogSp(ctx: Context):SharedPreferences{
        return ctx.applicationContext.getSharedPreferences(_sp_file_name,Context.MODE_PRIVATE)
    }

    fun logoutContext(ctx: Context) {

    }
}
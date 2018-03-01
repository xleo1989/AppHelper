package com.x.leo.apphelper.contextLog

import android.content.Context
import android.content.res.Configuration
import com.x.leo.apphelper.log.XLog
import java.io.*
import android.app.UiModeManager
import android.os.Build
import android.text.TextUtils
import android.bluetooth.BluetoothAdapter


/**
 * Created by XLEO on 2018/3/1.
 */
open class HardwareInfoReader {
    /**
     * 获取CPU的信息(架构 eg.AArch64)
     *
     * @return
     * adb shell cat /proc/cpuinfo
     */
    fun getCpuInfo(): String? {
        var cpuInfo: String? = ""
        try {
            if (File("/proc/cpuinfo").exists()) {
                val fr = FileReader("/proc/cpuinfo")
                val localBufferedReader = BufferedReader(fr, 8192)
                cpuInfo = localBufferedReader.readLine()
                localBufferedReader.close()

                if (cpuInfo != null) {
                    cpuInfo = cpuInfo!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                }
            }
        } catch (e: Exception) {
            XLog.e("get cpu info error:" + e.message, e, 100)
        }
        return cpuInfo
    }

    /**
     * 获取cpu序列号
     */
    @Deprecated("useless")
    fun getCpuSerial(): String? {
        var cpuSerial: String? = null
        try {
            if (File("/proc/cpuinfo").exists()) {
                val pp = ProcessBuilder().command("cat /proc/cpuinfo").start()
                val ir = InputStreamReader(pp.inputStream)
                val localBufferedReader = BufferedReader(ir, 8192)
                var line: String? = localBufferedReader.readLine()
                var i = 0
                while (line != null && i < 100) {
                    XLog.i(line, 10)
                    if (line.contains("Serial") && line.contains(":")) {
                        cpuSerial = line.substring(line.indexOf(":") + 1, line.length).trim()
                        break
                    }
                    line = localBufferedReader.readLine()
                    i++
                }
                localBufferedReader.close()
            }
        } catch (e: Exception) {
            XLog.e("get cpu info error:" + e.message, e, 100)
        }
        return if (TextUtils.isEmpty(cpuSerial?.replace("0", "")?.trim())) {
            null
        } else {
            cpuSerial
        }

    }

    /**
     * 获取MAC地址
     * 三星的目录为"cat /sys/class/net/eth0/address"
     */
    fun getMac(): String? {
        var macSerial: String? = null
        if (File("/sys/class/net/wlan0/address").exists()) {
            try {
                var str: String? = ""
                val pp = ProcessBuilder().command(
                        "cat /sys/class/net/wlan0/address ").start()
                val ir = InputStreamReader(pp.inputStream)
                val input = LineNumberReader(ir)
                while (null != str) {
                    str = input.readLine()
                    if (str != null) {
                        macSerial = str!!.trim { it <= ' ' }// 去空格
                        break
                    }
                }
                if (TextUtils.isEmpty(macSerial)) {
                    var str2: String? = ""
                    val pp2 = ProcessBuilder().command(
                            "cat /sys/class/net/wlan0/address ").start()
                    val ir2 = InputStreamReader(pp2.inputStream)
                    val input2 = LineNumberReader(ir2)
                    while (null != str) {
                        str2 = input2.readLine()
                        if (str2 != null) {
                            macSerial = str2!!.trim { it <= ' ' }// 去空格
                            break
                        }
                    }
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }

        return macSerial
    }

    /**
     * 判断是否是平板电脑
     *
     * @param context
     * @return
     */
    fun isTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    /**
     * 判断是否是tv
     */
    fun isTv(ctx: Context): Boolean {
        val uiModeManager = ctx.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    }

    /**
     * 获取设备serial
     */
    fun getHardwareSerial(): String? {
        var serialNumber: String? = null
        try {
            serialNumber = Build.SERIAL
            if (TextUtils.isEmpty(serialNumber?.replace("0", "")?.trim())) {
                val c = Class.forName("android.os.SystemProperties")
                val get = c.getMethod("get", String::class.java, String::class.java)
                serialNumber = get.invoke(c, "sys.serialnumber", "Error") as String
                if (TextUtils.equals(serialNumber, "Error")) {
                    serialNumber = get.invoke(c, "ril.serialnumber", "Error") as String
                }
            }
        } catch (e: Exception) {
            XLog.e("obtain Hardware Serial error:" + e.message, e, 100)
        }
        return if (TextUtils.equals(serialNumber, "Error")) {
            null
        } else serialNumber
    }

    /**
     * 判断系统是否已root
     */
    fun isRooted(): Boolean {
        try {
            if ((File("/sbin/su").exists()
                    || File("/system/bin/su").exists()
                    || File("/system/xbin/su").exists()
                    || File("/data/local/xbin/su").exists()
                    || File("/data/local/bin/su").exists()
                    || File("/system/sd/xbin/su").exists()
                    || File("/system/bin/failsafe/su").exists()
                    || File("/data/local/su").exists()
                    || File("/su/bin/su").exists())) {
                return true
            }
        } catch (e: Exception) {
            XLog.e("root detect error:" + e.javaClass.simpleName + ":" + e.message, e, 100)
        }
        try {
            ProcessBuilder().command("su").start()
            return true
        } catch (e: Exception) {
            XLog.e("exec su error:", e, 100)
        }
        //test-keys为root手机或第三方ROM
        if (TextUtils.equals("test-keys", Build.TAGS)) {
        }

        return false
    }

    /**
     * 获取bluetooth mac
     * eg. android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
     */
    private fun getBtAddressViaReflection(): String? {
        var address: String? = null
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        try {
            val mService = BluetoothAdapter::class.java.getDeclaredField("mService").get(bluetoothAdapter)
            if (mService == null) {
                XLog.i("couldn't find bluetoothManagerService", 100)
                return null
            }
            address = mService::class.java.getDeclaredMethod("getAddress").invoke(mService) as String
        } catch (e: Exception) {
            XLog.e("obtain bluetooth address error:", e, 100)
        }
        return if (!TextUtils.isEmpty(address)) {
            XLog.i("using reflection to get the BT MAC address: " + address!!, 10)
            address
        } else {
            null
        }
    }

}
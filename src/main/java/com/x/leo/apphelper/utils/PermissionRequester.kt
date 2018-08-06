package com.x.leo.apphelper.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.app.Fragment
import com.x.leo.apphelper.log.xlog.XLog
import rx.Subscriber

/**
 * Created by XLEO on 2017/12/13.
 */
class PermissionRequester {
    fun requestPermissions(activity: Activity, permissions: Array<String>, code: Int, subscriber: Subscriber<Map<String, Boolean>>) {
        try {
            myWay(activity, code, subscriber, permissions)
        } catch (e: Throwable) {
            XLog.e(e.message ?: e.localizedMessage, e, 100)
        }
    }

    private fun myWay(activity: Activity, code: Int, subscriber: Subscriber<Map<String, Boolean>>, permissions: Array<String>) {
        val result = LinkedHashMap<String, Boolean>()
        if (Build.VERSION.SDK_INT >= 23) {
            val permissionFragment = getPermissionFragment(activity, code, subscriber, result)
            val needRequestArray = ArrayList<String>()
            permissions.forEach {
                when (activity.checkSelfPermission(it)) {
                    PackageManager.PERMISSION_GRANTED -> {
                        result.put(it, true)
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        result.put(it, false)
                        needRequestArray.add(it)
                    }
                    else -> {
                        needRequestArray.add(it)
                    }
                }
            }
            if (needRequestArray.size > 0) {
                permissionFragment.requestPermissions(needRequestArray.toArray(arrayOfNulls(needRequestArray.size)), code)
            } else {
                subscriber.onNext(result)
            }

        } else {
            permissions.forEach {
                result.put(it, true)
            }
            subscriber.onNext(result)
        }
    }

    private fun getPermissionFragment(activity: Activity, code: Int, subscriber: Subscriber<Map<String, Boolean>>, result: LinkedHashMap<String, Boolean>): Fragment {
        var findFragmentByTag = activity.fragmentManager.findFragmentByTag("permissionFragment")
        if (findFragmentByTag == null) {
            findFragmentByTag = PermissionFragment()
            activity.fragmentManager.beginTransaction().add(findFragmentByTag, "permissionFragment").commitAllowingStateLoss()
            activity.fragmentManager.executePendingTransactions()
        }
        (findFragmentByTag as PermissionFragment).subScriber = subscriber
        findFragmentByTag.requestCode = code
        findFragmentByTag.resultMap = result
        return findFragmentByTag!!
    }
}

class PermissionFragment : Fragment() {
    var subScriber: Subscriber<Map<String, Boolean>>? = null
    var requestCode: Int = 0
    var resultMap: MutableMap<String, Boolean>? = null
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (subScriber != null) {
            if (requestCode == requestCode) {
                if (permissions != null) {
                    permissions.forEachIndexed { index, s ->
                        resultMap!!.put(s, grantResults[index] == PackageManager.PERMISSION_GRANTED)
                    }
                }
            }
            subScriber?.onNext(resultMap)
        }
    }
}

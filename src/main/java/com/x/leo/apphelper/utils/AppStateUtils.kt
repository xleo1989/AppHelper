package com.x.leo.apphelper.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Rect
import android.os.Looper
import android.view.View

/**
 * @作者:XLEO
 * @创建日期: 2017/10/24 13:51
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object AppStateUtils{
    fun isInDebug(ctx:Context):Boolean{
        try {
            return ctx.applicationInfo.flags.and(ApplicationInfo.FLAG_DEBUGGABLE) != 0
        }catch (e:Exception){
            return false
        }
    }

    fun isAppInDebug(ctx: Context,pkg:String):Boolean{
        try {
            return ctx.packageManager.getPackageInfo(pkg,1).applicationInfo.flags.and(ApplicationInfo.FLAG_DEBUGGABLE) != 0
        }catch (e:Exception){
            return false
        }
    }

    fun isRunOnUIThread():Boolean{
        return Looper.getMainLooper() == Looper.myLooper()
    }

    fun isKeyboardShown(rootView:View):Boolean{
        val softKeyboardHeight = rootView.resources.displayMetrics.heightPixels * 0.2
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val heightDiff = rootView.bottom - r.bottom
        return heightDiff > softKeyboardHeight
    }

    fun isNetWorkUseable(applicationContext: Context?) {

    }
}
package com.x.leo.apphelper.utils

import android.content.Context
import android.content.pm.ApplicationInfo

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
}
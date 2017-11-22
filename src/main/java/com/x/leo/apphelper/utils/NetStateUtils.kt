package com.x.leo.apphelper.utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.support.annotation.RequiresPermission

/**
 * @作者:XJY
 * @创建日期: 2017/11/20 18:19
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */
object NetStateUtils {
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isWIFIConnected(ctx: Context): Boolean {
        val manager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return manager.activeNetworkInfo.isConnected && manager.activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isMobileConnected(ctx: Context): Boolean {
        val manager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return manager.activeNetworkInfo.isConnected && manager.activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkUseable(ctx: Context):Boolean{
        val manager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return manager.activeNetworkInfo.isConnected
    }
}
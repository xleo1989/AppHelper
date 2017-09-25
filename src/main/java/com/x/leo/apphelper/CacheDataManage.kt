package com.x.leo.apphelper

/**
 * @作者:XLEO
 * @创建日期: 2017/9/15 17:32
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object CacheDataManage{
    fun  register(key: Int,listener: CacheDataListener<Object>){
        ActionToTake.registerListener(key,listener)
    }
    fun unregister(key: Int,listener: CacheDataListener<Object>){
        ActionToTake.unRegisterListener(key,listener)
    }
    fun <T> addData(key: Int,data:T){
        CenterCache.addData(key,data)
    }
    fun  removeData(key: Int){
        CenterCache.removeData(key)
    }
    fun <T> obtainData(key:Int,clazz: Class<T>):T?{
        return CenterCache.getData(key,clazz)
    }
    fun <T> obtainAndRemove(key: Int,clazz: Class<T>):T?{
        val result = CenterCache.getData(key,clazz)
        CenterCache.removeData(key)
        return result
    }
}
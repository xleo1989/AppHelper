package com.x.leo.apphelper.data.cache

/**
 * @作者:XLEO
 * @创建日期: 2017/9/15 16:29
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
interface CacheDataListener<T>{
    fun onAdd(key:Int,t: T?)
    fun onAlter(key:Int,t: T?,isEqual:Boolean)
    fun onDelete(key:Int,t: T?)
    fun onObtain(key: Int,t:T?)
}
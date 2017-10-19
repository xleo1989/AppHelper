package com.x.leo.apphelper.data.cache

import android.util.SparseArray
import java.util.*

/**
 * @作者:XLEO
 * @创建日期: 2017/9/15 16:27
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object ActionToTake{
    val actionMap:SparseArray<LinkedList<CacheDataListener<Object>>> by lazy {
        SparseArray<LinkedList<CacheDataListener<Object>>>()
    }
    fun getActionList(key: Int):LinkedList<CacheDataListener<Object>>? {
        return actionMap.get(key)
    }

    fun registerListener(key: Int,listener: CacheDataListener<Object>){
        val get = actionMap.get(key)
        if (get != null) {
            get.add(listener)
        }else{
            val list = LinkedList<CacheDataListener<Object>>()
            list.add(listener)
            actionMap.put(key,list)
        }
    }

    fun unRegisterListener(key:Int,listener: CacheDataListener<Object>){
        val get = actionMap.get(key)
        if (get != null) {
            get.remove(listener)
        }
    }
}
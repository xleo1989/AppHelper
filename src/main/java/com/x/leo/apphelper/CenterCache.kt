package com.x.leo.apphelper

import android.util.SparseArray

/**
 * @作者:XLEO
 * @创建日期: 2017/9/15 16:19
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object CenterCache {
    private val cacheHolder: SparseArray<Object> by lazy {
        SparseArray<java.lang.Object>()
    }

    @Synchronized fun <T> addData(key: Int, t: T) {
        if (t is java.lang.Object) {

            var action = DataCacheAction.ALTER
            val get = cacheHolder.get(key)
            if (get == null) {
                action = DataCacheAction.ADD
            }
            cacheHolder.put(key, t as Object)
            handleAction<T>(key, action, t)
        }
    }

    @Synchronized fun removeData(key: Int) {
        cacheHolder.remove(key)
        handleAction(key, DataCacheAction.REMOVE, null)
    }

    @Synchronized fun <T> getData(key: Int, clazz: Class<T>): T? {
        val legalValue = getLegalValue(cacheHolder.get(key), clazz)
        handleAction(key, DataCacheAction.OBTAIN, legalValue)
        return legalValue
    }

    private fun <T> getLegalValue(value: Object?, clazz: Class<T>): T? {
        if (value == null) {
            return null
        } else if (clazz.equals(java.lang.Object::class.java)) {
            return value as T
        } else if (value.`class`.isAssignableFrom(clazz)) {
            return value as T
        } else {
            try {
                return value as T
            } catch (e: Exception) {
                return null
            }
        }
    }

    private fun <T> handleAction(key: Int, action: DataCacheAction, t: T?) {
        val actionList = ActionToTake.getActionList(key)
        if (actionList != null && actionList.size > 0) {
            val result = if (t == null) null else t as Object
            for (i in 0..actionList.size - 1) {
                when (action) {
                    DataCacheAction.ADD -> {
                        actionList[i].onAdd(key, result)
                    }
                    DataCacheAction.ALTER -> {
                        actionList[i].onAlter(key, result)
                    }
                    DataCacheAction.REMOVE -> {
                        actionList[i].onDelete(key, result)
                    }
                    DataCacheAction.OBTAIN -> {
                        actionList[i].onObtain(key, result)
                    }
                    else -> {
                        throw IllegalStateException("error data cache state")
                    }
                }
            }
        }
    }
}
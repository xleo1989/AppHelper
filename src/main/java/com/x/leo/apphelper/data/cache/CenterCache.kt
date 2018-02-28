package com.x.leo.apphelper.data.cache

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
            var isEqual = false
            if (action == DataCacheAction.ALTER) {
                isEqual = get.equals(t)
            }
            cacheHolder.put(key, t as Object)
            handleAction<T>(key, action, t, isEqual)
        }
    }

    @Synchronized fun removeData(key: Int) {
        cacheHolder.remove(key)
        handleAction(key, DataCacheAction.REMOVE, null, false)
    }

    @Synchronized fun <T> getData(key: Int, clazz: Class<T>): T? {
        val legalValue = getLegalValue(cacheHolder.get(key), clazz)
        handleAction(key, DataCacheAction.OBTAIN, legalValue, false)
        return legalValue
    }

    private fun <T> getLegalValue(value: Object?, clazz: Class<T>): T? {
        return when {
            value == null -> null
            clazz == java.lang.Object::class.java -> value as T
            value.`class`.isAssignableFrom(clazz) -> value as T
            else -> try {
                value as T
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun <T> handleAction(key: Int, action: DataCacheAction, t: T?, isEqual:Boolean) {
        val actionList = ActionToTake.getActionList(key)
        if (actionList != null && actionList.size > 0) {
            val result = if (t == null) null else t as Object
            for (i in 0 until actionList.size) {
                when (action) {
                    DataCacheAction.ADD -> {
                        actionList[i].onAdd(key, result)
                    }
                    DataCacheAction.ALTER -> {
                        actionList[i].onAlter(key, result,isEqual)
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
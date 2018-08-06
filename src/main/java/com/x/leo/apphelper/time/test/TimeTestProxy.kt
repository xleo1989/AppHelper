package com.x.leo.apphelper.time.test

import java.lang.reflect.Proxy

/**
 * @作者:XLEO
 * @创建日期: 2017/9/20 10:56
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object TimeTestProxy {
    fun <T : Object> getProxyObject(obj: T): T {
        return Proxy.newProxyInstance(obj.`class`.classLoader,
                obj.`class`.interfaces,
                {
                    proxy, method, args ->
                    val startTime = System.currentTimeMillis()
                    method.invoke(obj, args)
                    LocalPrinter.INSTANCE.printTime(obj.`class`.simpleName, System.currentTimeMillis() - startTime, method.name, null)
                }) as T
    }
}
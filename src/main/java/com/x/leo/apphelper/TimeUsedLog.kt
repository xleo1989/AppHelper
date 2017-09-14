package com.x.leo.apphelper

import android.util.Log


/**
 * @作者:XLEO
 * @创建日期: 2017/9/7 14:41
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class TimeUsedLog(val className: String,val params:Array<String>)

object TimeUsedLogRead {
    val doGet = true
    fun <T> getTimes(clazz: Class<T>) {
        if (!doGet) {
            return
        }
        val constructor = clazz.constructors[0]
        val args = ArrayList<Any>()
        constructor.parameterTypes.forEach {
            args.add(it.newInstance())
        }
        val newInstance = constructor.newInstance(args.toArray())
        getTime(newInstance)
    }

    fun  getTime(obj: Any) {
        if (!doGet) {
            return
        }
        val clazz = obj::class.java
        for (declaredMethod in clazz.getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(TimeUsedLog::class.java)) {
                try {
                    declaredMethod.isAccessible = true
                    val startTime = System.currentTimeMillis()
                    val argsMethod = ArrayList<Any>()
                    if(declaredMethod.parameterTypes.size > 0){
                        for (parameterType in declaredMethod.parameterTypes) {
                            argsMethod.add(parameterType.newInstance())
                        }
                    }
                    if(argsMethod.size > 0) {
                        declaredMethod.invoke(obj, argsMethod.toArray())
                    }else{
                        declaredMethod.invoke(obj)
                    }
                    printOut(clazz.name, declaredMethod.name, System.currentTimeMillis() - startTime)
                } catch (e: Exception) {
                    Log.e("TimeUsed",e.message)
                }
            }
        }
    }

    private fun printOut(name: String?, name1: String?, l: Long) {
        Log.e("TimeUsed",name + "_" + name1 + "() spent_time:" + l + "||")
    }
}
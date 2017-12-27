package com.x.leo.apphelper.utils

import java.util.concurrent.*

/**
 * @作者:XLEO
 * @创建日期: 2017/8/24 10:20
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object ThreadPoolManager {
    val threadPool: ThreadPoolExecutor by lazy {
        val availableProcessors = if (Runtime.getRuntime().availableProcessors() > 1) {
            Runtime.getRuntime().availableProcessors()
        } else {
            3
        }
        ThreadPoolExecutor(availableProcessors - 1, availableProcessors * 2 - 1, 10, TimeUnit.MINUTES, LinkedBlockingQueue<Runnable>(availableProcessors * 3))
    }

    fun runWithThread(runnable: Runnable) {
        threadPool.execute {
            runnable.run()
        }
    }
}
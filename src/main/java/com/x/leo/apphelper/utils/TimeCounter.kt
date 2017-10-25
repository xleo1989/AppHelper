package com.x.leo.apphelper.utils

import android.os.CountDownTimer

/**
 * @作者:XLEO
 * @创建日期: 2017/10/24 15:02
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class TimeCounter(total:Long,step:Long,val timerlistener:TimerListener?): CountDownTimer(total,step){
    override fun onFinish() {
        timerlistener?.onFinish()
    }

    override fun onTick(millisUntilFinished: Long) {
        timerlistener?.onTick(millisUntilFinished)
    }
    interface TimerListener{
        fun onFinish()
        fun onTick(millisUntilFinished: Long)
    }
}


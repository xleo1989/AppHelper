package com.x.leo.apphelper.widget

import android.app.Activity
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import rx.functions.Action1

/**
 * Created by XLEO on 2018/2/26.
 */
open class TranslateFullScreenActivity(val resId: Int, val onViewCreate: Action1<TranslateFullScreenActivity>) : Activity() {
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        window.setFormat(PixelFormat.TRANSPARENT)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        setContentView(resId)
        onViewCreate.call(this)
    }
}
package com.x.leo.apphelper.widget

import android.content.Context
import android.content.DialogInterface
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager

/**
 * Created by XLEO on 2017/11/29.
 */
class XLeoDialog(val ctx: Context) : DialogInterface {

    private lateinit var localView: View
    private var mLayoutParams: WindowManager.LayoutParams? = null
    private var mWindowManager: WindowManager = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    fun setView(view: View): XLeoDialog {
        localView = view
        return this
    }

    fun setLayoutParams(layoutParams: WindowManager.LayoutParams): XLeoDialog {
        mLayoutParams = layoutParams
        return this
    }

    private var isShowing: Boolean = false

    fun isShowing() = isShowing
    /**
     * x,y 屏幕定位位置，优先级小于gravity
     * 未设置layoutparams时，添加默认配置
     */
    fun show(x: Int, y: Int, gravity: Int) {
        if (isShowing) {
            dismiss()
        }
        if (mLayoutParams == null) {
            mLayoutParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            mLayoutParams!!.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    .or(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    .or(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    .or(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
            mLayoutParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION
            mLayoutParams!!.dimAmount = 0.5f
            mLayoutParams!!.format = PixelFormat.TRANSLUCENT
        }
        mLayoutParams!!.gravity = gravity
        if (gravity == Gravity.NO_GRAVITY) {
            mLayoutParams!!.x = x
            mLayoutParams!!.y = y
        }
        mWindowManager.addView(localView, mLayoutParams)
        isShowing = true
    }

    override fun cancel() {

    }

    override fun dismiss() {
        if (isShowing) {
            mOnDismissListener?.onDismiss(this)
            isShowing = false
            mWindowManager.removeView(localView)
        }
    }

    private var mOnDismissListener: DialogInterface.OnDismissListener? = null

    fun setOnDismissListener(l: DialogInterface.OnDismissListener) {
        mOnDismissListener = l
    }

    fun setOnDismissListener(block: () -> Unit) {
        mOnDismissListener = DialogInterface.OnDismissListener {
            block.invoke()
        }
    }
}
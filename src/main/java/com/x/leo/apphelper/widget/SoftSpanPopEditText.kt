package com.x.leo.apphelper.widget

import android.graphics.Point
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ScrollView
import com.x.leo.apphelper.log.XLog

/**
 * Created by XLEO on 2017/11/28.
 */
class SoftSpanPopEditText {
    private var movedDistence: Int = 0
    private var currentListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    fun register(currentView: View, scrollRes: Int) {
        init(currentView, scrollRes)
        currentView.viewTreeObserver.addOnGlobalLayoutListener(currentListener)
    }

    fun unRegister(currentView: View) {
        currentView.viewTreeObserver.removeOnGlobalLayoutListener(currentListener)
    }

    private fun init(currentView: View, scrollRes: Int) {
        if (currentView == null) {
            return
        }
        currentListener = ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            currentView.getWindowVisibleDisplayFrame(r)
            if (currentView.rootView.height - r.bottom > currentView.resources.displayMetrics.heightPixels * 0.2) {
                val vr = Rect()
                currentView.getGlobalVisibleRect(vr)
                if (vr.bottom > r.bottom) {
                    var scrolledHeight = vr.bottom - r.bottom
                    if (scrollRes != -1) {
                        try {
                            val find = currentView.rootView.findViewById<ScrollView>(scrollRes)
                            val childAt = find.getChildAt(0)
                            if (childAt != null) {
                                if (childAt.height + find.paddingTop + find.paddingBottom + childAt.top - find.height > scrolledHeight) {
                                    find.smoothScrollBy(0, scrolledHeight)
                                    movedDistence += scrolledHeight
                                } else
                                    currentView.rootView.scrollTo(0, scrolledHeight)
                            } else {
                                currentView.rootView.scrollTo(0, scrolledHeight)
                            }

                        } catch (e: Throwable) {
                            XLog.e("catch exception:", e, 100)
                        }
                    } else {
                        currentView.rootView.scrollTo(0, scrolledHeight)
                    }
                }
            } else {
                currentView.clearFocus()
                if (scrollRes != -1) {
                    try {
                        val find = currentView.rootView.findViewById<ScrollView>(scrollRes)
                        val childAt = find.getChildAt(0)
                        if (childAt != null) {
                            val rect = Rect()
                            val offset = Point()
                            childAt.getGlobalVisibleRect(rect, offset)
                            if (rect.top - offset.y >= movedDistence && movedDistence != 0)
                                find.smoothScrollBy(0, -movedDistence)
                        }
                    } catch (e: Throwable) {
                        XLog.e("catch exception:", e, 100)
                    }
                }
                currentView.rootView.scrollTo(0, 0)
                movedDistence = 0
            }
        }
    }
}
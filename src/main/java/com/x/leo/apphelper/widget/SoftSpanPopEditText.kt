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
    private var movedDistance: Int = 0
    private var currentListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    fun register(currentView: View, scrollRes: Int) {
        init(currentView, scrollRes)
        currentView.viewTreeObserver.addOnGlobalLayoutListener(currentListener)
    }

    fun unRegister(currentView: View) {
        currentView.viewTreeObserver.removeOnGlobalLayoutListener(currentListener)
    }

    private fun init(currentView: View, scrollRes: Int) {
        currentListener = ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            currentView.getWindowVisibleDisplayFrame(r)
            if (currentView.rootView.height - r.bottom > currentView.resources.displayMetrics.heightPixels * 0.2) {
                val vr = Rect()
                currentView.getGlobalVisibleRect(vr)
                if (vr.bottom > r.bottom) {
                    val scrolledHeight = vr.bottom - r.bottom
                    if (scrollRes != -1) {
                        try {
                            val find = currentView.rootView.findViewById<ScrollView>(scrollRes)
                            val childAt = find.getChildAt(0)
                            if (childAt != null) {
                                if (childAt.height + find.paddingTop + find.paddingBottom + childAt.top - find.height > scrolledHeight) {
                                    find.smoothScrollBy(0, scrolledHeight)
                                    movedDistance += scrolledHeight
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
                            if (rect.top - offset.y >= movedDistance && movedDistance != 0)
                                find.smoothScrollBy(0, -movedDistance)
                        }
                    } catch (e: Throwable) {
                        XLog.e("catch exception:", e, 100)
                    }
                }
                currentView.rootView.scrollTo(0, 0)
                movedDistance = 0
            }
        }
    }
}
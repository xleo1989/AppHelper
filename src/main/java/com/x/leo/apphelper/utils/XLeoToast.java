package com.x.leo.apphelper.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.x.leo.apphelper.R;

/**
 * @作者:My
 * @创建日期: 2017/4/21 12:36
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class XLeoToast extends Toast {

    private        Context   mContext;
    private static XLeoToast sXLeoToast;

    /**
     *
     * @param context 使用全局的Context
     */
    public synchronized static void initXLeoToast(Context context){
        if (sXLeoToast == null) {
            sXLeoToast = new XLeoToast(context.getApplicationContext());
        }
    }
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context
     */
    public XLeoToast(Context context) {
        super(context);
        mContext = context;
    }

    public static void showMessage(String message){
        if (sXLeoToast == null) {
            throw new IllegalStateException("not init toast yet");
        }
        View view = sXLeoToast.getView();
        if (view == null) {
            View view1 = sXLeoToast.initView();
            sXLeoToast.setView(view1);
            view = view1;
        }

        ((TextView)view).setText(message);
        sXLeoToast.setDuration(Toast.LENGTH_SHORT);
        sXLeoToast.show();
    }

    public static void showMessage(int strId){
        showMessage(sXLeoToast.mContext.getString(strId));
    }

    private  View initView() {
        if (mContext == null) {
            throw new IllegalStateException("not init toast yet");
        }
        TextView view = new TextView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        view.setBackgroundResource(R.drawable.toastbackground);
        view.setTextColor(Color.WHITE);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        return view;
    }

    @Override
    public void show() {
        if (getView() == null) {
            return;
        }
        super.show();
    }
}

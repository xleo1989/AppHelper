package com.leox.demo.demotest.utils;

import android.util.Log;

public class Logger {
    public static final String TAG_START = "╔";
    public static final String MSG_DIVIDER = "═════════════════════";
    public static final String TAG_MIDDLE = "╠";
    public static final String TAG_BOTTOM = "╚";
    public static final String TAG_PREFIX = "║";

    public static void i(String Tag, String msg, Object... objects) {
        LoggerEditor editor = createBaseEditor(msg, objects);
        handleSuffix(editor);
        Log.i(Tag, editor.getLog());
    }

    private static LoggerEditor createBaseEditor(String msg, Object[] objects) {
        if (msg != null && objects != null && objects.length > 0) {
            msg = String.format(msg, objects);
        }
        LoggerEditor editor = LoggerEditor.getEditor(null);
        String name = Thread.currentThread().getName();
        if (name == null) {
            name = String.valueOf(Thread.currentThread().getId());
        }
        editor.addLine(TAG_START, MSG_DIVIDER)
                .addLine(TAG_PREFIX + "THREAD:", name)
                .addLine(TAG_MIDDLE, MSG_DIVIDER)
                .addLine(TAG_PREFIX, msg);
        return editor;
    }

    private static void handleSuffix(LoggerEditor editor) {
        editor.addLine(TAG_BOTTOM, MSG_DIVIDER);
    }

    public static void e(String Tag, String msg, Throwable e, Object... objects) {
        LoggerEditor editor = createBaseEditor(msg, objects);
        editor.addErrorStack(TAG_PREFIX, e);
        handleSuffix(editor);
        Log.e(Tag, editor.getLog());
    }
}

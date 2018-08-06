package com.leox.demo.demotest.utils;

import org.jetbrains.annotations.Nullable;

public class LoggerEditor {
    private StringBuffer sb;

    private LoggerEditor(@Nullable String initString) {
        sb = new StringBuffer();
        if (initString != null) {
            sb.append(initString);
        }else{
            sb.append(" ");
        }
        sb.append("\n");
    }

    public static LoggerEditor getEditor(@Nullable String initString) {
        return new LoggerEditor(initString);
    }

    public LoggerEditor addLine(String tag, String message) {
        sb.append(tag).append(message).append("\n");
        return this;
    }

    public LoggerEditor addErrorStack(String tag, Throwable t) {
        if (t != null) {
            sb.append(tag).append("Cause:").append(t.getClass().getSimpleName()).append(":  ").append(t.getMessage()).append("\n");
            StackTraceElement[] stackTrace = t.getStackTrace();
            if (stackTrace == null) {
                return this;
            }
            for (int i = 0; i < stackTrace.length; i++) {
                try {
                    StackTraceElement obj = stackTrace[i];
                    sb.append(tag).append("at ").append(obj.getClassName()).append(".").append(obj.getMethodName())
                            .append("(").append(obj.getFileName()).append(":")
                            .append(obj.getLineNumber()).append(")")
                            .append("\n");
                } catch (Throwable tm) {
                    tm.printStackTrace();
                }
            }
        } else {
            sb.append(tag).append("null").append("\n");
        }
        return this;
    }

    public String getLog() {
        return sb.toString();
    }
}

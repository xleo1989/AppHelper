package com.x.leo.apphelper.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

/**
 * 网络资源
 */
public class BrandUtils {

    public static final String SYS_EMUI = "sys_emui";
    public static final String SYS_MIUI = "sys_miui";
    public static final String SYS_FLYME = "sys_flyme";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_VERSION = "ro.build.version.emui";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";
    private static final int CODE_REQUEST_CAMERA_PERMISSIONS = 1521;

    private static SystemInfo systemInfoInstance;

    public static SystemInfo getSystemInfo() {
        if (systemInfoInstance == null) {
            synchronized (BrandUtils.class) {
                if (systemInfoInstance == null) {
                    systemInfoInstance = new SystemInfo();
                    getSystem(systemInfoInstance);
                }
            }
        }
        return systemInfoInstance;
    }

    /**
     * 获取系统信息
     * @param info
     */
    private static void getSystem(SystemInfo info) {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            if (prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null) {
                info.os = SYS_MIUI;//小米
                info.versionCode = Integer.valueOf(prop.getProperty(KEY_MIUI_VERSION_CODE, "0"));
                info.versionName = prop.getProperty(KEY_MIUI_VERSION_NAME, "V0");
            } else if (prop.getProperty(KEY_EMUI_API_LEVEL, null) != null
                    || prop.getProperty(KEY_EMUI_VERSION, null) != null
                    || prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null) {
                info.os = SYS_EMUI;//华为
                info.versionCode = Integer.valueOf(prop.getProperty(KEY_EMUI_API_LEVEL, "0"));
                info.versionName = prop.getProperty(KEY_EMUI_VERSION, "unknown");
            } else if (getMeizuFlymeOSFlag().toLowerCase().contains("flyme")) {
                info.os = SYS_FLYME;//魅族
                info.versionCode = 0;
                info.versionName = "unknown";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getMeizuFlymeOSFlag() {
        return getSystemProperty("ro.build.display.id", "");
    }

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    /**
     * 跳转应用设置中心
     * @param activity
     */
    public static void settingPermissionActivity(Activity activity) {
        //判断是否为小米系统
        if (TextUtils.equals(getSystemInfo().getOs(), BrandUtils.SYS_MIUI)) {
            Intent miuiIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            miuiIntent.putExtra("extra_pkgname", activity.getPackageName());
            //检测是否有能接受该Intent的Activity存在
            List<ResolveInfo> resolveInfos = activity.getPackageManager().queryIntentActivities(miuiIntent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfos.size() > 0) {
                activity.startActivityForResult(miuiIntent, CODE_REQUEST_CAMERA_PERMISSIONS);
                return;
            }
        }
        //如果不是小米系统 则打开Android系统的应用设置页
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, CODE_REQUEST_CAMERA_PERMISSIONS);
    }

    public static class SystemInfo {
        private String os = "android";
        private String versionName = Build.VERSION.RELEASE;
        private int versionCode = Build.VERSION.SDK_INT;

        public String getOs() {
            return os;
        }

        public String getVersionName() {
            return versionName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        @Override
        public String toString() {
            return "SystemInfo{" +
                    "os='" + os + '\'' +
                    ", versionName='" + versionName + '\'' +
                    ", versionCode=" + versionCode +
                    '}';
        }
    }
}
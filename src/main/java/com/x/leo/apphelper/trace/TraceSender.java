package com.x.leo.apphelper.trace;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.content.PermissionChecker;
import android.telephony.TelephonyManager;

import com.x.leo.apphelper.log.xlog.XLog;
import com.x.leo.apphelper.utils.ThreadPoolManager;

import org.json.JSONObject;

/**
 * Created by Miaoke on 14/04/2017.
 */

public class TraceSender {


    /**
     * get the imei
     *
     * @return
     */
    public static String getImei(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String imei;
        String imsi;
        try {
            imei = tm.getDeviceId();
            imsi = tm.getSubscriberId();

        } catch (SecurityException e) {
            XLog.INSTANCE.d("SecurityException: " + e.getMessage(),10);
            imei = "no_phone_state_permission";
            imsi = "no_phone_state_permission";
        }

        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String uuid = new DeviceUuidFactory(context).getDeviceUuid().toString();
        XLog.INSTANCE.d("Imei: " + imei + "_" + imsi + "_" + androidId + "_" + uuid,10);
        return imei + "_" + imsi + "_" + androidId + "_" + uuid;
    }


    public static void sendInfo(final Context context, final JSONObject obj) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            ThreadPoolManager.INSTANCE.runWithThread(new Runnable() {
                @Override
                public void run() {
                    //TODO NettyClient.getInstance(messageType).sendMessage();
                }
            });
        }else{
            //TODO NettyClient.getInstance(messageType).sendMessage();
        }
    }


    private static String tryGetPhoneNumberBySim(Context context) {
        if (Build.VERSION.SDK_INT >= 23 && PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            return "000000";
        }
        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        //need permission android.permission.READ_SMS
        String line1Number = tm.getLine1Number();
        if (line1Number != null && !"10081".equals(line1Number)) {
            return line1Number;
        } else {
            return "000000";
        }
    }
}

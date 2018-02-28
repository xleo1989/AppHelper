package com.x.leo.apphelper.contextLog

/**
 * Created by XLEO on 2018/2/28.
 */

object ConfigHolder {
    /**
     * device
     */
    val DEVICE_BRAND: String by lazy { "device_brand" }
    val DEVICE_SDK: String by lazy { "device_sdk" }
    val DEVICE_ID: String by lazy { "device_id" }
    val UUID_ID: String by lazy { "uuid_id" }
    val NETWORK_STATUS: String by lazy { "wifi_status" }
    val DEVICE_TYPE: String by lazy { "device_type" }
    val ANDROID_ID: String by lazy { "android_id" }
    val DEVICE_SCREEN_WIDTH:String by lazy { "device_screen_width" }
    val DEVICE_SCREEN_HEIGHT:String by lazy { "device_screen_height" }
    val DEVICE_CUSTOME_SIGNATURE:String by lazy { "device_custome_signature" }
    val DEVICE_BUILD_SERIAL:String by lazy { "device_build_serial" }
    /**
     * app
     */
    val APP_PERMISSIONS: String by lazy { "app_permissions" }
    val APP_FIRST_OPEN_TIME: String by lazy { "app_last_open_time" }
    val APP_CRASH_LOG: String by lazy { "app_crash_log" }
    val APP_VERSION_CODE: String by lazy { "app_version_code" }
    val APP_VERSION_NAME: String by lazy { "app_version_name" }

    /**
     * sim
     */
    val MAIN_SIM_SERIAL: String by lazy { "main_sim_serial" }
    val EXTRA_SIM_SERIAL:String by lazy { "extra_sim_serial" }
    val DEVICE_MAIN_PHONE_TYPE: String by lazy { "device_main_phone_type" }
    val DEVICE_EXTRA_PHONE_TYPE:String by lazy { "device_extra_phone_type" }
    val DEVICE_MAIN_PHONE_NUMBEN: String by lazy { "device_main_phone_number" }
    val DEVICE_EXTRA_PHONE_NUMBER:String by lazy { "device_extra_phone_number" }
    val DEVICE_MAIN_PHONE_SERVER:String by lazy { "device_main_phone_server" }
    val DEVICE_EXTRA_PHONE_SERVER:String by lazy { "device_extra_phone_server" }


}
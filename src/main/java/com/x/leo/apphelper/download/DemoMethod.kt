package com.x.leo.apphelper.download

import android.app.Activity
import android.widget.Toast

/**
 * Created by XLEO on 2018/4/9.
 */
object DemoMethod {
    fun downloadMethod(activity: Activity) {
        when (UserDownloadManager.checkExternalStorageState(activity)) {
            StorageUsableStates.OK -> showUpdate(activity)
            StorageUsableStates.EXTERNAL_STORAGE_NOT_USABLE -> Toast.makeText(activity, "no usable external storage", Toast.LENGTH_LONG).show()
            StorageUsableStates.PERMISSION_OF_WRITE_NOT_GRANTED -> {
                //todo request permission first
                val aBoolean = true
                if (aBoolean!!) {
                    showUpdate(activity)
                } else {
                    Toast.makeText(activity, "permission denied", Toast.LENGTH_LONG).show()
                }
            }

        }

    }

    private fun showUpdate(activity: Activity) {
        UserDownloadManager.init(activity)
                .setTitle("Update APP")
                .setMessage("new apk released")
                .setUrl("https://s3-ap-southeast-1.amazonaws.com/hhcapk/rupiaheasy.apk")
                .setFileName("update.apk")
                .setViewModule(DefaultDownloadViewModule(activity))
                .show()
    }
}
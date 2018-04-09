package com.x.leo.apphelper.download

import android.app.Activity
import android.widget.Toast
import com.tbruyelle.rxpermissions.RxPermissions
import rx.Subscriber

/**
 * Created by XLEO on 2018/4/9.
 */
object DemoMethod {
    fun downloadMethod(activity: Activity) {
        when (UserDownloadManager.checkExternalStorageState(activity)) {
            StorageUsableStates.OK -> showUpdate(activity)
            StorageUsableStates.EXTERNAL_STORAGE_NOT_USABLE -> Toast.makeText(activity, "no usable external storage", Toast.LENGTH_LONG).show()
            StorageUsableStates.PERMISSION_OF_WRITE_NOT_GRANTED -> RxPermissions(activity)
                    .request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(object : Subscriber<Boolean>() {
                        override fun onCompleted() {

                        }

                        override fun onError(throwable: Throwable) {
                            Toast.makeText(activity,"error happened when request permission",Toast.LENGTH_LONG).show()
                        }

                        override fun onNext(aBoolean: Boolean?) {
                            if (aBoolean!!) {
                                showUpdate(activity)
                            }else{
                                Toast.makeText(activity,"permission denied",Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            else -> {
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
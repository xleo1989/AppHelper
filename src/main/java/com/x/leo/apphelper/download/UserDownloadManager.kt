package com.x.leo.apphelper.download

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import android.widget.Toast
import java.io.File


/**
 * Created by XLEO on 2018/4/8.
 */
class UserDownloadManager private constructor(private val ctx: Context) {
    private var message: String? = null
    private var title: String? = null
    private var viewModule: DownloadViewModule? = null
    private var url: String? = null
    private var downloadReceiver: DownloadStateReceiver = DownloadStateReceiver()
    private var fileName: String? = null
    private val destinationUri by lazy {
        val file = File(
                if ((Build.VERSION.SDK_INT < 23 || ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                    Environment.getExternalStorageDirectory().absolutePath + File.separator + (fileName?:"temp")
                } else {
                    throw IllegalArgumentException("external storage not usable")
                }
        )
        if (file.exists()) {
            file.delete()
            file.createNewFile()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(ctx, ctx.packageName + ".fileprovider", file)
        } else {
            Uri.parse("file://" + file.absolutePath)
        }
    }


    companion object {
        /**
         * need permission of WRITE_EXTERNAL_STORAGE and usable external storage
         */
        @Throws(IllegalArgumentException::class)
        fun init(ctx: Context): UserDownloadManager {
            if ((Build.VERSION.SDK_INT < 23 || ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                return UserDownloadManager(ctx.applicationContext)
            } else {
                Toast.makeText(ctx, "need usable external storage", Toast.LENGTH_LONG).show()
                throw IllegalArgumentException("need usable external storage and permission of WRITE_EXTERNAL_STORAGE")
            }

        }

        /**
         * 状态检查
         * @return StorageUsableStates
         */
        fun checkExternalStorageState(ctx: Context): StorageUsableStates {
            return if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()) {
                StorageUsableStates.EXTERNAL_STORAGE_NOT_USABLE
            } else if (Build.VERSION.SDK_INT >= 23 && ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                StorageUsableStates.PERMISSION_OF_WRITE_NOT_GRANTED
            } else {
                StorageUsableStates.OK
            }
        }
    }

    fun setMessage(message: String): UserDownloadManager {
        this.message = message
        return this
    }

    fun setTitle(title: String): UserDownloadManager {
        this.title = title
        return this
    }

    fun setViewModule(viewModule: DownloadViewModule): UserDownloadManager {
        this.viewModule = viewModule
        return this
    }

    fun setDownLoadStateReceiver(downloadStateReceiver: DownloadStateReceiver): UserDownloadManager {
        this.downloadReceiver = downloadReceiver
        return this
    }

    fun setUrl(url: String): UserDownloadManager {
        this.url = url
        return this
    }

    @Throws(NullPointerException::class)
    fun show() {
        if (viewModule != null) {
            if (title != null) {
                viewModule!!.setTitle(title!!)
            }
            if (message != null) {
                viewModule!!.setMessage(message!!)
            }
            viewModule!!.setOnDownloadStart {
                DownloadAgency.getInstance(ctx, downloadReceiver)
                        .setTitle(title)
                        .setVisibleInDownLoadUi(true)
                        .setDestinationUri(destinationUri)
                        .start(url!!)
            }
            val onProgressChangeListener = viewModule!!.getOnProgressChangeListener()
            if (onProgressChangeListener != null) {
                downloadReceiver.setOnProgressChangeListener(onProgressChangeListener)
            }
            viewModule!!.show()
        }
    }

    fun setFileName(s: String): UserDownloadManager {
        this.fileName = s
        return this
    }
}

enum class StorageUsableStates {
    EXTERNAL_STORAGE_NOT_USABLE,
    PERMISSION_OF_WRITE_NOT_GRANTED,
    OK
}


interface OnProgressChangeInterface {
    fun onProgressChange(percent: Int)
    fun onStart()
    fun onError(reasonString: String, reason: Int)
    fun onComplete(parse: Uri)
    fun onCancel()
    fun onPaused(reason: Int, reasonString: String)
    fun onPending()
}

abstract class OnProgressChange : OnProgressChangeInterface {
    override fun onStart() {}
    override fun onError(reasonString: String, reason: Int) {}
    override fun onCancel() {}
    override fun onPaused(reason: Int, reasonString: String) {}
    override fun onPending() {}
}
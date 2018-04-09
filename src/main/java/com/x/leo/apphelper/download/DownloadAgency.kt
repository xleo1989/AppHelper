package com.x.leo.apphelper.download

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Looper

/**
 * Created by XLEO on 2018/4/8.
 */
open class DownloadAgency private constructor(private val ctx: Context, private val receiver: DownloadStateReceiver?) {
    private val downloadManager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    companion object {
        private val handler = android.os.Handler(Looper.getMainLooper())
        fun getInstance(ctx: Context,receiver: DownloadStateReceiver?):DownloadAgency{
            return DownloadAgency(ctx,receiver)
        }
    }

    fun start(url:String) {
        if (receiver != null) {
            val filter = IntentFilter()
            filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
            ctx.registerReceiver(receiver,filter)
        }
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(title)
                .setDestinationUri(destinationUri)
                .setVisibleInDownloadsUi(isVisibleInDownLoadUi)
                .setDescription("test description")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        val downId = downloadManager.enqueue(request)
        receiver?.setDownId(downId)
    }

    fun complete(){
        val runnable =object :Runnable {
            override fun run() {
                if (receiver != null) {
                    if (receiver.isComplete()) {
                        ctx.unregisterReceiver(receiver)
                    } else {
                        handler.postDelayed(this, 1000)
                    }
                }
            }
        }
        handler.post(runnable)
    }

    private var title: String? = null

    fun setTitle(title: String?): DownloadAgency {
        this.title = title
        return this
    }

    private var isVisibleInDownLoadUi: Boolean = true

    fun setVisibleInDownLoadUi(b: Boolean): DownloadAgency {
        this.isVisibleInDownLoadUi = b
        return this
    }

    private var destinationUri: Uri? = null

    open fun setDestinationUri(destinationUri: Uri): DownloadAgency {
        this.destinationUri = destinationUri
        return this
    }
}
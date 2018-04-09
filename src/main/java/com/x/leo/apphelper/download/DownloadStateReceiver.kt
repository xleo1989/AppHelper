package com.x.leo.apphelper.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.x.leo.apphelper.log.XLog

/**
 * Created by XLEO on 2018/4/8.
 */
open class DownloadStateReceiver: BroadcastReceiver(){
    private var onProgressChangeInterface: OnProgressChangeInterface? = null
    private var downId:Long = -1L
    private var isComplete = false
    override fun onReceive(context: Context?, intent: Intent?) {
        if (downId == -1L || context == null || intent == null) {
            return
        }
        val query = DownloadManager.Query()
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        query.setFilterById(downId)
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
           when(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))){
               DownloadManager.STATUS_FAILED->{
                   val reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                   val reasonString = when (reason) {
                       DownloadManager.ERROR_CANNOT_RESUME -> {
                           "cannot resume"
                       }
                       DownloadManager.ERROR_DEVICE_NOT_FOUND->{
                           "device not found"
                       }
                       DownloadManager.ERROR_FILE_ALREADY_EXISTS->{
                           "file already exists"
                       }
                       DownloadManager.ERROR_FILE_ERROR->{
                           " a storage issue arises which doesn't fit under any other error code"
                       }
                       DownloadManager.ERROR_HTTP_DATA_ERROR->{
                           "an error receiving or processing data occurred at the HTTP level."
                       }
                       DownloadManager.ERROR_INSUFFICIENT_SPACE->{
                            " there was insufficient storage space"
                       }
                       DownloadManager.ERROR_TOO_MANY_REDIRECTS->{
                           " there were too many redirects."
                       }
                       DownloadManager.ERROR_UNHANDLED_HTTP_CODE->{
                           "an HTTP code was received that download manager can't handle."
                       }
                       DownloadManager.ERROR_UNKNOWN->{
                           " the download has completed with an error that doesn't fit under any other error code."
                       }
                       else -> {
                           "http error,code:" + reason
                       }
                   }
                   XLog.i(reasonString,10)
                   onProgressChangeInterface?.onError(reasonString,reason)
               }
               DownloadManager.STATUS_PAUSED->{
                   val reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                   val reasonString:String = when(reason){
                       DownloadManager.PAUSED_QUEUED_FOR_WIFI->{
                           "the download exceeds a size limit for downloads over the mobile network and the download manager is waiting for a Wi-Fi connection to proceed."
                       }
                       DownloadManager.PAUSED_UNKNOWN->{
                           "the download is paused for some other reason"
                       }
                       DownloadManager.PAUSED_WAITING_FOR_NETWORK->{
                           " the download is waiting for network connectivity to proceed."
                       }
                       DownloadManager.PAUSED_WAITING_TO_RETRY->{
                           "the download is paused because some network error occurred and the download manager is waiting before retrying the request."
                       }
                       else->{
                           "unknown reason"
                       }
                   }
                   XLog.i(reasonString,10)
                   onProgressChangeInterface?.onPaused(reason,reasonString)
               }
               DownloadManager.STATUS_PENDING->{
                   onProgressChangeInterface?.onPending()
               }
               DownloadManager.STATUS_RUNNING->{
                   val totalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                   val currentSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                   val progress = if(totalSize == 0L || currentSize == 0L){
                       0
                   }else{
                       ((totalSize.toFloat()/currentSize.toFloat() + 0.5f) * 100).toInt()
                   }
                   onProgressChangeInterface?.onProgressChange(progress)
               }
               DownloadManager.STATUS_SUCCESSFUL->{
                   val uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                   onProgressChangeInterface?.onComplete(Uri.parse(uri))
                   isComplete = true
               }
           }
        }
    }

    fun setDownId(downId: Long) {
        this.downId = downId
    }

    fun isComplete():Boolean {
        return isComplete
    }
    fun setOnProgressChangeListener(onProgressChangeInterface: OnProgressChangeInterface?){
        this.onProgressChangeInterface = onProgressChangeInterface
    }
}
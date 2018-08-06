package com.x.leo.apphelper.utils

import android.content.Context
import android.content.res.AssetManager
import com.x.leo.apphelper.documented.DocumentMessage
import com.x.leo.apphelper.log.xlog.XLog
import rx.Observable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * @作者:XLEO
 * @创建日期: 2017/8/24 10:57
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object AssertsCopyer {
    fun checkFile(s: String, ctx: Context): Boolean {
        val doc = DocumentMessage.getDoc()
        val aBoolean = doc.getBoolean(s, false)
        doc.reset()
        return aBoolean ?: false
    }
    fun createCopyObservable(s: String, ctx: Context, assets: AssetManager):Observable<String>{
        return Observable.create<String> {
            t ->
            try {
                copyFileJob(s,ctx,assets)
                t.onNext("success")
            }catch (e:CopyException){
                t.onError(e)
            }finally {
                t.onCompleted()
            }
        }
    }
    @Throws(CopyException::class)
    private fun copyFileJob(s: String, ctx: Context, assets: AssetManager) {
        var fileOutputStream: FileOutputStream? = null
        if (s.contains(File.separator)) {
            val dir = s.substring(0, s.lastIndexOf(File.separator))
            val file = File(ctx.filesDir.absolutePath, dir)
            if (!(file.isDirectory && file.exists())) {
                file.mkdirs()
            }
        }
        var open: InputStream? = null
        try {
            val file = File(ctx.filesDir, s)
            if (file.exists()) {
                file.delete()
                file.createNewFile()
            }
            fileOutputStream = FileOutputStream(file)
            open = assets.open(s)
            var len = -1
            val buffer = ByteArray(1024 * 8)
            len = open!!.read(buffer)
            while (len != -1) {
                fileOutputStream!!.write(buffer, 0, len)
                len = open!!.read(buffer)
            }
            fileOutputStream!!.flush()
            DocumentMessage.getDoc().putBoolean(s, true).reset()
            XLog.d("copy asset success:" + s,10 )
        } catch (e: IOException) {
            throw CopyException(e)
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close()
                } catch (e: IOException) {
                    throw CopyException(e)
                }

            }
            if (open != null) {
                try {
                    open.close()
                } catch (e: IOException) {
                    throw CopyException(e)
                }

            }
        }
    }
}
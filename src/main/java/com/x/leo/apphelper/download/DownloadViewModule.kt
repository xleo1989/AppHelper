package com.x.leo.apphelper.download

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.x.leo.apphelper.R
import com.x.leo.apphelper.log.xlog.XLog

/**
 * Created by XLEO on 2018/4/9.
 */
abstract class DownloadViewModule(resId: Int, val ctx: Context) {
    protected val view: View by lazy {
        val temp = LayoutInflater.from(ctx).inflate(resId, null)
        initView(temp)
        temp
    }

    abstract fun setTitle(title: String)
    abstract fun setMessage(message: String)
    open fun initView(temp: View) {}
    fun start() {
        onStart?.invoke()
    }

    private var onStart: (() -> Unit)? = null

    fun setOnDownloadStart(func: () -> Unit) {
        this.onStart = func
    }

    abstract fun show()

    open fun getOnProgressChangeListener(): OnProgressChangeInterface? {
        return null
    }
}

/**
 * default view module
 */
class DefaultDownloadViewModule(ctx: Context):DownloadViewModule(R.layout.dialog_demo_download, ctx){
    override fun initView(temp: View) {
        super.initView(temp)
        temp.findViewById<View>(R.id.btn_dialog_update_download)
                .setOnClickListener {
                    start()
                    Toast.makeText(view.context, "download started,check the notification", Toast.LENGTH_LONG).show()
                }
    }

    override fun setTitle(title: String) {
        (view.findViewById<View>(R.id.tv_dialog_update_message) as TextView).text = title
    }

    override fun setMessage(message: String) {
        (view.findViewById<View>(R.id.tv_update_notes) as TextView).text = message
    }

    override fun show() {
        AlertDialog.Builder(ctx)
                .setView(view)
                .show()

    }

    override fun getOnProgressChangeListener(): OnProgressChangeInterface? {
        return object : OnProgressChange() {
            override fun onStart() {
                val progressBar = view.findViewById<View>(R.id.pb_dialog_update) as ProgressBar
                progressBar.visibility = View.VISIBLE
                view.findViewById<LinearLayout>(R.id.ll_dialog_update_message).visibility = View.GONE
            }

            override fun onProgressChange(percent: Int) {
                val progressBar = view.findViewById<View>(R.id.pb_dialog_update) as ProgressBar
                if (progressBar.visibility != View.VISIBLE) {
                    progressBar.visibility = View.VISIBLE
                    view.findViewById<LinearLayout>(R.id.ll_dialog_update_message).visibility = View.GONE
                }
                progressBar.progress = percent
            }

            override fun onError(reasonString: String, reason: Int) {
                super.onError(reasonString, reason)
                XLog.i(10, "down load error,code: %d,message: %s", reason, reasonString)
            }

            override fun onComplete(parse: Uri) {
                val installIntent = Intent()
                installIntent.action = Intent.ACTION_VIEW
                installIntent.setDataAndType(parse, "application/vnd.android.package-archive")
                installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                ctx.startActivity(installIntent)
            }
        }
    }

}
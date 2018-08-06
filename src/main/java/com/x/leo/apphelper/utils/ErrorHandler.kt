package com.x.leo.apphelper.utils

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.x.leo.apphelper.log.xlog.XLog

/**
 * Created by XLEO on 2018/3/16.
 */
class ErrorHandler {
    companion object {
        fun createMultipleHandler(builder: Builder, vararg args: HandleType) {
            args.forEach {
                builder.setType(it)
                builder.createHandler()?.show()
            }
        }
    }

    class Builder {
       private var callback: CallBack? = null
       private var type: HandleType = HandleType.LOG
       private var message: String? = null
       private var view: View? = null
       private var ctx: Context? = null
       private var customeClazz:Class<CustomizeErrorHandler>? = null
        fun setCallback(callback: CallBack?): Builder {
            this.callback = callback
            return this
        }

        fun setType(type: HandleType): Builder {
            if (this.type != type) {
                this.type = type
            }
            return this
        }

        fun setMessage(message: String?): Builder {
            this.message = message
            return this
        }

        fun setView(view: View): Builder {
            this.view = view
            return this
        }

        fun setContext(ctx: Context): Builder {
            this.ctx = ctx
            return this
        }

        fun createHandler(): ErrorHandlerInterface? {
            try {
                val result = when (type) {
                    HandleType.DIALOG -> {
                        when {
                            view != null -> DialogErrorHandler(view!!)
                            ctx != null -> DialogErrorHandler(ctx!!, message)
                            else -> null
                        }
                    }
                    HandleType.LOG -> {
                        LogErrorHandler(message)
                    }
                    HandleType.TOAST -> {
                        ToastErrorHandler(message)
                    }
                    HandleType.CUSTOMIZE->{
                        if (customeClazz is ErrorHandlerInterface) {
                            customeClazz!!.getDeclaredConstructor(Context::class.java,String::class.java)
                                    .newInstance(ctx,message)
                        }
                        null
                    }
                }
                if (callback != null) {
                    result?.setOnHandleListener(callback)
                }
                return result
            } catch (e: Exception) {
                XLog.e("create handler error:", e, 100)
                return null
            }
        }
    }

    enum class HandleType {
        DIALOG, TOAST, LOG,CUSTOMIZE
    }

    interface CallBack {
        fun onAccept()
        fun onRefuse()
    }
}

interface ErrorHandlerInterface {
    fun show()
    fun setOnHandleListener(callback: ErrorHandler.CallBack?) {}
}

abstract class CustomizeErrorHandler(val ctx: Context,val message: String?):ErrorHandlerInterface

class DialogErrorHandler : ErrorHandlerInterface {

    private var localDialog: AlertDialog.Builder? = null
    private var callback: ErrorHandler.CallBack? = null

    constructor(ctx: Context, message: String?) {
        localDialog = AlertDialog.Builder(ctx)
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage(message)
    }

    constructor(view: View) {
        localDialog = AlertDialog.Builder(view.context)
                .setView(view)
                .setCancelable(true)
    }

    override fun show() {
        if (callback == null) {
            localDialog?.setPositiveButton(android.R.string.ok, { dialog, _ ->
                dialog?.dismiss()
            })
        } else {
            localDialog?.setPositiveButton(android.R.string.ok, { dialog, _ ->
                try {
                    callback?.onAccept()
                } catch (e: Exception) {
                    XLog.e("callback accept error:", e, 100)
                } finally {
                    dialog?.dismiss()
                }
            })?.setNegativeButton(android.R.string.cancel, { dialog, _ ->
                try {
                    callback?.onRefuse()
                } catch (e: Exception) {
                    XLog.e("callback refuse error:", e, 100)
                } finally {
                    dialog?.dismiss()
                }
            })
        }
        localDialog?.show()
    }

    override fun setOnHandleListener(callback: ErrorHandler.CallBack?) {
        this.callback = callback
    }
}

class ToastErrorHandler(val message: String?) : ErrorHandlerInterface {
    override fun show() {
    }
}

class LogErrorHandler(val message: String?) : ErrorHandlerInterface {
    override fun show() {
        XLog.e(message, null, 100)
    }
}
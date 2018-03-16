package com.x.leo.apphelper.utils

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.x.leo.apphelper.log.XLog

/**
 * Created by XLEO on 2018/3/16.
 */
class ErrorHandler {
    companion object {
        fun createMultipleHandler(builder: Builder, vararg args: HandleType) {
            args.forEach {
                builder.setEType(it)
                builder.createHandler()?.show()
            }
        }
    }

    class Builder {
        var callback: CallBack? = null
        var type: HandleType = HandleType.LOG
        var message: String? = null
        var view: View? = null
        var ctx: Context? = null
        var customeClazz:Class<CustomizeErrorHandler>? = null
        fun setECallback(callback: CallBack?): Builder {
            this.callback = callback
            return this
        }

        fun setEType(type: HandleType): Builder {
            if (this.type != type) {
                this.type = type
            }
            return this
        }

        fun setEMessage(message: String?): Builder {
            this.message = message
            return this
        }

        fun setEView(view: View): Builder {
            this.view = view
            return this
        }

        fun setEContext(ctx: Context): Builder {
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
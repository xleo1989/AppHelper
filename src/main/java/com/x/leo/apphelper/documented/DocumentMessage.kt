package com.x.leo.apphelper.documented

import android.content.Context
import android.content.SharedPreferences
import com.x.leo.apphelper.log.xlog.XLog
import com.x.leo.apphelper.utils.AppStateUtils

/**
 * @作者:XLEO
 * @创建日期: 2017/10/25 11:22
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class DocumentMessage {
    private val ctx: Context
    //    private val lockObj:ReentrantLock
    private var fileName: String?
    private var fileCache: SharedPreferences? = null

    private constructor(ctx: Context) {
        this.ctx = ctx
//        lockObj = ReentrantLock()
        fileName = null
    }

    companion object {
        private val DEFAULT_FILE_NAME = "document_message_file_cache"
        private val FILE_STATE = "document_message_file_state"
        private val STATE_SUFFIX = "_DOCUNENT_STATE"
        private var instance: DocumentMessage? = null
        private var ctx: Context? = null

        fun isInitComplete(): Boolean {
            return ctx == null
        }

        fun initDocumentMessage(ctx: Context) {
            this@Companion.ctx = ctx
        }

        fun getDoc(): DocumentMessage {
//            if (instance == null) {
//                synchronized(this) {
//                    if (instance == null) {
//                        instance = DocumentMessage(ctx!!)
//                    }
//                    return@getDoc this.instance as DocumentMessage
//                }
//            }else {
//                return instance!!
//            }
            return DocumentMessage(ctx!!)
        }
    }


    /**
     * 指定文件，在使用之前设置,使用之后释放锁
     */
    fun setFileName(fileName: String): DocumentMessage {
//        lockObj.lock()
        fileCache = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        this.fileName = fileName
        if (fileCache!!.getInt(FILE_STATE, -1) == -1) {
            fileCache!!.edit().putInt(FILE_STATE, FileState.FILE_ADDED.ordinal).apply()
        }

        return this
    }

    fun getFileName(): String? {
        return fileName
    }

    /**
     * reset to unlock and clear choosed file holder,remember to call after use complete
     */
    fun reset(): DocumentMessage {
//        if (lockObj.isLocked) {
//            lockObj.unlock()
//        }
        fileCache == null
        fileName = null
        return this
    }

    fun putMessage(key: String, value: String?): DocumentMessage {
        checkFileCache()
        fileCache!!.edit().putString(key, value)
                .putInt(FILE_STATE, FileState.FILE_ALTERED.ordinal)
                .putBoolean(key + STATE_SUFFIX, true).apply()
        return this
    }

    private fun checkFileCache() {
        if (fileCache == null) {
//            lockObj.lock()
            fileCache = ctx.getSharedPreferences(DEFAULT_FILE_NAME, Context.MODE_PRIVATE)
            fileName = DEFAULT_FILE_NAME
            if (fileCache!!.getInt(FILE_STATE, FileState.FILE_NOT_EXISTED.ordinal) == FileState.FILE_NOT_EXISTED.ordinal) {
                fileCache!!.edit().putInt(FILE_STATE, FileState.FILE_ADDED.ordinal).apply()
            }

        }
    }

    fun getMessage(key: String, defValue: String?): String? {
        checkFileCache()
        val string = fileCache?.getString(key, defValue)
        fileCache!!.edit().putInt(FILE_STATE, FileState.FILE_READED.ordinal).putBoolean(key + STATE_SUFFIX, false).apply()
        return string
    }

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener): DocumentMessage {
        checkFileCache()
        fileCache!!.registerOnSharedPreferenceChangeListener(listener)
        return this
    }

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener): DocumentMessage {
        checkFileCache()
        fileCache!!.unregisterOnSharedPreferenceChangeListener(listener)
        return this
    }

    fun getChangedList(): ArrayList<String> {
        checkFileCache()
        val result = ArrayList<String>()
        if (fileCache!!.getInt(FILE_STATE, FileState.FILE_NOT_EXISTED.ordinal) == FileState.FILE_NOT_EXISTED.ordinal)
            return result
        fileCache!!.all.keys.forEach {
            if (it.contains(STATE_SUFFIX, false)) {
                try {
                    if (fileCache!!.getBoolean(it, false)) {
                        result.add(it.removeSuffix(STATE_SUFFIX))
                    }
                } catch (e: Exception) {
                    if (AppStateUtils.isInDebug(ctx)) {
                        XLog.d("" + e?.message, 5)
                    }
                }
            }
        }
        return result
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean? {
        checkFileCache()
        val string = fileCache?.getBoolean(key, defValue)
        fileCache!!.edit().putInt(FILE_STATE, FileState.FILE_READED.ordinal).putBoolean(key + STATE_SUFFIX, false).apply()
        return string
    }

    fun putBoolean(key: String, value: Boolean): DocumentMessage {
        checkFileCache()
        fileCache!!.edit().putBoolean(key, value)
                .putInt(FILE_STATE, FileState.FILE_ALTERED.ordinal)
                .putBoolean(key + STATE_SUFFIX, true).apply()
        return this
    }

    fun putLong(key: String, value: Long): DocumentMessage {
        checkFileCache()
        fileCache!!.edit().putLong(key, value)
                .putInt(FILE_STATE, FileState.FILE_ALTERED.ordinal)
                .putBoolean(key + STATE_SUFFIX, true).apply()
        return this
    }

    fun getLong(key: String): Long? {
        checkFileCache()
        val string = fileCache?.getLong(key, -1L)
        fileCache!!.edit().putInt(FILE_STATE, FileState.FILE_READED.ordinal).putBoolean(key + STATE_SUFFIX, false).apply()
        return string
    }

}
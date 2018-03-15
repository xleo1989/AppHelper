package com.x.leo.apphelper.contextLog

import java.lang.Exception

/**
 * Created by XLEO on 2018/2/28.
 */
interface CustomeException{}

open class  PermissionDeniedException(message:String):Exception(message),CustomeException{}
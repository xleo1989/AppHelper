package com.x.leo.apphelper

/**
 * @作者:XLEO
 * @创建日期: 2017/9/15 17:08
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
abstract class CacheDataAdapter<T>:CacheDataListener<T>{
    override external fun  onAdd(key: Int, t: T?)
    override external fun  onAlter(key: Int, t: T?)
    override external fun  onDelete(key: Int, t: T?)
    override external fun  onObtain(key: Int, t: T?)
}
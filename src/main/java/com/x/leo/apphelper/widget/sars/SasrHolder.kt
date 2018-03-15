package com.x.leo.apphelper.widget.sars

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.x.leo.apphelper.R
import com.x.leo.apphelper.log.XLog

/**
 * Created by XLEO on 2018/1/25.
 */
abstract class SasrHolder(view: View, val viewType:Int): RecyclerView.ViewHolder(view){
    fun initView(sarsDataInterface: SasrDataInterface, position: Int, onItemClickListener: SasrAdapter.OnItemClickListener?) {
        when (viewType) {
            1 -> {
                itemView.setOnClickListener { onItemClickListener?.onItemClick(itemView, position) }
                injectHeader(sarsDataInterface, position, onItemClickListener)

            }
            else -> {
                injectItem(sarsDataInterface, position, onItemClickListener)
                if (onItemClickListener != null) {
                    itemView.setOnClickListener { onItemClickListener?.onItemClick(itemView, position) }
                }
            }
        }

    }

    abstract fun injectItem(sarsDataInterface: SasrDataInterface, position: Int, onItemClickListener: SasrAdapter.OnItemClickListener?)

    abstract fun injectHeader(sarsDataInterface: SasrDataInterface, position: Int, onItemClickListener: SasrAdapter.OnItemClickListener?)
}

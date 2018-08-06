package com.x.leo.apphelper.widget.sars.example

import android.view.View
import android.widget.TextView
import com.x.leo.apphelper.R
import com.x.leo.apphelper.log.xlog.XLog
import com.x.leo.apphelper.widget.sars.SasrDataInterface
import com.x.leo.apphelper.widget.sars.SasrAdapter
import com.x.leo.apphelper.widget.sars.SasrHolder

/**
 * Created by XLEO on 2018/1/25.
 */
open class SasrHolderExample(view: View, viewType: Int) : SasrHolder(view,viewType) {
    override fun injectItem(sarsDataInterface: SasrDataInterface, position: Int, onItemClickListener: SasrAdapter.OnItemClickListener?) {
        try {
            itemView.findViewById<TextView>(R.id.tv_item).text = sarsDataInterface.title
        } catch (e: Exception) {
            XLog.e(e.message, e, 100)
        }
    }

    override fun injectHeader(sarsDataInterface: SasrDataInterface, position: Int, onItemClickListener: SasrAdapter.OnItemClickListener?) {
        try {
            itemView.findViewById<TextView>(R.id.tv_header).text = sarsDataInterface.title
        } catch (e: Exception) {
            XLog.e(e.message, e, 100)
        }
    }
}
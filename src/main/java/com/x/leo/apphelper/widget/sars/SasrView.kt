package com.x.leo.apphelper.widget.sars

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import com.x.leo.apphelper.R
import com.x.leo.apphelper.widget.sars.example.SasrHolderExample

/**
 * Created by XLEO on 2018/1/25.
 */
open class SasrView(ctx: Context, attributeSet: AttributeSet?) : android.support.v7.widget.RecyclerView(ctx, attributeSet) {
    private val datas: ArrayList<SasrDataInterface> = ArrayList()
    var begainHidden: Boolean = true
    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        if (attributeSet != null) {
            val attrs = context.obtainStyledAttributes(attributeSet!!, R.styleable.SarsView)
            begainHidden = attrs.getBoolean(R.styleable.SarsView_beginHiden, true)
            if (attrs.getBoolean(R.styleable.SarsView_defaultAdapter,false)) {
                adapter = SasrAdapter(datas, begainHidden, SasrHolderExample::class.java)
            }
            attrs.recycle()
        }
    }

    fun setOnItemClickListener(l: SasrAdapter.OnItemClickListener) {
        (adapter as SasrAdapter<*>).onItemClickListener = l
    }


    fun setDatas(data: ArrayList<SasrDataInterface>) {
        datas.clear()
        datas.addAll(data)
        adapter.notifyDataSetChanged()
    }

    fun getResult(): SasrDataInterface {
        return (adapter as SasrAdapter<*>).headerData
    }

    fun setResult(result: SasrDataInterface) {
        (adapter as SasrAdapter<*>).headerData = result
        adapter.notifyDataSetChanged()
    }
}



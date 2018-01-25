package com.x.leo.apphelper.widget.sars

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.x.leo.apphelper.R
import com.x.leo.apphelper.log.XLog

/**
 * Created by XLEO on 2018/1/25.
 */
open class SarsView(ctx: Context, attributeSet: AttributeSet?) : android.support.v7.widget.RecyclerView(ctx, attributeSet) {
    private val datas: ArrayList<SarsDataInterface> = ArrayList()
    var begainHidden: Boolean = true
    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        if (attributeSet != null) {
            val attrs = context.obtainStyledAttributes(attributeSet!!, R.styleable.SarsView)
            begainHidden = attrs.getBoolean(R.styleable.SarsView_beginHiden, true)
            if (attrs.getBoolean(R.styleable.SarsView_defaultAdapter,false)) {
                adapter = SarsAdapter(datas, begainHidden,SasrHolderExample::class.java)
            }
            attrs.recycle()
        }
    }

    fun setOnItemClickListener(l: SarsAdapter.OnItemClickListener) {
        (adapter as SarsAdapter<*>).onItemClickListener = l
    }


    fun setDatas(data: ArrayList<SarsDataInterface>) {
        datas.clear()
        datas.addAll(data)
        adapter.notifyDataSetChanged()
    }

    fun getResult(): SarsDataInterface {
        return (adapter as SarsAdapter<*>).headerData
    }

    fun setResult(result: SarsDataInterface) {
        (adapter as SarsAdapter<*>).headerData = result
        adapter.notifyDataSetChanged()
    }
}

abstract class SasrHolder(view:View,val viewType:Int):RecyclerView.ViewHolder(view){
    fun initView(sarsDataInterface: SarsDataInterface, position: Int, onItemClickListener: SarsAdapter.OnItemClickListener?) {
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

    abstract fun injectItem(sarsDataInterface: SarsDataInterface, position: Int, onItemClickListener: SarsAdapter.OnItemClickListener?)

    abstract fun injectHeader(sarsDataInterface: SarsDataInterface, position: Int, onItemClickListener: SarsAdapter.OnItemClickListener?)
}
open class SasrHolderExample(view: View, viewType: Int) : SasrHolder(view,viewType) {
    override fun injectItem(sarsDataInterface: SarsDataInterface, position: Int, onItemClickListener: SarsAdapter.OnItemClickListener?) {
        try {
            (itemView.findViewById(R.id.tv_header) as TextView).text = sarsDataInterface.title
        } catch (e: Exception) {
            XLog.e(e.message, e, 100)
        }
    }

    override fun injectHeader(sarsDataInterface: SarsDataInterface, position: Int, onItemClickListener: SarsAdapter.OnItemClickListener?) {
        try {
            (itemView.findViewById(R.id.tv_item) as TextView).text = sarsDataInterface.title
        } catch (e: Exception) {
            XLog.e(e.message, e, 100)
        }
    }
}

open class SarsAdapter<T:SasrHolder>(val datas: ArrayList<SarsDataInterface>, var isHidden: Boolean,val clazz: Class<T>) : RecyclerView.Adapter<T>() {
    companion object {
        @Throws(InstantiationException::class, IllegalAccessException::class)
        private fun <T> newTclass(clazz: Class<T>): T {
            return clazz.newInstance()

        }
    }
    private val baseListener: OnItemClickListener = object:OnItemClickListener{
        override fun onItemClick(itemView: View, position: Int) {
            headerData = datas[position]
            isHidden = !isHidden
            onItemClickListener?.onItemClick(itemView,position)
            notifyDataSetChanged()
        }
    }
    var onItemClickListener: OnItemClickListener? = null;
    private val onHeaderClickListener: OnItemClickListener = object : OnItemClickListener {
        override fun onItemClick(itemView: View, position: Int) {
            isHidden = !isHidden
            notifyDataSetChanged()
        }
    }
    var headerData: SarsDataInterface = object:SarsDataInterface{
        override var title: String?
            get() = title//To change initializer of created properties use File | Settings | File Templates.
            set(value) {
                title = value
            }
    }

    init {
        headerData.title = "请选择"
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): T {
        return if (parent != null) {
            val itemView = when (viewType) {
                1 -> {
                    LayoutInflater.from(parent!!.context).inflate(R.layout.header_sarsview, parent!!, true)
                }
                2 -> {
                    LayoutInflater.from(parent!!.context).inflate(R.layout.item_sarsview, parent!!, true)
                }
                else -> {
                    LayoutInflater.from(parent!!.context).inflate(R.layout.item_sarsview, parent!!, true)
                }
            }
            clazz.getConstructor(View::class.java,Int::class.java).newInstance(itemView,viewType)
        } else {
            throw IllegalArgumentException("parent is null")
        }
    }

    override fun onBindViewHolder(holder: T?, position: Int) {
        when (getItemViewType(position)) {
            1 -> {
                holder?.initView(headerData, position - 1, onHeaderClickListener)
            }
            else -> {
                holder?.initView(datas[position - 1], position - 1, baseListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            1
        } else {
            2
        }
    }

    override fun getItemCount(): Int {
        return 1 + if (!isHidden) datas.size else 0
    }

    interface OnItemClickListener {
        fun onItemClick(itemView: View, position: Int)
    }
}
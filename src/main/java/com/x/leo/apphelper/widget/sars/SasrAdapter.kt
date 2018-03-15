package com.x.leo.apphelper.widget.sars

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.x.leo.apphelper.R

/**
 * Created by XLEO on 2018/1/25.
 */
open class SasrAdapter<T:SasrHolder>(val datas: ArrayList<SasrDataInterface>, var isHidden: Boolean,val clazz: Class<T>) : RecyclerView.Adapter<T>() {
    private val baseListener: OnItemClickListener = object:OnItemClickListener{
        override fun onItemClick(itemView: View, position: Int) {
            headerData = datas[position]
            isHidden = !isHidden
            onItemClickListener?.onItemClick(itemView,position)
            notifyDataSetChanged()
        }
    }
    var onItemClickListener: OnItemClickListener? = null
    private val onHeaderClickListener: OnItemClickListener = object : OnItemClickListener {
        override fun onItemClick(itemView: View, position: Int) {
            isHidden = !isHidden
            notifyDataSetChanged()
        }
    }
    var headerData: SasrDataInterface = object:SasrDataInterface{
        override var title: String? = "请选择"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        return if (parent != null) {
            val itemView = when (viewType) {
                1 -> {
                    LayoutInflater.from(parent!!.context).inflate(R.layout.header_sarsview, parent!!, false)
                }
                2 -> {
                    LayoutInflater.from(parent!!.context).inflate(R.layout.item_sarsview, parent!!, false)
                }
                else -> {
                    LayoutInflater.from(parent!!.context).inflate(R.layout.item_sarsview, parent!!, false)
                }
            }
            clazz.getConstructor(View::class.java,Int::class.java).newInstance(itemView,viewType)
        } else {
            throw IllegalArgumentException("parent is null")
        }
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        when (getItemViewType(position)) {
            1 -> {
                holder?.initView(headerData, position - 1, object :OnItemClickListener{
                    override fun onItemClick(itemView: View, position: Int) {
                        customerOnHeaderClickListener?.onItemClick(itemView,position)
                        onHeaderClickListener?.onItemClick(itemView,position)
                    }
                })
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

    private  var customerOnHeaderClickListener: OnItemClickListener? = null

    fun setOnHeaderClickListener(l: OnItemClickListener) {
        customerOnHeaderClickListener = l
    }
}
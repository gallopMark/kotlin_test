package com.haoyuinfo.app.adapterhelper

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


abstract class BaseRecyclerAdapter : RecyclerView.Adapter<BaseRecyclerAdapter.RecyclerHolder>() {
    var itemClickListener: OnItemClickListener? = null
    var itemLongClickListener: OnItemLongClickListener? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(bindView(viewType), viewGroup, false))
    }

    open fun getItem(position: Int): Any? {
        return null
    }

    abstract fun bindView(viewType: Int): Int

    open inner class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        private var holder: SparseArray<View> = SparseArray()

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : View> obtainView(id: Int): T {
            var view = holder.get(id)
            if (view != null) return view as T
            view = itemView.findViewById(id)
            holder.put(id, view)
            return view as T
        }

        override fun onClick(v: View) {
            if (v.id == itemView.id) {
                itemClickListener?.onItemClick(this@BaseRecyclerAdapter, this, v, adapterPosition)
            }
        }

        override fun onLongClick(v: View): Boolean {
            if (v.id == itemView.id) {
                itemLongClickListener?.onItemLongClick(this@BaseRecyclerAdapter, this, v, adapterPosition)
                return true
            }
            return false
        }
    }

    interface OnItemClickListener {
        fun onItemClick(dapter: BaseRecyclerAdapter, holder: RecyclerHolder, view: View, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(adapter: BaseRecyclerAdapter, holder: RecyclerHolder, view: View, position: Int)
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        this.itemLongClickListener = onItemLongClickListener
    }
}
package com.haoyuinfo.app.adapterhelper

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseRecyclerAdapter : RecyclerView.Adapter<BaseRecyclerAdapter.RecyclerHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(bindView(viewType), viewGroup, false));
    }

    open fun getItem(position: Int): Any? {
        return null
    }

    abstract fun bindView(viewType: Int): Int

    class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        private var holder = SparseArray<View>()
        private var onItemClickListener: OnItemClickListener? = null
        private var onItemLongClickListener: OnItemLongClickListener? = null

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : View> obtainView(id: Int): T {
            var view = holder.get(id)
            if (view == null) {
                view = itemView.findViewById(id)
                holder.put(id, view)
            }
            return view as T
        }

        override fun onClick(v: View) {
            if (v.id == itemView.id) {
                onItemClickListener?.onItemClick(this, v, adapterPosition)
            }
        }

        override fun onLongClick(v: View): Boolean {
            if (v.id == itemView.id) {
                onItemLongClickListener?.onItemLongClick(this, v, adapterPosition)
                return true
            }
            return false
        }

        interface OnItemClickListener {
            fun onItemClick(holder: RecyclerHolder, view: View, position: Int)
        }

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
            this.onItemClickListener = onItemClickListener
        }

        interface OnItemLongClickListener {
            fun onItemLongClick(holder: RecyclerHolder, view: View, position: Int)
        }

        fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
            this.onItemLongClickListener = onItemLongClickListener
        }
    }
}
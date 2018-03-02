package com.haoyuinfo.library.widget

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

class EmptyRecyclerView : RecyclerView {
    private var emptyView: View? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun setAdapter(adapter: Adapter<*>?) {
        val oldAdapter = getAdapter()
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer)
        }
        super.setAdapter(adapter)
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer)
        }
    }

    private var observer: RecyclerView.AdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            checkIfEmpty()
        }
    }

    fun checkIfEmpty() {
        emptyView?.visibility = if (adapter.itemCount > 0) View.GONE else View.VISIBLE
    }

    fun setEmptyView(emptyView: View) {
        this.emptyView = emptyView
        checkIfEmpty()
    }
}
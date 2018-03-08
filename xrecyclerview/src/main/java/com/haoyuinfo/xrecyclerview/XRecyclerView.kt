package com.haoyuinfo.xrecyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup


class XRecyclerView : RecyclerView {
    private lateinit var mContext: Context
    private val mHeaderViews = ArrayList<View>()//头文件列表
    private lateinit var mHeader: XRecyclerViewHeader
    private lateinit var mFooter: XRecyclerViewFooter
    private var mAdapter: RecyclerView.Adapter<*>? = null//传入的Adapter
    private var mWrapAdapter: RecyclerView.Adapter<*>? = null//组合的Adapter
    private var mLastY = -1f //记录的Y坐标
    private val rate = 2.5f//阻力率
    private var mLoadingListener: LoadingListener? = null//滑动监听
    private var pullRefreshEnabled = true   //刷新状态
    private var loadingMoreEnabled = true   //上拉状态

    companion object {
        private const val TYPE_REFRESH_HEADER = -5 //添加刷新头
        private const val TYPE_HEADER = -4  //添加头部
        private const val TYPE_NORMAL = 0
        private const val TYPE_FOOTER = -3//添加上拉加载布局
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        mContext = context
        val xRecyclerViewHeader = XRecyclerViewHeader(context)
        mHeaderViews.add(0, xRecyclerViewHeader)
        mHeader = xRecyclerViewHeader
        mFooter = XRecyclerViewFooter(context)
        mFooter.visibility = View.GONE
        mFooter.setOnClickListener {
            if (mHeader.getState() < XRecyclerViewHeader.STATE_REFRESHING) mLoadingListener?.onLoadMore()
        }
    }

    fun setArrowImageView(resId: Int) {
        mHeader.setArrowImageView(resId)
    }

    fun setArrowColorFilter(color:Int){
        mHeader.setArrowColorFilter(color)
    }

    //添加头部文件的时候 判断有没有刷新头
    fun addHeaderView(view: View) {
        if (pullRefreshEnabled && mHeaderViews[0] !is XRecyclerViewHeader) {
            val xRecyclerViewHeader = XRecyclerViewHeader(mContext)
            mHeaderViews.add(0, xRecyclerViewHeader)
            mHeader = xRecyclerViewHeader
        }
        mHeaderViews.add(view)
    }

    fun loadMoreComplete(isSuccess: Boolean) {//上拉加载完成后的   隐藏上拉加载布局
        if (isSuccess) {
            mFooter.setState(XRecyclerViewFooter.STATE_COMPLETE)
        } else {
            mFooter.setState(XRecyclerViewFooter.STATE_FAILURE)
        }
    }

    fun refreshComplete(isSuccess: Boolean) {//下拉刷新完成后的  隐藏下拉加载 布局
        mHeader.refreshComplate(isSuccess)
    }

    fun setPullRefreshEnabled(enabled: Boolean) {//设置是否可以刷新
        pullRefreshEnabled = enabled
    }

    fun setLoadingMoreEnabled(enabled: Boolean) {//设置是否可以上拉
        loadingMoreEnabled = enabled
        if (!enabled) {
            mFooter.visibility = View.GONE
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        mAdapter = adapter
        mWrapAdapter = WrapAdapter(mHeaderViews, mFooter, adapter)
        super.setAdapter(mWrapAdapter)
        mAdapter?.registerAdapterDataObserver(mDataObserver)
    }

    /**
     * 活动监听  判断是否到底，用于加载
     */
    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == RecyclerView.SCROLL_STATE_IDLE && loadingMoreEnabled) {
            val layoutManager = layoutManager
            val lastVisibleItemPosition: Int  //最后可见的Item的position的值
            if (layoutManager is GridLayoutManager) {   //网格布局的中lastVisibleItemPosition的取值
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            } else if (layoutManager is StaggeredGridLayoutManager) {//瀑布流布局中lastVisibleItemPosition的取值
                val into = IntArray(layoutManager.spanCount)
                layoutManager.findLastVisibleItemPositions(into)
                lastVisibleItemPosition = findMax(into)
            } else {   //剩下只有线性布局（listview）中lastVisibleItemPosition的取值
                lastVisibleItemPosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }
            if (layoutManager.childCount > 0
                    && lastVisibleItemPosition >= layoutManager.itemCount - 1 && layoutManager.itemCount > layoutManager.childCount
                    && mHeader.getState() < XRecyclerViewHeader.STATE_REFRESHING) {
                mFooter.setState(XRecyclerViewFooter.STATE_LOADING)
                mLoadingListener?.onLoadMore()
            }
        }
    }

    /**
     * 监听手势活动  判断有没有到顶，用于刷新
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (mLastY == -1f) {
            mLastY = e.rawY
        }
        when (e.action) {
            MotionEvent.ACTION_DOWN -> mLastY = e.rawY
            MotionEvent.ACTION_MOVE -> {
                mHeader.refreshUpdatedAtValue()
                val deltaY = e.rawY - mLastY
                mLastY = e.rawY
                if (isOnTop() && pullRefreshEnabled) {
                    mHeader.onMove(deltaY / rate)
                    if (mHeader.getVisiableHeight() > 0 && mHeader.getState() < XRecyclerViewHeader.STATE_REFRESHING) {
                        return false
                    }
                }
            }
            else -> {
                mLastY = -1f // reset
                if (isOnTop() && pullRefreshEnabled) {
                    if (mHeader.releaseAction()) {
                        mLoadingListener?.onRefresh()
                    }
                }
            }
        }
        return super.onTouchEvent(e)
    }

    //判断是不是在顶部
    private fun isOnTop(): Boolean {
        if (mHeaderViews.isEmpty()) {
            return false
        }
        val view = mHeaderViews[0]
        return view.parent != null
    }

    //瀑布流里面用到的计算公式
    private fun findMax(lastPositions: IntArray): Int {
        var max = lastPositions[0]
        for (value in lastPositions) {
            if (value > max) {
                max = value
            }
        }
        return max
    }

    /*** adapter数据观察者*/
    private val mDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            mWrapAdapter?.notifyDataSetChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            mWrapAdapter?.notifyItemRangeChanged(positionStart, itemCount, payload)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeRemoved(positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemMoved(fromPosition, toPosition)
        }
    }

    /**
     * 设配器重组
     */
    private inner class WrapAdapter(private val mHeaderViews: ArrayList<View>,
                                    private val mFootView: XRecyclerViewFooter,
                                    private val adapter: Adapter<RecyclerView.ViewHolder>?) : Adapter<RecyclerView.ViewHolder>() {
        private var headerPosition = 1

        val headersCount: Int
            get() = mHeaderViews.size

        val footersCount: Int
            get() = 1

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
            super.onAttachedToRecyclerView(recyclerView)
            val manager = recyclerView?.layoutManager
            if (manager is GridLayoutManager) {
                manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (isHeader(position) || isFooter(position))
                            manager.spanCount
                        else
                            1
                    }
                }
            }
        }

        override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder?) {
            super.onViewAttachedToWindow(holder)
            val lp = holder?.itemView?.layoutParams
            if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams && (isHeader(holder.layoutPosition) || isFooter(holder.layoutPosition))) {
                lp.isFullSpan = true
            }
        }

        fun isHeader(position: Int): Boolean {
            return position >= 0 && position < mHeaderViews.size
        }

        fun isFooter(position: Int): Boolean {
            return position < itemCount && position >= itemCount - 1
        }

        fun isRefreshHeader(position: Int): Boolean {
            return position == 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
            if (viewType == TYPE_REFRESH_HEADER) {
                return SimpleViewHolder(mHeaderViews[0])
            } else if (viewType == TYPE_HEADER) {
                return SimpleViewHolder(mHeaderViews[headerPosition++])
            } else if (viewType == TYPE_FOOTER) {
                return SimpleViewHolder(mFootView)
            }
            return adapter?.onCreateViewHolder(parent, viewType)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (isHeader(position)) {
                return
            }
            val adjPosition = position - headersCount
            adapter?.let {
                if (adjPosition < it.itemCount) {
                    it.onBindViewHolder(holder, adjPosition)
                }
            }
        }

        override fun getItemCount(): Int {
            return if (adapter != null) {
                headersCount + footersCount + adapter.itemCount
            } else {
                headersCount + footersCount
            }
        }

        override fun getItemViewType(position: Int): Int {
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER
            }
            if (isHeader(position)) {
                return TYPE_HEADER
            }
            if (isFooter(position)) {
                return TYPE_FOOTER
            }
            val adjPosition = position - headersCount
            adapter?.let {
                if (adjPosition < it.itemCount) {
                    return it.getItemViewType(adjPosition)
                }
            }
            return TYPE_NORMAL
        }

        override fun getItemId(position: Int): Long {
            if (adapter != null && position >= headersCount) {
                val adjPosition = position - headersCount
                val adapterCount = adapter.itemCount
                if (adjPosition < adapterCount) {
                    return adapter.getItemId(adjPosition)
                }
            }
            return -1
        }

        override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
            adapter?.unregisterAdapterDataObserver(observer)
        }

        override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
            adapter?.registerAdapterDataObserver(observer)
        }

        private inner class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    /**
     * 监听接口
     */
    fun setLoadingListener(listener: LoadingListener) {
        mLoadingListener = listener
    }

    interface LoadingListener {

        fun onRefresh()

        fun onLoadMore()
    }
}
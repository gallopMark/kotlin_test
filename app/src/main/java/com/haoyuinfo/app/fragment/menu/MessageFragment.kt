package com.haoyuinfo.app.fragment.menu

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.haoyu.app.entity.Paginator
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapter.MessageAdapter
import com.haoyuinfo.app.entity.Message
import com.haoyuinfo.app.entity.MessageResult
import com.haoyuinfo.app.utils.Constants
import com.haoyuinfo.app.utils.OkHttpUtils
import com.haoyuinfo.library.base.BaseFragment
import com.haoyuinfo.library.recyclerviewenhanced.RecyclerTouchListener
import com.haoyuinfo.library.widget.CurrencyLoadView
import com.haoyuinfo.xrecyclerview.XRecyclerView
import kotlinx.android.synthetic.main.fragment_message.*
import okhttp3.Request

/**
 * 创建日期：2018/3/5.
 * 描述:消息
 * 作者:xiaoma
 */
class MessageFragment : BaseFragment(), XRecyclerView.LoadingListener {
    private var page = 1
    private var isRefresh = false
    private var isLoadMore = false
    private val mDatas = ArrayList<Message>()
    private lateinit var adapter: MessageAdapter

    override fun setLayoutResID(): Int {
        return R.layout.fragment_message
    }

    override fun setUp() {
        xRecyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
        xRecyclerView.setLoadingListener(this)
        adapter = MessageAdapter(context, mDatas)
        xRecyclerView.adapter = adapter
    }

    override fun initData() {
        val url = String.format(Constants.MESSAGE_URL, page, 20)
        addDisposable(OkHttpUtils.getAsync(context, url, object : OkHttpUtils.ResultCallback<MessageResult>() {
            override fun onBefore(request: Request) {
                if (isRefresh || isLoadMore)
                    loadView.setState(CurrencyLoadView.STATE_GONE)
                else
                    loadView.setState(CurrencyLoadView.STATE_LOADING)
            }

            override fun onError(request: Request, e: Throwable) {
                when {
                    isRefresh -> xRecyclerView.refreshComplete(false)
                    isLoadMore -> {
                        page -= 1
                        xRecyclerView.loadMoreComplete(false)
                    }
                    else -> loadView.setState(CurrencyLoadView.STATE_ERROR)
                }
            }

            override fun onResponse(response: MessageResult?) {
                loadView.setState(CurrencyLoadView.STATE_GONE)
                val list = response?.getResponseData()?.mMessages
                if (list != null && list.isNotEmpty()) {
                    val paginator = response.getResponseData()?.paginator
                    updateUI(list, paginator)
                } else {
                    when {
                        isRefresh -> xRecyclerView.refreshComplete(true)
                        isLoadMore -> xRecyclerView.loadMoreComplete(true)
                        else -> loadView.setState(CurrencyLoadView.STATE_EMPTY)
                    }
                }
            }
        }))
    }

    private fun updateUI(mDatas: List<Message>, paginator: Paginator?) {
        if (xRecyclerView.visibility != View.VISIBLE) xRecyclerView.visibility = View.VISIBLE
        when {
            isRefresh -> {
                this.mDatas.clear()
                xRecyclerView.refreshComplete(true)
            }
            isLoadMore -> xRecyclerView.loadMoreComplete(true)
        }
        this.mDatas.addAll(mDatas)
        adapter.notifyDataSetChanged()
        if (paginator != null && paginator.hasNextPage) {
            xRecyclerView.setLoadingMoreEnabled(true)
        } else {
            xRecyclerView.setLoadingMoreEnabled(false)
        }
    }

    override fun setListener() {
        loadView.setOnRetryListener(object : CurrencyLoadView.OnRetryListener {
            override fun onRetry(view: View) {
                initData()
            }
        })
        val onTouchListener = RecyclerTouchListener(context, xRecyclerView)
        onTouchListener.setSwipeOptionViews(R.id.mEditTv, R.id.mDeleteTv).setSwipeable(R.id.ll_rowFG, R.id.ll_rowBG, object : RecyclerTouchListener.OnSwipeOptionsClickListener {
            override fun onSwipeOptionClicked(viewID: Int, position: Int) {
                when (viewID) {
                    R.id.mEditTv -> { toast("edit $position") }
                    R.id.mDeleteTv -> { toast("delete $position") }
                }
            }
        }).setIndependentViews(R.id.mReplyTv).setClickable(object : RecyclerTouchListener.OnRowClickListener {
            override fun onRowClicked(position: Int) {
                val selected = position - 1
                if (selected in 0 until mDatas.size) { toast("onClick $position") }
            }

            override fun onIndependentViewClicked(independentViewID: Int, position: Int) {
                val selected = position - 1
                if (selected in 0 until mDatas.size) {
                    toast("onIndependentViewClicked $position")
                }
            }
        }).setLongClickable(true, object : RecyclerTouchListener.OnRowLongClickListener {
            override fun onRowLongClicked(position: Int) {
                toast("onRowLongClicked $position")
            }
        })
        xRecyclerView.addOnItemTouchListener(onTouchListener)
    }

    override fun onRefresh() {
        isRefresh = true
        isLoadMore = false
        page = 1
        initData()
    }

    override fun onLoadMore() {
        isRefresh = false
        isLoadMore = true
        page += 1
        initData()
    }
}
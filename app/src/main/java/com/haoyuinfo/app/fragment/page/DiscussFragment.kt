package com.haoyuinfo.app.fragment.page

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.haoyu.app.entity.Paginator
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapter.CourseDiscussAdapter
import com.haoyuinfo.app.entity.DiscussEntity
import com.haoyuinfo.app.entity.DiscussResult
import com.haoyuinfo.app.utils.Constants
import com.haoyuinfo.app.utils.OkHttpUtils
import com.haoyuinfo.library.base.BasePageFragment
import com.haoyuinfo.library.recyclerviewenhanced.RecyclerTouchListener
import com.haoyuinfo.library.widget.CurrencyLoadView
import com.haoyuinfo.xrecyclerview.XRecyclerView
import kotlinx.android.synthetic.main.fragment_course_discuss.*
import okhttp3.Request

/**
 * 创建日期：2018/3/15.
 * 描述:课程讨论
 * 作者:xiaoma
 */
class DiscussFragment : BasePageFragment(), XRecyclerView.LoadingListener {
    private var courseId: String? = null
    private var page = 1
    private val mDatas = ArrayList<DiscussEntity>()
    private var isRefresh = false
    private var isLoadMore = false
    private lateinit var adapter: CourseDiscussAdapter
    override fun setLayoutResID(): Int {
        return R.layout.fragment_course_discuss
    }

    override fun setUp() {
        courseId = arguments?.getString("courseId")
        xRecyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
        adapter = CourseDiscussAdapter(context, mDatas)
        xRecyclerView.adapter = adapter
        xRecyclerView.setLoadingListener(this)
        addItemListener()
    }

    private fun addItemListener() {
        val itemTouchListener = RecyclerTouchListener(context, xRecyclerView)
                .setIndependentViews(R.id.mSupportLl, R.id.mDiscussLl)
                .setClickable(object : RecyclerTouchListener.OnRowClickListener {
                    override fun onRowClicked(position: Int) {

                    }

                    override fun onIndependentViewClicked(independentViewID: Int, position: Int) {
//                        val selected = position - 1
                        when (independentViewID) {
                            R.id.mSupportLl -> {

                            }
                            R.id.mDiscussLl -> {

                            }
                        }
                    }
                })
        xRecyclerView.addOnItemTouchListener(itemTouchListener)
    }

    override fun initData() {
        val url = String.format(Constants.COURSE_DISCUSS, courseId, page)
        addDisposable(OkHttpUtils.getAsync(context, url, object : OkHttpUtils.ResultCallback<DiscussResult>() {
            override fun onBefore(request: Request) {
                when {
                    isRefresh || isLoadMore -> loadView.setState(CurrencyLoadView.STATE_GONE)
                    else -> loadView.setState(CurrencyLoadView.STATE_LOADING)
                }
            }

            override fun onError(request: Request, e: Throwable) {
                when {
                    isRefresh -> xRecyclerView?.refreshComplete(false)
                    isLoadMore -> {
                        page -= 1
                        xRecyclerView?.loadMoreComplete(false)
                    }
                    else -> {
                        loadView?.setState(CurrencyLoadView.STATE_ERROR)
                    }
                }
            }

            override fun onResponse(response: DiscussResult?) {
                loadView?.setState(CurrencyLoadView.STATE_GONE)
                val list = response?.getResponseData()?.discussions
                val paginator = response?.getResponseData()?.paginator
                if (list != null && list.isNotEmpty()) {
                    updateUI(list, paginator)
                } else {
                    when {
                        isRefresh -> xRecyclerView?.refreshComplete(true)
                        isLoadMore -> xRecyclerView?.loadMoreComplete(true)
                        else -> loadView?.setState(CurrencyLoadView.STATE_EMPTY)
                    }
                }
            }
        }))
    }

    private fun updateUI(mDatas: List<DiscussEntity>, paginator: Paginator?) {
        xRecyclerView?.let {
            if (it.visibility != View.VISIBLE) it.visibility = View.VISIBLE
            when {
                isRefresh -> {
                    this.mDatas.clear()
                    it.refreshComplete(true)
                }
                isLoadMore -> it.loadMoreComplete(true)
            }
            this.mDatas.addAll(mDatas)
            adapter.notifyDataSetChanged()
            if (paginator != null && paginator.hasNextPage)
                it.setLoadingMoreEnabled(true)
            else
                it.setLoadingMoreEnabled(false)
        }
    }

    override fun onRefresh() {
        isRefresh = true
        isLoadMore = false
        page = 1
        initData()
    }

    override fun onLoadMore() {
        isRefresh = true
        isLoadMore = false
        page += 1
        initData()
    }
}
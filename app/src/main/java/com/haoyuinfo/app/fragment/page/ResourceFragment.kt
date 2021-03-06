package com.haoyuinfo.app.fragment.page

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.haoyu.app.entity.Paginator
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapter.CourseResourcesAdapter
import com.haoyuinfo.app.adapterhelper.BaseRecyclerAdapter
import com.haoyuinfo.app.entity.CourseResource
import com.haoyuinfo.app.entity.CourseResourceResult
import com.haoyuinfo.app.entity.MFileInfo
import com.haoyuinfo.app.utils.Constants
import com.haoyuinfo.app.utils.OkHttpUtils
import com.haoyuinfo.library.base.BasePageFragment
import com.haoyuinfo.library.widget.CurrencyLoadView
import com.haoyuinfo.xrecyclerview.XRecyclerView
import okhttp3.Request

/**
 * 创建日期：2018/3/15.
 * 描述:课程资源吗
 * 作者:xiaoma
 */
class ResourceFragment : BasePageFragment(), XRecyclerView.LoadingListener {
    private var courseId: String? = null
    private var page = 1
    private val limit = 20
    private var isRefresh = false
    private var isLoadMore = false
    private lateinit var loadView: CurrencyLoadView
    private lateinit var xRecyclerView: XRecyclerView
    private val mDatas = ArrayList<MFileInfo>()
    private lateinit var adapter: CourseResourcesAdapter

    override fun setLayoutResID(): Int {
        return R.layout.fragment_course_resource
    }

    override fun setUp(view: View) {
        courseId = arguments?.getString("courseId")
        loadView = view.findViewById(R.id.loadView)
        xRecyclerView = view.findViewById(R.id.xRecyclerView)
        loadView.setEmptyIco(R.drawable.ic_empty_resources)
        loadView.setEmptyText("暂无资源")
        xRecyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
        adapter = CourseResourcesAdapter(context, mDatas)
        xRecyclerView.adapter = adapter
        xRecyclerView.setLoadingListener(this)
        adapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(dapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {

            }
        })
    }

    override fun initData() {
        val url = String.format(Constants.COURSE_RESOURCE, courseId, page, limit)
        addDisposable(OkHttpUtils.getAsync(context, url, object : OkHttpUtils.ResultCallback<CourseResourceResult>() {
            override fun onBefore(request: Request) {
                when {
                    isRefresh || isLoadMore -> loadView.setState(CurrencyLoadView.STATE_GONE)
                    else -> loadView.setState(CurrencyLoadView.STATE_LOADING)
                }
            }

            override fun onError(request: Request, e: Throwable) {
                when {
                    isRefresh -> xRecyclerView.refreshComplete(false)
                    isLoadMore -> {
                        page -= 1
                        xRecyclerView.loadMoreComplete(false)
                    }
                    else -> {
                        loadView.setState(CurrencyLoadView.STATE_ERROR)
                    }
                }
            }

            override fun onResponse(response: CourseResourceResult?) {
                loadView.setState(CurrencyLoadView.STATE_GONE)
                val list = response?.getResponseData()?.getResources()
                val paginator = response?.getResponseData()?.paginator
                if (list != null && list.isNotEmpty()) {
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

    private fun updateUI(resources: List<CourseResource>, paginator: Paginator?) {
        if (xRecyclerView.visibility != View.VISIBLE) xRecyclerView.visibility = View.VISIBLE
        when {
            isRefresh -> {
                mDatas.clear()
                xRecyclerView.refreshComplete(true)
            }
            isLoadMore -> xRecyclerView.loadMoreComplete(true)
        }
        for (resource in resources) mDatas.addAll(resource.getFileInfos())
        adapter.notifyDataSetChanged()
        if (paginator != null && paginator.hasNextPage)
            xRecyclerView.setLoadingMoreEnabled(true)
        else
            xRecyclerView.setLoadingMoreEnabled(false)
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
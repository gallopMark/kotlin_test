package com.haoyuinfo.app.fragment.menu

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.haoyu.app.entity.Paginator
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapter.PeerAdapter
import com.haoyuinfo.app.entity.MobileUser
import com.haoyuinfo.app.entity.PeerResult
import com.haoyuinfo.app.utils.Constants
import com.haoyuinfo.app.utils.OkHttpUtils
import com.haoyuinfo.library.base.BaseFragment
import com.haoyuinfo.library.widget.CurrencyLoadView
import com.haoyuinfo.xrecyclerview.XRecyclerView
import kotlinx.android.synthetic.main.fragment_peer.*
import okhttp3.Request

/**
 * 创建日期：2018/3/5.
 * 描述:同行
 * 作者:xiaoma
 */
class PeerFragment : BaseFragment(), XRecyclerView.LoadingListener {
    private val mDatas = ArrayList<MobileUser>()
    private lateinit var adapter: PeerAdapter
    private var page: Int = 1
    private var isRefresh = false
    private var isLoadMore = false
    override fun setLayoutResID(): Int {
        return R.layout.fragment_peer
    }

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        xRecyclerView.layoutManager = GridLayoutManager(context, 4).apply { orientation = GridLayoutManager.VERTICAL }
        adapter = PeerAdapter(context, mDatas)
        xRecyclerView.adapter = adapter
        xRecyclerView.setLoadingListener(this)
    }

    override fun initData() {
        val url = String.format(Constants.PEER_URL, page, 40)
        addDisposable(OkHttpUtils.getAsync(context, url, object : OkHttpUtils.ResultCallback<PeerResult>() {

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

            override fun onResponse(response: PeerResult?) {
                loadView.setState(CurrencyLoadView.STATE_GONE)
                val list = response?.getResponseData()?.mUsers
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

    private fun updateUI(mDatas: List<MobileUser>, paginator: Paginator?) {
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
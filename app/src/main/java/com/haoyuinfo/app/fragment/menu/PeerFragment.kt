package com.haoyuinfo.app.fragment.menu

import android.support.v7.widget.GridLayoutManager
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapter.PeerAdapter
import com.haoyuinfo.app.entity.MobileUser
import com.haoyuinfo.app.entity.MobileUserResult
import com.haoyuinfo.app.utils.Constants
import com.haoyuinfo.app.utils.OkHttpUtils
import com.haoyuinfo.library.base.BaseFragment
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

    override fun setUp() {
        xRecyclerView.layoutManager = GridLayoutManager(context, 4).apply { orientation = GridLayoutManager.VERTICAL }
        adapter = PeerAdapter(context, mDatas)
        xRecyclerView.adapter = adapter
        xRecyclerView.setLoadingListener(this)
    }

    override fun initData() {
        val url = "${Constants.PEER_URL}?page=$page&limit=40"
        OkHttpUtils.getAsync(context, url, object : OkHttpUtils.ResultCallback<MobileUserResult>() {

            override fun onError(request: Request, e: Throwable) {
                when {
                    isRefresh -> {
                        xRecyclerView.refreshComplete(false)
                    }
                    isLoadMore -> {
                        page -= 1
                        xRecyclerView.loadMoreComplete(false)
                    }
                }
            }

            override fun onResponse(response: MobileUserResult?) {
                if (isRefresh) {
                    xRecyclerView.refreshComplete(true)
                } else if (isLoadMore) {
                    xRecyclerView.loadMoreComplete(true)
                }
                response?.getResponseData()?.getmUsers()?.let {
                    if (isRefresh) {
                        mDatas.clear()
                    }
                    mDatas.addAll(it)
                    adapter.notifyDataSetChanged()
                }
            }
        })
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
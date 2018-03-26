package com.haoyuinfo.app.fragment.menu

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.haoyuinfo.app.R
import com.haoyuinfo.library.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_community.*

/**
 * 创建日期：2018/3/5.
 * 描述:研修社区
 * 作者:xiaoma
 */
class CommunityFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun setLayoutResID(): Int {
        return R.layout.fragment_community
    }

    override fun setUp() {
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbSays -> println("cmts says...")
                R.id.rbLesson -> println("cmts lessons...")
                R.id.rbMove -> println("cmts moves...")
            }
        }
        val urls = arrayOf("http://gyxz.hwm6b6.cn/vp/yx_ljun1/SuperStarBTS.apk",
                "http://dl.52ipk.com/APK/swzb//13/28/swzb.apk",
                "https://apk.apk.hz155.com/down/zscc.apk")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_cmts, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.build -> println("cmts build...")
        }
        return super.onOptionsItemSelected(item)
    }
}
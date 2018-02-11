package com.haoyuinfo.app.activity

import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.haoyuinfo.app.R
import com.haoyuinfo.app.base.BaseActivity
import com.haoyuinfo.app.base.BaseResult
import com.haoyuinfo.app.module.TrainEntity
import com.haoyuinfo.app.utils.Constants
import com.haoyuinfo.app.utils.OkHttpUtils
import com.haoyuinfo.app.utils.ScreenUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_menu.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import okhttp3.Request


class MainActivity : BaseActivity() {
    override fun setLayoutResID(): Int {
        return R.layout.activity_main
    }

    override fun setUp() {
        setDrawer()
        setMenu()
        setToolTitle("乐高教育在幼儿园课堂中的兴趣探讨")
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp)
        toolbar.setNavigationOnClickListener { toggle() }
    }

    private fun setDrawer() {
        val width = ScreenUtils.getScreenWidth(this)
        val params = menu.layoutParams
        params.width = (width * 0.7).toInt()
        menu.layoutParams = params
        drawerLayout.setScrimColor(ContextCompat.getColor(this, R.color.transparent))
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                //设置右面的布局位置  根据左面菜单的right作为右面布局的left   左面的right+屏幕的宽度（或者right的宽度这里是相等的）为右面布局的right
                main.layout(menu.right, 0, menu.right + width, menu.height)
            }
        })
    }

    private fun setMenu() {
        tv_learn.setOnClickListener { toggle() }
    }

    private fun toggle() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START)
        } else {
            drawerLayout.openDrawer(Gravity.START)
        }
    }

    override fun initData() {
        addDisposable(OkHttpUtils.getAsync(this, Constants.MAIN_URL, object : OkHttpUtils.ResultCallback<BaseResult<List<TrainEntity>>>() {
            override fun onError(request: Request, e: Throwable) {

            }

            override fun onResponse(response: BaseResult<List<TrainEntity>>?) {

            }
        }))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_scan -> {
                Toast.makeText(this, "扫一扫", Toast.LENGTH_LONG).show()
            }
            R.id.action_msg -> {
                Toast.makeText(this, "通知公告", Toast.LENGTH_LONG).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}


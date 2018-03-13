package com.haoyuinfo.app.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.haoyuinfo.app.R
import com.haoyuinfo.app.base.BaseResult
import com.haoyuinfo.app.entity.TrainEntity
import com.haoyuinfo.app.utils.Constants
import com.haoyuinfo.app.utils.OkHttpUtils
import com.haoyuinfo.library.base.BaseActivity
import com.haoyuinfo.library.utils.ScreenUtils
import com.haoyuinfo.mediapicker.entity.MediaItem
import com.haoyuinfo.mediapicker.utils.MediaPicker
import com.uuzuche.zxing.utils.CodeUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_empty_train.*
import kotlinx.android.synthetic.main.layout_main.*
import kotlinx.android.synthetic.main.layout_main_train.*
import kotlinx.android.synthetic.main.layout_menu.*
import okhttp3.Request


class MainActivity : BaseActivity() {

    override fun setLayoutResID(): Int {
        return R.layout.activity_main
    }

    override fun setUp(savedInstanceState: Bundle?) {
        setDrawer()
        setMenu()
        setToolTitle(resources.getString(R.string.learn))
        actionBar?.setNavigationIcon(R.drawable.ic_menu_white_24dp)
        actionBar?.setNavigationOnClickListener { toggle() }
    }

    private fun setDrawer() {
        val width = ScreenUtils.getScreenWidth(this)
        setDrawerLeftEdgeSize(0.6f)
//        val params = menu.layoutParams
//        params.width = (width * 0.75).toInt()
//        menu.layoutParams = params
//        drawerLayout.setScrimColor(ContextCompat.getColor(this, R.color.transparent))
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                //设置右面的布局位置  根据左面菜单的right作为右面布局的left   左面的right+屏幕的宽度（或者right的宽度这里是相等的）为右面布局的right
                main.layout(menu.right, main.top, menu.right + width, menu.height)
            }
        })
    }

    private fun setDrawerLeftEdgeSize(displayWidthPercentage: Float) {
        try {
            // 找到 ViewDragHelper 并设置 Accessible 为true
            val leftDraggerField = drawerLayout.javaClass.getDeclaredField("mLeftDragger")
            leftDraggerField.isAccessible = true
            val leftDragger = leftDraggerField.get(drawerLayout)
            val edgeSizeField = leftDragger.javaClass.getDeclaredField("mEdgeSize")
            // 找到 edgeSizeField 并设置 Accessible 为true
            edgeSizeField.isAccessible = true
            val edgeSize = edgeSizeField.getInt(leftDragger)
            // 设置新的边缘大小
            val displaySize = Point()
            windowManager.defaultDisplay.getSize(displaySize)
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (displaySize.x * displayWidthPercentage).toInt()))
        } catch (e: Exception) {

        }
    }

    private fun setMenu() {
        ll_userInfo.setOnClickListener {
            MediaPicker.Builder(this)
                    .mode(MediaItem.TYPE_PHOTO)
                    .mutilyMode(true)
                    .limit(9)
                    .withRequestCode(1)
                    .build()
                    .openActivity()
        }
        tv_learn.setOnClickListener { toggle() }
        val listener = View.OnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.type = when (it.id) {
                R.id.tv_teaching -> MenuActivity.TYPE_CMTS
                R.id.tv_peer -> MenuActivity.TYPE_PEER
                R.id.tv_message -> MenuActivity.TYPE_MESSAGE
                R.id.tv_wsGroup -> MenuActivity.TYPE_WORKSHOP
                R.id.tv_consulting -> MenuActivity.TYPE_CONSULT
                else -> MenuActivity.TYPE_SETTINGS
            }
            startActivity(intent)
        }
        tv_teaching.setOnClickListener(listener)
        tv_peer.setOnClickListener(listener)
        tv_message.setOnClickListener(listener)
        tv_wsGroup.setOnClickListener(listener)
        tv_consulting.setOnClickListener(listener)
        tv_settings.setOnClickListener(listener)
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
                if (response == null) {

                } else {
                    val list = response.responseData
                    if (list != null && list.isNotEmpty()) {
                        llMain.visibility = View.VISIBLE
                        bindData(list)
                    } else {
                        llEmpty.visibility = View.VISIBLE
                        llCmtsLearn.setOnClickListener { }
                    }
                }
            }
        }))
    }

    private fun bindData(mDatas: List<TrainEntity>) {
        tvTrain.text = mDatas[0].name
        tvTrain.setOnClickListener { showPop(mDatas) }
    }

    private fun showPop(mDatas: List<TrainEntity>) {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_scan -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    openCature()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
                }
            }
            R.id.action_msg -> {
                startActivity(Intent(this, AmapActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCature()
        } else {
            Toast.makeText(this, "扫描二维码需要打开相机和散光灯的权限", Toast.LENGTH_LONG).show()
        }
    }

    private fun openCature() {
        val intent = Intent(this, CaptureActivity::class.java)
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> {
                if (resultCode == RESULT_OK) {
                    val images = data?.getParcelableArrayListExtra<MediaItem>(MediaPicker.EXTRA_PATHS)
                    images?.let {
                        for (item in it) {
                            println("path:${item.path}")
                        }
                    }
                }
            }
            2 -> {
                if (resultCode == RESULT_OK) {   //扫描结果
                    val qrCode = data?.getStringExtra(CodeUtils.RESULT_STRING)
                    val intent = Intent(this, MenuActivity::class.java)
                    intent.type = MenuActivity.TYPE_CAPTURE_RESULT
                    intent.putExtra(CodeUtils.RESULT_STRING, qrCode)
                    startActivity(intent)
                }
            }
        }
    }
}


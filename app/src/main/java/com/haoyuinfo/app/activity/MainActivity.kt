package com.haoyuinfo.app.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.util.ArrayMap
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapter.MyTrainAdapter
import com.haoyuinfo.app.adapter.MyTrainCourseAdapter
import com.haoyuinfo.app.adapter.MyTrainWSAdapter
import com.haoyuinfo.app.adapterhelper.BaseRecyclerAdapter
import com.haoyuinfo.app.base.BaseResult
import com.haoyuinfo.app.entity.*
import com.haoyuinfo.app.utils.Constants
import com.haoyuinfo.app.utils.OkHttpUtils
import com.haoyuinfo.library.base.BaseActivity
import com.haoyuinfo.library.utils.ScreenUtils
import com.haoyuinfo.library.utils.TimeUtil
import com.haoyuinfo.library.widget.CurrencyLoadView
import com.haoyuinfo.mediapicker.entity.MediaItem
import com.haoyuinfo.mediapicker.utils.MediaPicker
import com.uuzuche.zxing.utils.CodeUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_empty_train.*
import kotlinx.android.synthetic.main.layout_main.*
import kotlinx.android.synthetic.main.layout_main_train.*
import kotlinx.android.synthetic.main.layout_menu.*
import okhttp3.Request


class MainActivity : BaseActivity(), CurrencyLoadView.OnRetryListener {
    private val myTrains = ArrayList<TrainEntity>()
    private var selectItem = 0
    private var selectTrainId: String? = null
    private var selectTrainTime: TimePeriod? = null
    private var dataMap = ArrayMap<String, MyTrainInfo>()
    private val mCourses = ArrayList<CourseEntity>()
    private val wsDatas = ArrayList<WorkShop>()
    private lateinit var courseAdapter: MyTrainCourseAdapter
    private lateinit var wsAdapter: MyTrainWSAdapter

    override fun setLayoutResID(): Int {
        return R.layout.activity_main
    }

    override fun setUp(savedInstanceState: Bundle?) {
        setDrawer()
        setMenu()
        setToolTitle(resources.getString(R.string.learn))
        actionBar?.setNavigationIcon(R.drawable.ic_menu_white_24dp)
        actionBar?.setNavigationOnClickListener { toggle() }
        loadView.setOnRetryListener(this)
        childLoadView.setOnRetryListener(this)
        rvCourse.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.VERTICAL }
        rvWs.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.VERTICAL }
        courseAdapter = MyTrainCourseAdapter(this, mCourses)
        wsAdapter = MyTrainWSAdapter(this, wsDatas)
        rvCourse.adapter = courseAdapter
        rvWs.adapter = wsAdapter
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
            override fun onBefore(request: Request) {
                loadView.setState(CurrencyLoadView.STATE_LOADING)
            }

            override fun onError(request: Request, e: Throwable) {
                loadView.setState(CurrencyLoadView.STATE_ERROR)
            }

            override fun onResponse(response: BaseResult<List<TrainEntity>>?) {
                if (response == null) {
                    loadView.setState(CurrencyLoadView.STATE_ERROR)
                } else {
                    loadView.setState(CurrencyLoadView.STATE_GONE)
                    val list = response.getResponseData()
                    if (list != null && list.isNotEmpty()) {
                        llMain.visibility = View.VISIBLE
                        bindData(list)
                    } else {
                        llEmpty.visibility = View.VISIBLE
                        llCmtsLearn.setOnClickListener {
                            val intent = Intent(this@MainActivity, MenuActivity::class.java)
                            intent.type = MenuActivity.TYPE_CMTS
                            startActivity(intent)
                        }
                    }
                }
            }
        }))
    }

    private fun bindData(mDatas: List<TrainEntity>) {
        myTrains.addAll(mDatas)
        selectTrainId = myTrains[0].id
        tvTrain.text = myTrains[0].name
        selectTrainTime = myTrains[0].mTrainingTime
        getTrainInfo()
        llTrain.setOnClickListener { showPopWindow() }
    }

    private fun showPopWindow() {
        ivExpand.setImageResource(R.drawable.ic_expand_less_black_24dp)
        val recyclerView = RecyclerView(this).apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.black_overlay))
        }
        recyclerView.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.VERTICAL }
        val popupWindow = PopupWindow(recyclerView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val adapter = MyTrainAdapter(myTrains, selectItem)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(dapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                selectItem = position
                adapter.setSelectedItem(selectItem)
                popupWindow.dismiss()
                selectTrainId = myTrains[selectItem].id
                tvTrain.text = myTrains[selectItem].name
                selectTrainTime = myTrains[selectItem].mTrainingTime
                onCheckTrain()
            }
        })
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                popupWindow.dismiss()
                return false
            }
        })
        recyclerView.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(ColorDrawable())
        popupWindow.isOutsideTouchable = true
        popupWindow.setOnDismissListener {
            ivExpand.setImageResource(R.drawable.ic_expand_more_black_24dp)
        }
        popupWindow.showAsDropDown(llTrain)
    }

    private fun onCheckTrain() {
        if (dataMap[selectTrainId] != null) {
            updateTrainInfo(dataMap[selectTrainId])
        } else {
            getTrainInfo()
        }
    }

    private fun getTrainInfo() {
        val url = "${Constants.TRAIN_INFO}?trainId=$selectTrainId"
        addDisposable(OkHttpUtils.getAsync(this, url, object : OkHttpUtils.ResultCallback<MyTrainInfo>() {

            override fun onBefore(request: Request) {
                showDialog()
            }

            override fun onError(request: Request, e: Throwable) {
                hideDialog()
                childLoadView.setState(CurrencyLoadView.STATE_ERROR)
            }

            override fun onResponse(response: MyTrainInfo?) {
                hideDialog()
                dataMap[selectTrainId] = response
                updateTrainInfo(response)
            }
        }))
    }

    private fun updateTrainInfo(response: MyTrainInfo?) {
        if (response?.getResponseData() != null) {
            if (nsvContent.visibility != View.VISIBLE) nsvContent.visibility = View.VISIBLE
            response.getResponseData()?.trainResult?.let { updateTrainInfoUI(it) }
            response.getResponseData()?.mCourseRegisters?.let { updateCourseRegisters(it) }
            response.getResponseData()?.mWorkshopUsers?.let { updateWorkShopListUI(it) }
            response.getResponseData()?.mCommunityResult?.let { updateCommunityListUI(it) }
        } else {
            if (nsvContent.visibility != View.GONE) nsvContent.visibility = View.GONE
            childLoadView.setState(CurrencyLoadView.STATE_EMPTY)
        }
    }

    private var showCommuity = false
    private var isSelf = false
    private var isNoLimit = false
    private var hasTopic = false
    private var canCreateWS = false
    //    /*更新培训情况信息*/
    private fun updateTrainInfoUI(trainResult: MyTrainInfo.TrainResult) {
        var courseTxt = "已选${trainResult.registerCourseNum}合格${trainResult.passCourseNum}门"
        trainResult.trainType?.let {
            if (!it.contains("course")) {
                courseTxt = "此项无需考核"
            }
        }
        tvCourseResult.text = courseTxt
        var wsTxt = "获得${trainResult.getWstsPoint}/${trainResult.wstsPoint}积分"
        trainResult.trainType?.let {
            if (!it.contains("workshop")) {
                wsTxt = "此项无需考核"
            }
        }
        tvWSResult.text = wsTxt
        var cmtsTxt = "获得${trainResult.getCmtsPoint}/${trainResult.cmtsPoint}积分"
        showCommuity = true
        trainResult.trainType?.let {
            if (!it.contains("community")) {
                cmtsTxt = "此项无需考核"
                showCommuity = false
            }
        }
        tvCmtsResult.text = cmtsTxt
        isSelf = trainResult.chooseCourseType != null && trainResult.chooseCourseType == "self"
        isNoLimit = trainResult.studyHoursType != null && trainResult.studyHoursType == "no_limit"
        hasTopic = trainResult.trainCourseConfig != null && trainResult.trainCourseConfig == "hasTopic"
        canCreateWS = trainResult.trainWorkshopConfig != null && trainResult.trainWorkshopConfig == "create"
        llCourse.setOnClickListener { scrollToPosition(mCourseLayout) }
        llWorkShop.setOnClickListener { scrollToPosition(tv_workshop) }
        llCommunity.setOnClickListener { scrollToPosition(mCmtsLayout) }
    }

    private fun scrollToPosition(view: View) {
        nsvContent.postDelayed({ nsvContent.smoothScrollTo(0, view.bottom) }, 20)
    }

    private fun updateCourseRegisters(mDatas: List<MyTrainInfo.CourseRegisters>) {
        mCourses.clear()
        for (register in mDatas) {
            register.getmCourse()?.let {
                it.state = register.state
                mCourses.add(it)
            }
        }
        if (mCourses.size > 0) {
            courseAdapter.notifyDataSetChanged()
            rvCourse.visibility = View.VISIBLE
            mEmptyCourseLl.visibility = View.GONE
            if (isNoLimit) {
                mSelectCourseLl.visibility = View.VISIBLE
                mSelectCourseLl.setOnClickListener { onSelectCourse() }
            } else {
                mSelectCourseLl.visibility = View.GONE
            }
        } else {
            rvCourse.visibility = View.GONE
            mEmptyCourseLl.visibility = View.VISIBLE
            if (isSelf) {
                mSelectCourseTv.visibility = View.VISIBLE
                mSelectCourseTv.setOnClickListener { onSelectCourse() }
            } else {
                mSelectCourseTv.visibility = View.GONE
            }
        }
    }

    /*去选课中心选课*/
    private fun onSelectCourse() {

    }

    /*更新工作坊列表*/
    private fun updateWorkShopListUI(mDatas: List<WorkShopUser>) {
        wsDatas.clear()
        for (wsUser in mDatas) {
            wsUser.getmWorkshop()?.let {
                it.point = wsUser.getPoint()
                wsDatas.add(it)
            }
        }
        if (wsDatas.isNotEmpty()) {
            wsAdapter.notifyDataSetChanged()
            rvWs.visibility = View.VISIBLE
            mEmptyWSLl.visibility = View.GONE
        } else {
            rvWs.visibility = View.GONE
            mEmptyWSLl.visibility = View.VISIBLE
            if (canCreateWS) {
                tv_wsConfig.text = "暂未创建个人工作坊"
                mCreateWSTv.visibility = View.VISIBLE
                mCreateWSTv.setOnClickListener {
                    //                    val intent = Intent(this, WorkshopEditActivity::class.java)
//                    intent.putExtra("trainId", trainId)
//                    startActivityForResult(intent, 2)
                }
            } else {
                tv_wsConfig.text = "需参与工作坊考核"
                mCreateWSTv.visibility = View.GONE
            }
        }
    }

    private fun updateCommunityListUI(cmts: MyTrainInfo.CommunityResult) {
        if (showCommuity) {
            mCmtsLayout.visibility = View.VISIBLE
            setCmtsLayout(cmts)
        } else {
            mCmtsLayout.visibility = View.GONE
        }
    }

    private fun setCmtsLayout(cmts: MyTrainInfo.CommunityResult) {
        val width = ScreenUtils.getScreenWidth(this) / 3 - 20
        val height = width / 3 * 2
        val params = LinearLayout.LayoutParams(width, height)
        mCmtsIv.layoutParams = params
        mCmtsIv.setImageResource(R.drawable.ic_default)
        cmts.getmCommunityRelation()?.timePeriod?.let {
            val text = "${TimeUtil.getSlashDate(it.startTime)}至${TimeUtil.getSlashDate(it.endTime)}"
            mCmtsPeriodTv.text = text
        }
        cmts.getmCommunityRelation()?.let {
            val hours = "${it.studyHours}学时"
            mCmtsHourTv.text = hours
            val score = "获得${cmts.score}/${it.score}积分"
            mCmtsScore.text = score
        }
        mCmtsLl.setOnClickListener {
            //startActivity(Intent(this, CmtsMainActivity::class.java))
        }
    }

    override fun onRetry(view: View) {
        when (view.id) {
            R.id.loadView -> initData()
            R.id.childLoadView -> getTrainInfo()
        }
    }

    override fun setListener() {
        courseAdapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(dapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                val state = mCourses[position].state
                if (state != null && state == "pass") {
                    val entity = mCourses[position]
                    if (entity.mTimePeriod?.state != null && entity.mTimePeriod?.state == "未开始") {
                        showCompatDialog("温馨提示", "课程尚未开放")
                    } else {
                        val courseId = entity.id
                        val courseTitle = entity.title
                        val intent = Intent(this@MainActivity, CourseLearnActivity::class.java)
                        selectTrainTime?.state?.let {
                            if (it == "进行中") {
                                intent.putExtra("training", true)
                            }
                        }
                        selectTrainTime?.minutes?.let {
                            if (it > 0) {
                                intent.putExtra("training", true)
                            }
                        }
                        intent.putExtra("courseId", courseId)
                        intent.putExtra("courseTitle", courseTitle)
                        startActivity(intent)
                    }
                } else {
                    if (state != null && state == "submit") {
                        showCompatDialog("温馨提示", "您的选课正在审核中")
                    } else if (state != null && state == "nopass") {
                        showCompatDialog("温馨提示", "您的选课审核不通过")
                    } else {
                        showCompatDialog("温馨提示", "课程尚未开放")
                    }
                }
            }
        })
        wsAdapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(dapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {

            }
        })
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


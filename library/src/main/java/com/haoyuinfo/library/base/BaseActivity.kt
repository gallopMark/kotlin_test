package com.haoyuinfo.library.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.haoyuinfo.library.R
import com.haoyuinfo.library.utils.PreferenceUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity : AppCompatActivity() {

    private val rxDisposables = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //强制竖屏
        AppManager.get().addActivity(this)
        setContentView(setLayoutResID())
        setToolbar()
        setUp()
        initData()
    }

    private fun setToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = ""
        setSupportActionBar(toolbar)
        //显示NavigationIcon,这个方法是ActionBar的方法.Toolbar没有这个方法
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationIcon(R.drawable.ic_back_white_24dp)
    }

    fun setToolTitle(title: CharSequence) {
        val tvTitle = findViewById<TextView>(R.id.toolbar_title)
        tvTitle?.text = title
    }

    abstract fun setLayoutResID(): Int

    abstract fun setUp()

    open fun initData() {

    }

    open fun addDisposable(d: Disposable?) {
        d?.let { rxDisposables.add(it) }
    }

    fun getAvatar(): String {
        return PreferenceUtils.getAvatar(this)
    }

    fun getUserId(): String {
        return PreferenceUtils.getUserId(this)
    }

    fun getRealName(): String {
        return PreferenceUtils.getRealName(this)
    }

    fun getDeptName(): String {
        return PreferenceUtils.getDeptName(this)
    }

    fun getRole(): String {
        return PreferenceUtils.getRole(this)
    }

    open fun getAccount(): String {
        return PreferenceUtils.getAccount(this)
    }

    fun getPassWord(): String {
        return PreferenceUtils.getPassWord(this)
    }

    fun isLogin(): Boolean {
        return PreferenceUtils.isLogin(this)
    }

    fun isRemember(): Boolean {
        return PreferenceUtils.isRemember(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        rxDisposables.dispose()
        AppManager.get().removeActivity(this)
    }
}
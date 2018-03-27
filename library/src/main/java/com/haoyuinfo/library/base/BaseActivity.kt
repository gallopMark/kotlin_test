package com.haoyuinfo.library.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.haoyuinfo.library.R
import com.haoyuinfo.library.dialog.MaterialDialog
import com.haoyuinfo.library.dialog.PromptDialog
import com.haoyuinfo.library.widget.CompatToast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity : AppCompatActivity() {

    var actionBar: Toolbar? = null
    private val rxDisposables = CompositeDisposable()
    private var promptDialog: PromptDialog? = null
    private var comPatDialog: MaterialDialog? = null
    private var mToast: CompatToast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //强制竖屏
        AppManager.get().addActivity(this)
        setContentView(setLayoutResID())
        setToolbar()
        setUp(savedInstanceState)
        initData()
        setListener()
    }

    private fun setToolbar() {
        actionBar = findViewById(R.id.toolbar)
        actionBar?.let {
            it.title = ""
            it.setNavigationIcon(R.drawable.ic_back_white_24dp)
            setSupportActionBar(it)
            it.setNavigationOnClickListener { finish() }
        }
        //显示NavigationIcon,这个方法是ActionBar的方法.Toolbar没有这个方法
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun setToolTitle(title: CharSequence) {
        val tvTitle = findViewById<TextView>(R.id.toolbar_title)
        tvTitle?.text = title
    }

    abstract fun setLayoutResID(): Int

    abstract fun setUp(savedInstanceState: Bundle?)

    open fun initData() {}

    open fun setListener() {}

    open fun addDisposable(d: Disposable?) {
        d?.let { rxDisposables.add(it) }
    }

    open fun showDialog() {
        promptDialog?.dismiss()
        window?.let { promptDialog = PromptDialog(this).apply { show() } }
    }

    open fun hideDialog() {
        promptDialog?.dismiss()
        promptDialog = null
    }

    open fun showCompatDialog(title: CharSequence, message: CharSequence) {
        comPatDialog?.dismiss()
        comPatDialog = MaterialDialog(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("确定", null)
            show()
        }
    }

    fun toast(text: CharSequence) {
        val v = LayoutInflater.from(this).inflate(R.layout.layout_compat_toast, FrameLayout(this))
        val textView = v.findViewById<TextView>(R.id.tv_text)
        textView.text = text
        mToast?.cancel()
        mToast = CompatToast(this, R.style.CompatToast).apply {
            duration = Toast.LENGTH_LONG
            view = v
            show()
        }
    }

    override fun onPause() {
        super.onPause()
        hideDialog()
        mToast?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        rxDisposables.dispose()
        AppManager.get().removeActivity(this)
    }
}
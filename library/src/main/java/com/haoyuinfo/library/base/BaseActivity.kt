package com.haoyuinfo.library.base

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
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

    // 封装跳转
    fun openActivity(c: Class<*>) {
        openActivity(c, null)
    }

    // 跳转 传递数据 bundel
    fun openActivity(c: Class<*>, bundle: Bundle?) {
        openActivity(c, bundle, null)
    }

    fun openActivity(c: Class<*>, bundle: Bundle?, uri: Uri?) {
        val intent = Intent(this, c)
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = it }
        startActivity(intent)
    }

    fun openActivity(intent: Intent) {
        openActivity(intent, null)
    }

    fun openActivity(intent: Intent, bundle: Bundle?) {
        openActivity(intent, bundle, null)
    }

    fun openActivity(intent: Intent, bundle: Bundle?, uri: Uri?) {
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = uri }
        startActivity(intent)
    }

    fun openActivityForResult(c: Class<*>, requestCode: Int) {
        openActivityForResult(c, null, requestCode)
    }

    fun openActivityForResult(c: Class<*>, bundle: Bundle?, requestCode: Int) {
        openActivityForResult(c, bundle, null, requestCode)
    }

    fun openActivityForResult(c: Class<*>, bundle: Bundle?, uri: Uri?, requestCode: Int) {
        val intent = Intent(this, c)
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = it }
        startActivityForResult(intent, requestCode)
    }

    fun openActivityForResult(intent: Intent, requestCode: Int) {
        openActivityForResult(intent, null, requestCode)
    }

    fun openActivityForResult(intent: Intent, bundle: Bundle?, requestCode: Int) {
        openActivityForResult(intent, bundle, null, requestCode)
    }

    fun openActivityForResult(intent: Intent, bundle: Bundle?, uri: Uri?, requestCode: Int) {
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = it }
        startActivityForResult(intent, requestCode)
    }

    override fun onPause() {
        super.onPause()
        hideDialog()
        mToast?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        rxDisposables.dispose()
        AppManager.removeActivity(this)
    }
}
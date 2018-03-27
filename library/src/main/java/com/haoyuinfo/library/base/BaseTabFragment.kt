package com.haoyuinfo.library.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.haoyuinfo.library.R
import com.haoyuinfo.library.dialog.MaterialDialog
import com.haoyuinfo.library.dialog.PromptDialog
import com.haoyuinfo.library.widget.CompatToast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * 创建日期：2018/3/26.
 * 描述:fragmenttabhost结合fragment使用，使用预加载避免重复创建view和重复加载数据
 * 作者:xiaoma
 */
abstract class BaseTabFragment : Fragment() {
    private val TAG = javaClass.name
    lateinit var context: Activity
    private var rootView: View? = null
    private var isViewCreated = false
    private val rxDisposables = CompositeDisposable()
    private var promptDialog: PromptDialog? = null
    private var mToast: CompatToast? = null
    private var comPatDialog: MaterialDialog? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.d(TAG, "onAttach...")
        this.context = context as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate...")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView...")
        rootView = rootView ?: inflater.inflate(setLayoutResID(), container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated...")
        super.onViewCreated(view, savedInstanceState)
        if (!isViewCreated) {
            setUp(view)
            initData()
            setListener()
            isViewCreated = true
        }
    }

    abstract fun setLayoutResID(): Int
    open fun setUp(view: View) {}
    open fun initData() {}
    open fun setListener() {}

    fun toast(text: CharSequence) {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_compat_toast, FrameLayout(context))
        val textView = v.findViewById<TextView>(R.id.tv_text)
        textView.text = text
        mToast?.cancel()
        mToast = CompatToast(context, R.style.CompatToast).apply {
            duration = Toast.LENGTH_LONG
            view = v
            show()
        }
    }

    open fun showDialog() {
        hideDialog()
        promptDialog = PromptDialog(context).apply { show() }
    }

    open fun hideDialog() {
        promptDialog?.dismiss()
        promptDialog = null
    }

    open fun showCompatDialog(title: CharSequence, message: CharSequence) {
        comPatDialog?.dismiss()
        comPatDialog = MaterialDialog(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("确定", null)
            show()
        }
    }

    open fun addDisposable(d: Disposable?) {
        d?.let { rxDisposables.add(it) }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart...")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume...")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause...")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop...")
    }

    override fun onDestroyView() {
        rootView?.parent?.let { (it as ViewGroup).removeView(rootView) }
        super.onDestroyView()
        Log.d(TAG, "onDestroyView...")
    }

    override fun onDestroy() {
        mToast?.cancel()
        rxDisposables.dispose()
        super.onDestroy()
        Log.d(TAG, "onDestroy...")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach...")
    }

}
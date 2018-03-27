package com.haoyuinfo.library.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
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

abstract class BaseFragment : Fragment() {
    lateinit var context: Activity
    private val rxDisposables = CompositeDisposable()
    private var promptDialog: PromptDialog? = null
    private val mToast: CompatToast? = null
    private var comPatDialog: MaterialDialog? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.context = context as Activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(setLayoutResID(), container, false)
    }

    abstract fun setLayoutResID(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUp(view, savedInstanceState)
        setListener()
    }

    open fun setUp(view: View, savedInstanceState: Bundle?) {}

    open fun setListener() {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    open fun initData() {}

    fun toast(text: CharSequence) {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_compat_toast, FrameLayout(context))
        val textView = v.findViewById<TextView>(R.id.tv_text)
        textView.text = text
        fromToast().apply { view = v }.show()
    }

    private fun fromToast(): CompatToast {
        return mToast
                ?: CompatToast(context, R.style.CompatToast).apply { duration = Toast.LENGTH_LONG }
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

    override fun onPause() {
        super.onPause()
        hideDialog()
    }

    override fun onDestroyView() {
        mToast?.cancel()
        rxDisposables.dispose()
        super.onDestroyView()
    }
}
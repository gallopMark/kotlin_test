package com.haoyuinfo.library.dialog

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.haoyuinfo.library.R
import com.haoyuinfo.library.utils.ScreenUtils
import com.haoyuinfo.library.widget.CircularProgressView

class LoadingDialog(private val mContext: Context) : AlertDialog(mContext, R.style.AppDialog) {

    private val view: View = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, LinearLayout(context), false)
    private val mLoadingTv: TextView
    private val progressView: CircularProgressView

    init {
        progressView = view.findViewById(R.id.progressView)
        mLoadingTv = view.findViewById(R.id.mLoadingTv)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)
        val dialogWidth = (ScreenUtils.getScreenWidth(mContext) * 3 / 10)
        val params = LinearLayout.LayoutParams(dialogWidth, dialogWidth)
        setContentView(view, params)
        window?.setBackgroundDrawableResource(R.color.transparent)
    }

    fun setLoadingText(text: CharSequence): LoadingDialog {
        mLoadingTv.text = text
        return this
    }

    override fun dismiss() {
        progressView.clearAnimation()
        super.dismiss()
    }
}
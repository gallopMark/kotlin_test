package com.haoyuinfo.library.dialog

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.haoyuinfo.library.R

/**
 * 创建日期：2018/3/14.
 * 描述:加载等待框
 * 作者:xiaoma
 */
class PromptDialog(private val mContext: Context) : AlertDialog(mContext, R.style.AppDialog) {

    private var animationDrawable: AnimationDrawable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_prompt, LinearLayout(mContext))
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        setContentView(view, params)
        val mLoadingIv = view.findViewById<ImageView>(R.id.mLoadingIv)
        animationDrawable = mLoadingIv?.drawable as AnimationDrawable
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun show() {
        animationDrawable?.start()
        super.show()
    }

    override fun dismiss() {
        animationDrawable?.stop()
        super.dismiss()
    }
}
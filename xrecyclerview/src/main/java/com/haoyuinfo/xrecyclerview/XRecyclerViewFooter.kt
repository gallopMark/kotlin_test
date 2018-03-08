package com.haoyuinfo.xrecyclerview

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.haoyuinfo.xRecyclerView.R


class XRecyclerViewFooter : LinearLayout {
    private lateinit var mContainer: LinearLayout//布局指向
    private lateinit var icLoading: ImageView
    private lateinit var mTextStatu: TextView
    private var animationDrawable: AnimationDrawable? = null

    companion object {
        const val STATE_LOADING = 0 //正在加载
        const val STATE_COMPLETE = 1  //加载完成
        const val STATE_FAILURE = 2 //正在失败
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    //初始化
    private fun initView(context: Context) {
        mContainer = LayoutInflater.from(context).inflate(R.layout.footer_view, this, false) as LinearLayout
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(0, 0, 0, 0)
        this.layoutParams = lp
        this.setPadding(0, 0, 0, 0)
        addView(mContainer, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        gravity = Gravity.CENTER
        icLoading = findViewById(R.id.ic_loading)
        mTextStatu = findViewById(R.id.tv_footer_statu)
        animationDrawable = icLoading.drawable as AnimationDrawable
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setState(state: Int) {
        when (state) {
            STATE_LOADING -> {
                mTextStatu.setText(R.string.xrecyclerview_footer_loading)
                visibility = View.VISIBLE
                animationDrawable?.start()
            }
            STATE_COMPLETE -> {
                animationDrawable?.stop()
                icLoading.clearAnimation()
                visibility = View.GONE
            }
            STATE_FAILURE -> {
                mTextStatu.setText(R.string.xrecyclerview_footer_hint_failure)
                animationDrawable?.stop()
                icLoading.visibility = View.GONE
            }
        }
    }
}
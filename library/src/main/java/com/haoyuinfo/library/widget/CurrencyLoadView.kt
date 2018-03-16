package com.haoyuinfo.library.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.haoyuinfo.library.R
import com.haoyuinfo.library.utils.PixelFormat

class CurrencyLoadView : FrameLayout {
    private var mContext: Context
    private lateinit var loadingView: View
    private lateinit var errorView: View
    private lateinit var emptyView: View
    private var cpvLoading: CircularProgressView? = null
    private var mLoadingTv: TextView? = null
    private var mErrorTv: TextView? = null
    private var mEmptyIv: ImageView? = null
    private var mEmptyTv: TextView? = null
    private var onRetryListener: OnRetryListener? = null

    companion object {
        const val STATE_IDEA = 0
        const val STATE_LOADING = 1
        const val STATE_ERROR = 2
        const val STATE_EMPTY = 3
        const val STATE_GONE = 4
    }

    constructor(context: Context) : super(context) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        loadingView = inflate(mContext, R.layout.layout_loading, null)
        errorView = inflate(mContext, R.layout.layout_error, null)
        emptyView = inflate(mContext, R.layout.layout_empty, null)
        val size = PixelFormat.dp2px(mContext, 120f)
        val compatParams = FrameLayout.LayoutParams(size, size)
        addView(loadingView, compatParams)
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        addView(errorView, params)
        addView(emptyView, params)
        setState(STATE_IDEA)
        initView()
    }

    private fun initView() {
        cpvLoading = findViewById(R.id.cpvLoading)
        mLoadingTv = findViewById(R.id.mLoadingTv)
        mErrorTv = findViewById(R.id.mErrorTv)
        mEmptyIv = findViewById(R.id.mEmptyIv)
        mEmptyTv = findViewById(R.id.mEmptyTv)
        errorView.setOnClickListener {
            setState(STATE_IDEA)
            onRetryListener?.onRetry(this@CurrencyLoadView)
        }
    }

    fun setLoadingText(text: CharSequence) {
        mLoadingTv?.text = text
    }

    fun setErrorText(text: CharSequence) {
        mErrorTv?.text = text
    }

    fun setEmptyIco(resId: Int) {
        mEmptyIv?.setImageResource(resId)
    }

    fun setEmptyText(text: CharSequence) {
        mEmptyTv?.text = text
    }

    fun setState(state: Int) {
        when (state) {
            STATE_IDEA -> {
                cpvLoading?.visibility = View.GONE
                loadingView.visibility = View.GONE
                errorView.visibility = View.GONE
                emptyView.visibility = View.GONE
                visibility = View.GONE
            }
            STATE_LOADING -> {
                cpvLoading?.visibility = View.VISIBLE
                loadingView.visibility = View.VISIBLE
                errorView.visibility = View.GONE
                emptyView.visibility = View.GONE
                visibility = View.VISIBLE
            }
            STATE_ERROR -> {
                cpvLoading?.visibility = View.GONE
                loadingView.visibility = View.GONE
                errorView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                visibility = View.VISIBLE
            }
            STATE_EMPTY -> {
                cpvLoading?.visibility = View.GONE
                loadingView.visibility = View.GONE
                errorView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                visibility = View.VISIBLE
            }
            STATE_GONE -> {
                cpvLoading?.visibility = View.GONE
                visibility = View.GONE
            }
        }
    }

    interface OnRetryListener {
        fun onRetry(view: View)
    }

    fun setOnRetryListener(onRetryListener: OnRetryListener) {
        this.onRetryListener = onRetryListener
    }
}
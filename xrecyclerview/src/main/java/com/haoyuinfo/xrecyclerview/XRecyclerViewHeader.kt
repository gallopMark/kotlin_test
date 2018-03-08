package com.haoyuinfo.xrecyclerview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.haoyuinfo.xRecyclerView.R

class XRecyclerViewHeader : LinearLayout {
    private lateinit var mContext: Context
    private lateinit var mContainer: LinearLayout//布局指向

    companion object {
        const val STATE_NORMAL = 0// 初始状态
        const val STATE_RELEASE_TO_REFRESH = 1   // 释放刷新
        const val STATE_REFRESHING = 2   // 正在刷新
        const val STATE_SUCCESS = 3  //刷新成功
        const val STATE_FAILURE = 4 //刷新失败
    }

    private lateinit var mArrowImageView: ImageView//箭头呀
    private lateinit var ivLoading: ImageView    // 正在刷新的图标
    private lateinit var mStatusTextView: TextView//状态文字
    private lateinit var icRefreshStatu: ImageView //刷新成功标记
    private lateinit var mHeaderTimeView: TextView //刷新时间文本
    private var mState = STATE_NORMAL  // 当前状态（临时保存用）

    //以下是箭头图标动画
    private lateinit var mRotateUpAnim: Animation
    private lateinit var mRotateDownAnim: Animation
    private var animationDrawable: AnimationDrawable? = null
    private val duration = 180L//旋转角度
    private var mMeasuredHeight: Int = 0//布局的原始高度，用于做状态改变的标志
    private var lastUpdateTime: Long = 0
    private val minute = (60 * 1000).toLong()
    private val hour = 60 * minute
    private val day = 24 * hour
    private val month = 30 * day
    private val year = 12 * month

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        mContext = context
        mContainer = LayoutInflater.from(context).inflate(R.layout.header_view, this, false) as LinearLayout
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(0, 0, 0, 0)
        layoutParams = lp
        setPadding(0, 0, 0, 0)
        addView(mContainer, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0))
        gravity = Gravity.BOTTOM
        mArrowImageView = findViewById(R.id.ic_arrow)
        ivLoading = findViewById(R.id.ic_refreshing)
        mStatusTextView = findViewById(R.id.tv_refresh_statu)
        mHeaderTimeView = findViewById(R.id.tv_refresh_time)
        icRefreshStatu = findViewById(R.id.ic_refresh_statu)
        //箭头改变动画
        mRotateUpAnim = RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        mRotateUpAnim.duration = duration
        mRotateUpAnim.fillAfter = true
        mRotateDownAnim = RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        mRotateDownAnim.duration = duration
        mRotateDownAnim.fillAfter = true
        ///添加匀速转动动画
        animationDrawable = ivLoading.drawable as AnimationDrawable
        setArrowColorFilter(R.color.colorPrimary)
        icRefreshStatu.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mMeasuredHeight = measuredHeight
    }

    fun setArrowImageView(resid: Int) {
        mArrowImageView.setImageResource(resid)
    }

    fun setArrowColorFilter(color: Int) {
        if (color != 0)
            mArrowImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary))
        else
            mArrowImageView.setColorFilter(ContextCompat.getColor(mContext, color))
    }

    //设置状态
    fun setState(state: Int) {
        if (state == mState) {
            return
        }
        //判断动画的添加
        when (state) {
            STATE_NORMAL -> {
                mArrowImageView.clearAnimation()
                mArrowImageView.visibility = View.VISIBLE
                animationDrawable?.stop()
                ivLoading.visibility = View.GONE
                icRefreshStatu.visibility = View.GONE
                mStatusTextView.setText(R.string.xrecyclerview_header_hint_normal)
            }
            STATE_RELEASE_TO_REFRESH -> {
                mArrowImageView.clearAnimation()
                mArrowImageView.startAnimation(mRotateUpAnim)
                mStatusTextView.setText(R.string.xrecyclerview_header_hint_ready)
            }
            STATE_REFRESHING -> {
                mArrowImageView.clearAnimation()
                mArrowImageView.visibility = View.GONE
                animationDrawable?.start()
                ivLoading.visibility = View.VISIBLE
                mStatusTextView.setText(R.string.xrecyclerview_header_hint_loading)
            }
            STATE_SUCCESS -> {
                animationDrawable?.stop()
                ivLoading.visibility = View.GONE
                icRefreshStatu.visibility = View.VISIBLE
                icRefreshStatu.setImageResource(R.drawable.ic_refresh_succeed)
                mStatusTextView.setText(R.string.xrecyclerview_header_hint_refresh_success)
                lastUpdateTime = System.currentTimeMillis()
            }
            STATE_FAILURE -> {
                animationDrawable?.stop()
                ivLoading.visibility = View.GONE
                icRefreshStatu.visibility = View.VISIBLE
                icRefreshStatu.setImageResource(R.drawable.ic_refresh_failed)
                mStatusTextView.setText(R.string.xrecyclerview_header_hint_refresh_failure)
            }
        }
        mState = state
    }

    //返回当前状态
    fun getState(): Int {
        return mState
    }

    //完成刷新
    fun refreshComplate(isSuccess: Boolean) {
        //注释的是时间文本  有需要可以去掉  也是在布局里改可见属性
        //        mHeaderTimeView.setText(friendlyTime(new Date()));
        if (isSuccess) {
            setState(STATE_SUCCESS)
        } else {
            setState(STATE_FAILURE)
        }
        postDelayed({ reset() }, 500)
    }

    private fun reset() {
        smoothScrollTo(0)
        postDelayed({ setState(STATE_NORMAL) }, 500)
    }

    /**
     * 刷新头滑动改变
     */
    fun onMove(dalta: Float) {
        if (getVisiableHeight() > 0 || dalta > 0) {
            setVisiableHeight(dalta.toInt() + getVisiableHeight())
            if (mState <= STATE_RELEASE_TO_REFRESH) {// 未处于刷新状态，更新箭头
                if (getVisiableHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH)
                } else {
                    setState(STATE_NORMAL)
                }
            }
        }
    }

    /**
     * 释放刷新头的时候，是否满足刷新的要求
     */
    fun releaseAction(): Boolean {
        var isOnRefresh = false
        val height = getVisiableHeight()
        if (height == 0) {
            isOnRefresh = false
        }
        //刷新时改变状态
        if (getVisiableHeight() > mMeasuredHeight && mState < STATE_REFRESHING) {
            setState(STATE_REFRESHING)
            isOnRefresh = true
        }
        //刷新时回滚到原始高度
        var destHeight = 0
        if (mState == STATE_REFRESHING) {
            destHeight = mMeasuredHeight
        }
        smoothScrollTo(destHeight)
        return isOnRefresh
    }

    //回滚到顶部
    private fun smoothScrollTo(destHeight: Int) {
        val animator = ValueAnimator.ofInt(getVisiableHeight(), destHeight)
        animator.setDuration(300).start()
        animator.addUpdateListener { animation -> setVisiableHeight(animation.animatedValue as Int) }
        animator.start()
    }

    //设置刷新头可见的高度
    fun setVisiableHeight(height: Int) {
        var h = height
        if (h < 0) h = 0
        val lp = mContainer.layoutParams as LinearLayout.LayoutParams
        lp.height = h
        mContainer.layoutParams = lp
    }

    //获得刷新头可见的高度
    fun getVisiableHeight(): Int {
        val lp = mContainer.layoutParams as LinearLayout.LayoutParams
        return lp.height
    }

    fun refreshUpdatedAtValue() {
        if (lastUpdateTime == 0L) {
            return
        }
        val currentTime = System.currentTimeMillis()
        val timePassed = currentTime - lastUpdateTime
        val timeIntoFormat: Long
        val updateAtValue = when {
            timePassed <= 0 -> resources.getString(R.string.not_updated_yet)
            timePassed < minute -> resources.getString(R.string.updated_just_now)
            timePassed < hour -> {
                timeIntoFormat = timePassed / minute
                val value = "${timeIntoFormat}分钟"
                String.format(resources.getString(R.string.updated_at), value)
            }
            timePassed < day -> {
                timeIntoFormat = timePassed / hour
                val value = "${timeIntoFormat}小时"
                String.format(resources.getString(R.string.updated_at), value)
            }
            timePassed < month -> {
                timeIntoFormat = timePassed / day
                val value = "${timeIntoFormat}天"
                String.format(resources.getString(R.string.updated_at), value)
            }
            timePassed < year -> {
                timeIntoFormat = timePassed / month
                val value = "${timeIntoFormat}个月"
                String.format(resources.getString(R.string.updated_at), value)
            }
            else -> {
                timeIntoFormat = timePassed / year
                val value = "${timeIntoFormat}年"
                String.format(resources.getString(R.string.updated_at), value)
            }
        }
        mHeaderTimeView.visibility = View.VISIBLE
        mHeaderTimeView.text = updateAtValue
    }
}
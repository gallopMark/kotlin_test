package com.haoyuinfo.library.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import com.haoyuinfo.library.R
import com.nineoldandroids.view.ViewHelper


class RippleView : LinearLayout {
    private var mDownX: Float = 0f
    private var mDownY: Float = 0f
    private var mAlphaFactor: Float = 0f
    private var mDensity: Float = 0f
    private var mRadius: Float = 0f
    private var mMaxRadius: Float = 0f
    private var mRippleColor: Int = 0
    private var mIsAnimating = false
    private var mHover = true
    private var mRadialGradient: RadialGradient? = null
    private lateinit var mPaint: Paint
    private var mRadiusAnimator: ObjectAnimator? = null
    private fun dp(dp: Int): Float {
        return dp * mDensity + 0.5f
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
        val a = context.obtainStyledAttributes(attrs, R.styleable.RippleView)
        mRippleColor = a.getColor(R.styleable.RippleView_rippleColor, mRippleColor)
        mAlphaFactor = a.getFloat(R.styleable.RippleView_alphaFactor, mAlphaFactor)
        mHover = a.getBoolean(R.styleable.RippleView_hover, mHover)
        a.recycle()
    }

    private fun init(context: Context) {
        mDensity = context.resources.displayMetrics.density
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { alpha = 100 }
        setRippleColor(Color.BLACK, 0.2f)
    }

    fun setRippleColor(rippleColor: Int, alphaFactor: Float) {
        mRippleColor = rippleColor
        mAlphaFactor = alphaFactor
    }

    fun setHover(enabled: Boolean) {
        mHover = enabled
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mMaxRadius = Math.sqrt((w * w + h * h).toDouble()).toFloat()
    }

    private var mAnimationIsCancel: Boolean = false
    private var mRect: Rect? = null

    @SuppressLint("ObjectAnimatorBinding", "ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val superResult = super.onTouchEvent(event)
        if (event.actionMasked == MotionEvent.ACTION_DOWN && isEnabled && mHover) {
            mRect = Rect(left, top, right, bottom)
            mAnimationIsCancel = false
            mDownX = event.x
            mDownY = event.y
            mRadiusAnimator = ObjectAnimator.ofFloat(this, "radius", 0f, dp(50)).apply {
                duration = 400
                interpolator = AccelerateDecelerateInterpolator()
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        setRadius(0f)
                        ViewHelper.setAlpha(this@RippleView, 1f)
                        mIsAnimating = false
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {
                        mIsAnimating = true
                    }
                })
                start()
            }
            if (!superResult) {
                return true
            }
        } else if (event.actionMasked == MotionEvent.ACTION_MOVE && isEnabled && mHover) {
            mDownX = event.x
            mDownY = event.y
            mRect?.let { mAnimationIsCancel = (!it.contains((left + event.x).toInt(), (top + event.y).toInt())) }
            if (mAnimationIsCancel) setRadius(0f) else setRadius(dp(50))
            if (!superResult) {
                return true
            }
        } else if (event.actionMasked == MotionEvent.ACTION_UP && !mAnimationIsCancel && isEnabled) {
            mDownX = event.x
            mDownY = event.y
            val tempRadius = Math.sqrt((mDownX * mDownX + mDownY * mDownY).toDouble()).toFloat()
            val targetRadius = Math.max(tempRadius, mMaxRadius)
            if (mIsAnimating) {
                mRadiusAnimator?.cancel()
            }
            mRadiusAnimator = ObjectAnimator.ofFloat(this, "radius", dp(50), targetRadius).apply {
                duration = 500
                interpolator = AccelerateDecelerateInterpolator()
                addListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animation: Animator?) {
                        mIsAnimating = true
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        setRadius(0f)
                        ViewHelper.setAlpha(this@RippleView, 1f)
                        mIsAnimating = false
                    }

                    override fun onAnimationRepeat(animation: Animator?) {}
                    override fun onAnimationCancel(animation: Animator?) {}
                })
                start()
            }
            if (!superResult) {
                return true
            }
        }
        return superResult
    }

    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = Math.round(Color.alpha(color) * factor)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    fun setRadius(radius: Float) {
        mRadius = radius
        if (mRadius > 0) {
            mRadialGradient = RadialGradient(mDownX, mDownY, mRadius, adjustAlpha(mRippleColor, mAlphaFactor), mRippleColor, Shader.TileMode.MIRROR)
            mPaint.shader = mRadialGradient
        }
        invalidate()
    }

    private val mPath = Path()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isInEditMode) return
        canvas.save()
        mPath.reset()
        mPath.addCircle(mDownX, mDownY, mRadius, Path.Direction.CW)
        canvas.clipPath(mPath)
        canvas.restore()
        canvas.drawCircle(mDownX, mDownY, mRadius, mPaint)
    }
}
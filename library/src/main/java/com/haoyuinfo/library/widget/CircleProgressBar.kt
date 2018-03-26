package com.haoyuinfo.library.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.haoyuinfo.library.R

class CircleProgressBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {
    private var max = 100
    private var progress = 0
    private var arcColor: Int = 0 //圆环的颜色
    private var progressColor: Int = 0  //圆环进度的颜色
    private var arcWidth: Float = 0f //圆环的宽度
    private var progressWidth: Float = 0f //进度圆环宽度
    private var colors: IntArray? = null
    private var startAngle = -90
    private var sweepAngle = 360
    private var currentAngle = 0
    private var aniSpeed: Int
    // 画圆所在的距形区域
    private var bgRect: RectF
    private val arcPaint: Paint
    private val mPaint: Paint
    private var mDrawFilter: PaintFlagsDrawFilter

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        val mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar)
        //获取自定义属性和默认值
        arcColor = mTypedArray.getColor(R.styleable.CircleProgressBar_arc_color, ContextCompat.getColor(context, R.color.darkgrey))
        progressColor = mTypedArray.getColor(R.styleable.CircleProgressBar_progress_color, ContextCompat.getColor(context, R.color.colorPrimary))
        arcWidth = mTypedArray.getDimension(R.styleable.CircleProgressBar_arc_width, dipToPx(context, 2))
        progressWidth = mTypedArray.getDimension(R.styleable.CircleProgressBar_progress_width, dipToPx(context, 4))
        max = mTypedArray.getInt(R.styleable.CircleProgressBar_maxValue, 100)
        progress = mTypedArray.getInt(R.styleable.CircleProgressBar_currentValue, 0)
        val colorArrayId = mTypedArray.getResourceId(R.styleable.CircleProgressBar_color_array, -1)
        if (colorArrayId != -1) {
            colors = resources.getIntArray(colorArrayId)
        }
        startAngle = mTypedArray.getInt(R.styleable.CircleProgressBar_start_angle, -90)
        sweepAngle = mTypedArray.getInt(R.styleable.CircleProgressBar_sweep_angle, 360)
        aniSpeed = mTypedArray.getInt(R.styleable.CircleProgressBar_aniSpeed,
                resources.getInteger(R.integer.circelprogressbar_default_animator_duration))
        setMax(max)
        setProgress(progress)
        mTypedArray.recycle()
        bgRect = RectF()
        //整个弧形画笔
        arcPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = arcWidth
            color = arcColor
        }
        mPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = progressWidth
            color = progressColor
        }
        mDrawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    }

    fun setMax(max: Int) {
        this.max = max
    }

    fun setProgress(progress: Int) {
        setProgress(progress, false)
    }

    fun setProgress(progress: Int, animate: Boolean) {
        this.progress = when {
            progress < 0 -> 0
            progress > max -> max
            else -> progress
        }
        currentAngle = progress * (sweepAngle / max)
        if (animate) setAnimation()
    }

    private fun setAnimation() {
        ValueAnimator.ofInt(this.progress, currentAngle).apply {
            duration = aniSpeed.toLong()
            setTarget(currentAngle)
            addUpdateListener { currentAngle = it.animatedValue as Int }
        }.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val max = Math.max(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(max, max)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawFilter = mDrawFilter  //抗锯齿
        val center = width / 2 //圆形位置
        val radius = if (arcWidth > progressWidth) (center - arcWidth) / 2 else (center - progressWidth) / 2 // 圆环的半径
        bgRect.left = center - radius  //左上角x
        bgRect.top = center - radius    //左上角 y
        bgRect.right = center + radius  //右下角 x
        bgRect.bottom = center + radius //右下角 y
        //画出整个圆弧
        canvas.drawArc(bgRect, startAngle.toFloat(), sweepAngle.toFloat(), false, arcPaint)
        //画出进度圆弧
        getShade()?.let { mPaint.shader = it }
        canvas.drawArc(bgRect, startAngle.toFloat(), currentAngle.toFloat(), false, mPaint)
        invalidate()
    }

    private fun getShade(): Shader? {
        colors?.let {
            val positions = FloatArray(it.size)
            val rotate = 1f / it.size
            for (i in 0 until it.size) {
                positions[i] = (rotate + (i * rotate))
            }
            return SweepGradient((width / 2).toFloat(), (height / 2).toFloat(), colors, null)
        }
        return null
    }

    private fun dipToPx(context: Context, dpVal: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal.toFloat(), context.resources.displayMetrics)
    }
}
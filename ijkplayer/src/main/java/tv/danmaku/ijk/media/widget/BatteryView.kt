package tv.danmaku.ijk.media.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import tv.danmaku.ijk.media.R


/**
 * 创建日期：2018/3/8.
 * 描述:自定义水平、垂直电池控件
 * 作者:xiaoma
 */
class BatteryView : View {
    private var strokeColor: Int = Color.parseColor("#ffffffff")
    private var fillColor: Int = strokeColor
    private var currentPower = 0   //当前电量
    private var totalPower = 100     //总电量
    private var orientation: Int = 0
    private var strokeWidth: Float = 0f
    private var topWidth: Float = 0f
    private var w: Int = 0
    private var h: Int = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BatteryView)
        strokeColor = typedArray.getColor(R.styleable.BatteryView_battery_stroke_color, Color.parseColor("#ffffffff"))
        fillColor = typedArray.getColor(R.styleable.BatteryView_battery_fill_color, Color.parseColor("#ffffffff"))
        orientation = typedArray.getInt(R.styleable.BatteryView_battery_orientation, 0)
        currentPower = typedArray.getInt(R.styleable.BatteryView_battery_current_power, 0)
        totalPower = typedArray.getInt(R.styleable.BatteryView_battery_total_power, 100)
        strokeWidth = typedArray.getDimension(R.styleable.BatteryView_battery_strokeWidth, resources.getDimension(R.dimen.margin_size_1))
        topWidth = typedArray.getDimension(R.styleable.BatteryView_battery_top_width, resources.getDimension(R.dimen.margin_size_4))
        w = measuredWidth
        h = measuredHeight
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //对View上的內容进行测量后得到的View內容占据的宽度
        w = measuredWidth
        //对View上的內容进行测量后得到的View內容占据的高度
        h = measuredHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (orientation == 0) {
            drawHorizontalBattery(canvas)
        } else {
            drawVerticalBattery(canvas)
        }
    }

    private fun drawHorizontalBattery(canvas: Canvas) {
        val paint = Paint()
        paint.color = strokeColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.isAntiAlias = true
        val strokW2 = strokeWidth / 2
        //画电池边框
        val r1 = RectF(strokW2, strokW2, w - topWidth - strokW2, h - strokW2)
        canvas.drawRoundRect(r1, 2f, 2f, paint)
        paint.strokeWidth = 0f
        paint.color = fillColor
        paint.style = Paint.Style.FILL
        //画电池内矩形电量
        val offset = (w - strokeWidth * 2) * currentPower / totalPower
        val margin = 2f
        val r2 = RectF(strokeWidth + margin, strokeWidth + margin, offset - margin, h - strokeWidth - margin)
        //根据电池电量决定电池内矩形电量颜色
        canvas.drawRoundRect(r2, 2f, 2f, paint)
        paint.color = strokeColor
        //画电池头
        val r3 = RectF(w - topWidth, h * 0.25f, w.toFloat(), h * 0.75f)
        canvas.drawRect(r3, paint)
    }

    /**
     * 绘制垂直电池
     * @param canvas
     */
    private fun drawVerticalBattery(canvas: Canvas) {
        val paint = Paint()
        paint.color = strokeColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        val headHeight = (strokeWidth + 0.5f).toInt()
        val rect = RectF(strokeWidth, headHeight + strokeWidth, w - strokeWidth, h - strokeWidth)
        canvas.drawRect(rect, paint)
        paint.strokeWidth = 0f
        val topOffset = (h - headHeight - strokeWidth) * (totalPower - currentPower) / 100.0f
        val rect2 = RectF(strokeWidth, headHeight.toFloat() + strokeWidth + topOffset, w - strokeWidth, h - strokeWidth)
        paint.style = Paint.Style.FILL
        canvas.drawRect(rect2, paint)
        val headRect = RectF(w / 4.0f, 0f, w * 0.75f, headHeight.toFloat())
        canvas.drawRect(headRect, paint)
    }

    /**
     * 设置电池电量
     * @param power
     */
    fun setPower(power: Int) {
        this.currentPower = power
        if (currentPower < 0) {
            currentPower = 0
        }
        invalidate() //刷新VIEW
    }

    /**
     * 设置电池颜色
     *
     * @param color
     */
    fun setStrokeColor(color: Int) {
        this.strokeColor = color
        invalidate()
    }

    fun setFillColor(color: Int) {
        this.fillColor = color
        invalidate()
    }

    /**
     * 获取电池电量
     * @return
     */
    fun getCurrrentPower(): Int {
        return currentPower
    }
}
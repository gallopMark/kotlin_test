package com.uuzuche.zxing.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.PixelFormat.OPAQUE
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.google.zxing.ResultPoint
import com.haoyuinfo.library.utils.ScreenUtils
import com.uuzuche.zxing.R
import com.uuzuche.zxing.camera.CameraManager
import java.util.*

class ViewfinderView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var paint: Paint
    private var resultBitmap: Bitmap? = null
    private var maskColor: Int = 0
    private var resultColor: Int = 0
    private var resultPointColor: Int = 0
    private var possibleResultPoints: MutableCollection<ResultPoint>
    private var lastPossibleResultPoints: Collection<ResultPoint>? = null

    /*手机的屏幕密度*/
    private var density: Float = 0f
    // 扫描线移动的y
    private var scanLineTop: Int = 0
    // 扫描线移动速度
    private var scanSpeed: Int = 0
    // 扫描线
    private var scanLight: Bitmap? = null
    // 是否展示小圆点
    private var isCircle: Boolean = false

    // 扫描框边角颜色
    private var innercornercolor: Int = 0
    // 扫描框边角长度
    private var innercornerlength: Int = 0
    // 扫描框边角宽度
    private var innercornerwidth: Int = 0

    init {
        density = resources.displayMetrics.density
        paint = Paint()
        maskColor = ContextCompat.getColor(context, R.color.viewfinder_mask)
        resultColor = ContextCompat.getColor(context, R.color.result_view)
        resultPointColor = ContextCompat.getColor(context, R.color.possible_result_points)
        possibleResultPoints = HashSet(5)
        scanLight = BitmapFactory.decodeResource(resources, R.drawable.scan_image)
        initInnerRect(context, attrs)
    }

//    constructor(context: Context) : this(context, null, 0)
//    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    private fun initInnerRect(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ViewfinderView)
        // 扫描框距离顶部
        val innerMarginTop = ta.getDimension(R.styleable.ViewfinderView_inner_margintop, -1f)
        if (innerMarginTop != -1f) {
            CameraManager.FRAME_MARGINTOP = innerMarginTop.toInt()
        }
        // 扫描框的宽度
        CameraManager.FRAME_WIDTH = ta.getDimension(R.styleable.ViewfinderView_inner_width, (ScreenUtils.getScreenWidth(context) * 3 / 5).toFloat()).toInt()
        // 扫描框的高度
        CameraManager.FRAME_HEIGHT = ta.getDimension(R.styleable.ViewfinderView_inner_height, (ScreenUtils.getScreenWidth(context) * 3 / 5).toFloat()).toInt()
        // 扫描框边角颜色
        innercornercolor = ta.getColor(R.styleable.ViewfinderView_inner_corner_color, ContextCompat.getColor(context, R.color.colorPrimary))
        // 扫描框边角长度
        innercornerlength = ta.getDimension(R.styleable.ViewfinderView_inner_corner_length, 65f).toInt()
        // 扫描框边角宽度
        innercornerwidth = ta.getDimension(R.styleable.ViewfinderView_inner_corner_width, 15f).toInt()
        // 扫描控件
        scanLight = BitmapFactory.decodeResource(resources, ta.getResourceId(R.styleable.ViewfinderView_inner_scan_bitmap, R.drawable.scan_image))
        // 扫描速度
        scanSpeed = ta.getInt(R.styleable.ViewfinderView_inner_scan_speed, 5)
        isCircle = ta.getBoolean(R.styleable.ViewfinderView_inner_scan_iscircle, true)
        ta.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val frame = CameraManager.get().framingRect ?: return
        val width = canvas.width
        val height = canvas.height
        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.color = if (resultBitmap != null) resultColor else maskColor
        canvas.drawRect(0f, 0f, width.toFloat(), frame.top.toFloat(), paint)
        canvas.drawRect(0f, frame.top.toFloat(), frame.left.toFloat(), (frame.bottom + 1).toFloat(), paint)
        canvas.drawRect((frame.right + 1).toFloat(), frame.top.toFloat(), width.toFloat(), (frame.bottom + 1).toFloat(), paint)
        canvas.drawRect(0f, (frame.bottom + 1).toFloat(), width.toFloat(), height.toFloat(), paint)
        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.alpha = 0xFF
            canvas.drawBitmap(resultBitmap, frame.left.toFloat(), frame.top.toFloat(), paint)
        } else {
            drawFrameBounds(canvas, frame)
            drawScanLight(canvas, frame)
            val currentPossible = possibleResultPoints
            val currentLast = lastPossibleResultPoints
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null
            } else {
                possibleResultPoints = HashSet(5)
                lastPossibleResultPoints = currentPossible
                paint.alpha = OPAQUE
                paint.color = resultPointColor
                if (isCircle) {
                    for (point in currentPossible) {
                        canvas.drawCircle(frame.left + point.x, frame.top + point.y, 6.0f, paint)
                    }
                }
            }
            if (currentLast != null) {
                paint.alpha = OPAQUE / 2
                paint.color = resultPointColor
                if (isCircle) {
                    for (point in currentLast) {
                        canvas.drawCircle(frame.left + point.x, frame.top + point.y, 3.0f, paint)
                    }
                }
            }
            postInvalidateDelayed(100, frame.left, frame.top, frame.right, frame.bottom)
        }
    }

    /**
     * 绘制移动扫描线
     *
     * @param canvas
     * @param frame
     */
    private fun drawScanLight(canvas: Canvas, frame: Rect) {
        if (scanLineTop == 0) {
            scanLineTop = frame.top
        }
        if (scanLineTop >= frame.bottom - 30) {
            scanLineTop = frame.top
        } else {
            scanLineTop += scanSpeed
        }
        val scanRect = Rect(frame.left, scanLineTop, frame.right, scanLineTop + 30)
        canvas.drawBitmap(scanLight, null, scanRect, paint)
    }

    /**
     * 绘制取景框边框
     * @param canvas
     * @param frame
     */
    private fun drawFrameBounds(canvas: Canvas, frame: Rect) {
        /*paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(frame, paint);*/
        paint.color = innercornercolor
        paint.style = Paint.Style.FILL
        val corWidth = innercornerwidth
        val corLength = innercornerlength
        // 左上角
        canvas.drawRect(frame.left.toFloat(), frame.top.toFloat(), (frame.left + corWidth).toFloat(), (frame.top + corLength).toFloat(), paint)
        canvas.drawRect(frame.left.toFloat(), frame.top.toFloat(), (frame.left + corLength).toFloat(), (frame.top + corWidth).toFloat(), paint)
        // 右上角
        canvas.drawRect((frame.right - corWidth).toFloat(), frame.top.toFloat(), frame.right.toFloat(),
                (frame.top + corLength).toFloat(), paint)
        canvas.drawRect((frame.right - corLength).toFloat(), frame.top.toFloat(), frame.right.toFloat(), (frame.top + corWidth).toFloat(), paint)
        // 左下角
        canvas.drawRect(frame.left.toFloat(), (frame.bottom - corLength).toFloat(), (frame.left + corWidth).toFloat(), frame.bottom.toFloat(), paint)
        canvas.drawRect(frame.left.toFloat(), (frame.bottom - corWidth).toFloat(), (frame.left + corLength).toFloat(), frame.bottom.toFloat(), paint)
        // 右下角
        canvas.drawRect((frame.right - corWidth).toFloat(), (frame.bottom - corLength).toFloat(), frame.right.toFloat(), frame.bottom.toFloat(), paint)
        canvas.drawRect((frame.right - corLength).toFloat(), (frame.bottom - corWidth).toFloat(), frame.right.toFloat(), frame.bottom.toFloat(), paint)
        //画扫描框下面的字
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 16 * density
        paint.alpha = 0x40
        paint.typeface = Typeface.create("System", Typeface.BOLD)
        canvas.drawText(resources.getString(R.string.scan_text), frame.centerX().toFloat(), frame.bottom + 30.toFloat() * density, paint)
    }


    fun drawViewfinder() {
        resultBitmap = null
        invalidate()
    }

    fun addPossibleResultPoint(point: ResultPoint) {
        possibleResultPoints.add(point)
    }

}
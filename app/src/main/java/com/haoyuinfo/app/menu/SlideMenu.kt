package com.haoyuinfo.app.menu

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout


class SlideMenu : FrameLayout {
    private lateinit var menuView: View
    private lateinit var mainView: View
    private var menuWidth: Int = 0
    private var menuHeight: Int = 0
    private var mainWidth: Int = 0
    private var dragRange: Int = 0
    private lateinit var viewDragHelper: ViewDragHelper
    private var isOpen = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        viewDragHelper = ViewDragHelper.create(this, callback)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        menuView = getChildAt(0)
        mainView = getChildAt(1)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        menuWidth = menuView.measuredWidth
        menuHeight = menuView.measuredHeight
        mainWidth = mainView.measuredWidth
        dragRange = (menuWidth * 0.6).toInt()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
        return true
    }

    private val callback: ViewDragHelper.Callback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child === menuView || child === mainView
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            if (changedView === menuView) {
                menuView.layout(0, 0, menuWidth, menuHeight)
                if (mainView.left > dragRange) {
                    mainView.layout(dragRange, 0, dragRange + mainWidth, mainView.bottom)
                } else {
                    mainView.layout(mainView.left + dx, 0, mainView.right + dx, mainView.bottom)
                }
            }
            val percent = mainView.left / dragRange.toFloat()
            excuteAnimation(percent)
        }

        private fun excuteAnimation(percent: Float) {
            menuView.scaleX = 0.5f + 0.5f * percent
            menuView.scaleY = 0.5f + 0.5f * percent
            mainView.scaleX = 1 - percent * 0.2f
            mainView.scaleY = 1 - percent * 0.2f
            menuView.translationX = -dragRange / 2 + dragRange / 2 * percent
            menuView.alpha = percent
            background.alpha = ((1 - percent) * 255).toInt()
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
            if (mainView.left > dragRange / 2) {
                open()
            } else {
                close()
            }
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return menuWidth
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            if (child === mainView) {
                if (left < 0) return 0
                if (left > dragRange) return dragRange
            }
            if (child === menuView) {
                mainView.layout(mainView.left + dx, 0, mainView.right + dx, mainView.bottom)
                menuView.layout(0, 0, menuWidth, menuHeight)
                return 0
            }
            return left
        }
    }

    fun toggle() {
        if (isOpen) {
            close()
        } else {
            open()
        }
    }

    fun open() {
        isOpen = true
        viewDragHelper.smoothSlideViewTo(mainView, dragRange, 0)
        ViewCompat.postInvalidateOnAnimation(this@SlideMenu)
    }

    fun close() {
        isOpen = false
        viewDragHelper.smoothSlideViewTo(mainView, 0, 0)
        ViewCompat.postInvalidateOnAnimation(this@SlideMenu)
    }

    override fun computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }
}
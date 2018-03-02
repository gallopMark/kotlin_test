package com.haoyuinfo.library.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation


object AnimationUtil {
    /**
     * 从控件所在位置移动到控件的底部
     * @param Duration 动画时间
     */
    fun moveToViewBottom(v: View, duration: Long) {
        if (v.visibility != View.VISIBLE) return
        val animation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f)
        animation.duration = duration
        v.clearAnimation()
        v.animation = animation
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                v.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
    }

    /**
     * 从控件的底部移动到控件所在位置
     * @param Duration 动画时间
     */
    fun bottomMoveToViewLocation(v: View, duration: Long) {
        if (v.visibility == View.VISIBLE) return
        v.visibility = View.VISIBLE
        val animation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
        animation.duration = duration
        v.clearAnimation()
        v.animation = animation
    }

    /**
     * 从控件所在位置移动到控件的顶部
     * @param Duration 动画时间
     */
    fun moveToViewTop(v: View, duration: Long) {
        if (v.visibility != View.VISIBLE) return
        val animation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f)
        animation.duration = duration
        v.clearAnimation()
        v.animation = animation
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                v.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
    }

    /**
     * 从控件的顶部移动到控件所在位置
     * @param Duration 动画时间
     */
    fun topMoveToViewLocation(v: View, Duration: Long) {
        v.visibility = View.VISIBLE
        val animation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
        animation.duration = Duration
        v.clearAnimation()
        v.animation = animation
    }
}
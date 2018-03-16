package com.haoyuinfo.library.widget

import android.content.Context
import android.view.WindowManager
import android.widget.Toast

class CompatToast(context: Context, animStyle: Int) : Toast(context) {
    init {
        try {
            val clazz = Toast::class.java
            val mTN = clazz.getDeclaredField("mTN")
            mTN.isAccessible = true
            val mObj = mTN.get(this)
            val field = mObj.javaClass.getDeclaredField("mParams")
            field?.let {
                it.isAccessible = true
                it.get(mObj)?.let {
                    if (it is WindowManager.LayoutParams) {
                        it.windowAnimations = animStyle
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
package com.haoyuinfo.library.recyclerviewenhanced

import android.view.MotionEvent

interface OnActivityTouchListener {
    fun getTouchCoordinates(ev: MotionEvent)
}
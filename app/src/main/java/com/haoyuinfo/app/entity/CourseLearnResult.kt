package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import com.haoyuinfo.app.base.BaseResult

class CourseLearnResult : BaseResult<CourseLearnResult.MData>() {
    inner class MData {
        @SerializedName("mCourse")
        private var mCourse: CourseEntity? = null
        @SerializedName("mActivities")
        var mActivities: List<CourseSectionActivity>? = null
            get() = if (field == null) ArrayList() else field

        fun getmCourse(): CourseEntity? {
            return mCourse
        }
    }
}
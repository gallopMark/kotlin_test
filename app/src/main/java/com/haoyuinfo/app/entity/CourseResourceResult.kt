package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import com.haoyu.app.entity.Paginator
import com.haoyuinfo.app.base.BaseResult
import java.util.*

/**
 * 创建日期：2018/3/16.
 * 描述:课程资源结果集
 * 作者:xiaoma
 */
class CourseResourceResult : BaseResult<CourseResourceResult.MData>() {
    inner class MData {
        @SerializedName("resources")
        private var resources: List<CourseResource>? = null
        @SerializedName("paginator")
        var paginator: Paginator? = null

        fun getResources(): List<CourseResource> {
            resources?.let { return it }
            return ArrayList()
        }
    }
}
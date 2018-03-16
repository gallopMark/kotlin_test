package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.DecimalFormat

class CourseEntity : Serializable {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("title")
    var title: String? = null // 课程标题
    @SerializedName("organization")
    var organization: String? = null // 主办方
    @SerializedName("studyHours")
    var studyHours: Double = 0.toDouble()// 学时
    @SerializedName("image")
    var image: String? = null// 课程封面图片地址
    @SerializedName("registerNum")
    var registerNum: Int = 0  //报读数
    @SerializedName("type")
    var type: String? = null
    @SerializedName("code")
    var code: String? = null
    @SerializedName("termNo")
    var termNo: String? = null
    @SerializedName("intro")
    var intro: String? = null
    @SerializedName("mTimePeriod")
    var mTimePeriod: TimePeriod? = null
    @SerializedName("mSections")
    private var mSections: List<CourseSectionEntity>? = null
        get() = if (field == null) ArrayList() else field

    var state: String? = null     //课程状态

    fun getStudyHours(): Int {
        try {
            return Integer.parseInt(DecimalFormat("0").format(studyHours))
        } catch (e: NumberFormatException) {
            return studyHours.toInt()
        }
    }
}
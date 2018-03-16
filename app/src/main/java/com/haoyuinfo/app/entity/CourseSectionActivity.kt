package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import com.haoyuinfo.mediapicker.entity.MultiItem
import java.io.Serializable

class CourseSectionActivity : MultiItem, Serializable {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("relation")
    var relation: Relation? = null
    @SerializedName("state")
    var state: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("type")
    var type: String? = null
    @SerializedName("completeState")
    var completeState: String? = null
    @SerializedName("timePeriod")
    var timePeriod: TimePeriod? = null //活动时间
    @SerializedName("mTimePeriod")
    private var mTimePeriod: TimePeriod? = null //活动时间
    @SerializedName("lastViewTime")
    var lastViewTime: Double = 0.toDouble()
    @SerializedName("timing")
    var isTiming: Boolean = false
    @SerializedName("mVideo")
    var mVideo: VideoMobileEntity? = null
    @SerializedName("score")   //活动得分
    var score: Double = 0.toDouble()
    @SerializedName("inCurrentDate")
    var isInCurrentDate: Boolean = false  //是否在活动时间内

    var isVisiable: Boolean = false

    fun setmTimePeriod(mTimePeriod: TimePeriod) {
        this.mTimePeriod = mTimePeriod
    }

    fun getmTimePeriod(): TimePeriod? {
        return mTimePeriod
    }

    fun getmVideo(): VideoMobileEntity? {
        return mVideo
    }

    fun setmVideo(mVideo: VideoMobileEntity) {
        this.mVideo = mVideo
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            !is CourseSectionActivity -> false
            else -> this === other || id == other.id
        }
    }

    override fun hashCode(): Int {
        return 31 + (id?.hashCode() ?: 0)
    }

    override fun getItemType(): Int {
        return 2
    }
}
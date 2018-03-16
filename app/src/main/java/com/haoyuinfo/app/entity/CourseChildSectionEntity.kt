package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import com.haoyuinfo.mediapicker.entity.MultiItem
import java.io.Serializable
import java.util.*

class CourseChildSectionEntity : MultiItem, Serializable {
    @SerializedName("activities")
    var activities: List<CourseSectionActivity>? = ArrayList()
        get() = if (field == null) ArrayList() else field
    @SerializedName("id")
    var id: String? = null
    @SerializedName("state")
    var state: String? = null
    @SerializedName("completeState")
    var completeState: String? = null
    @SerializedName("title")
    var title: String? = null

    override fun getItemType(): Int {
        return 1
    }

}
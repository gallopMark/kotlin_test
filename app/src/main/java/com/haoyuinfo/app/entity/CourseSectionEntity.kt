package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import com.haoyuinfo.mediapicker.entity.MultiItem
import java.io.Serializable
import java.util.*

class CourseSectionEntity : MultiItem, Serializable {
    @SerializedName("childMSections")
    var childSections: List<CourseChildSectionEntity>? = null
        get() = if (field == null) {
            ArrayList()
        } else field
    @SerializedName("id")
    var id: String? = null
    @SerializedName("title")
    var title: String? = null

    override fun getItemType(): Int {
        return 0
    }
}
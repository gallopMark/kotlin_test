package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class VideoMobileEntity : Serializable {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("viewTime")
    var viewTime: Double = 0.toDouble()
    @SerializedName("interval")
    var interval: Int = 0
    @SerializedName("type")
    var type: String? = null
    @SerializedName("allowDownload")
    var allowDownload: String? = null
    @SerializedName("summary")
    var summary: String? = null
    @SerializedName("urls")
    var urls: String? = null
    @SerializedName("videoFiles")
    var videoFiles: List<MFileInfo>? = null
        get() = if (field == null) {
            ArrayList()
        } else field
    @SerializedName("attchFiles")
    var attchFiles: List<MFileInfo>? = null
        get() = if (field == null) {
            ArrayList()
        } else field
}

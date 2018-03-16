package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MFileInfo : Serializable {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("fileName")
    var fileName: String? = null
    @SerializedName("url")
    var url: String? = null
    @SerializedName("fileSize")
    var fileSize: Long = 0
    @SerializedName("relativeUrl")
    var relativeUrl: String? = null
    private var title: String? = null
    fun setTitle(title: String?) {
        this.title = title
    }

    fun getTitle(): String? {
        return title
    }
}

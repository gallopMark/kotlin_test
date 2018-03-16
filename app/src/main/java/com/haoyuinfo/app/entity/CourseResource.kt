package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName

/**
 * 创建日期：2018/3/16.
 * 描述:课程资源
 * 作者:xiaoma
 */
class CourseResource {
    @SerializedName("id")
    private var id: String? = null
    @SerializedName("title")
    private var title: String? = null
    @SerializedName("fileInfos")
    private var fileInfos: List<MFileInfo>? = null
    @SerializedName("mFileInfos")
    private var mFileInfos: List<MFileInfo>? = null

    fun getId(): String? {
        return id
    }

    fun getTitle(): String? {
        return title
    }

    fun getFileInfos(): List<MFileInfo> {
        fileInfos?.let {
            for (fileInfo in it) {
                fileInfo.setTitle(title)
            }
            return it
        }
        return ArrayList()
    }

    fun getmFileInfos(): List<MFileInfo>? {
        return mFileInfos
    }
}
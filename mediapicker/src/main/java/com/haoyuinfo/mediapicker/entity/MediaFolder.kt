package com.haoyuinfo.mediapicker.entity

class MediaFolder() {
    var name: String? = null  //当前文件夹的名字
    var path: String? = null  //当前文件夹的路径
    var cover: MediaItem? = null   //当前文件夹需要要显示的缩略图，默认为最近的一次图片
    var firstImagePath: String? = null  //当前文件夹的第一张图片路径
    var mediaItems = ArrayList<MediaItem>()  //当前文件夹下所有图片的集合

    constructor(path: String, name: String) : this() {
        this.path = path
        this.name = name
    }

    /** 只要文件夹的路径和名字相同，就认为是相同的文件夹  */
    override fun equals(other: Any?): Boolean {
        return when (other) {
            !is MediaFolder -> false
            else -> this === other || path == other.path && name == other.name
        }
    }

    override fun hashCode(): Int {
        return 31 + (path?.hashCode() ?: 0)
    }
}
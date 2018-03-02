package com.haoyuinfo.mediapicker.entity

import android.os.Parcel
import android.os.Parcelable


class MediaItem : MultiItem, Parcelable {
    var name: String? = null       //图片的名字
    var path: String? = null       //图片的路径
    var mimeType: String? = null   //图片的类型
    var size: Long = 0    //图片或视频大小
    var addTime: Long = 0      //图片的创建时间
    var duration: Long = 0     //视频时长

    companion object {
        const val TYPE_PHOTO = 1
        const val TYPE_VIDEO = 2

        @JvmField
        val CREATOR: Parcelable.Creator<MediaItem> = object : Parcelable.Creator<MediaItem> {
            override fun createFromParcel(source: Parcel): MediaItem {
                return MediaItem(source)
            }

            override fun newArray(size: Int): Array<MediaItem?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor()
    constructor(parcel: Parcel) {
        name = parcel.readString()
        path = parcel.readString()
        mimeType = parcel.readString()
        size = parcel.readLong()
        addTime = parcel.readLong()
        duration = parcel.readLong()
    }

    fun getDuration(): String {
        return timeParse(duration)
    }

    private fun timeParse(duration: Long): String {
        var time = ""
        val minute = duration / 60000
        val seconds = duration % 60000
        val second = Math.round(seconds.toFloat() / 1000).toLong()
        if (minute < 10) {
            time += "0"
        }
        time += minute.toString() + ":"
        if (second < 10) {
            time += "0"
        }
        time += second
        return time
    }

    /* 图片的路径和创建时间相同就认为是同一张图片*/
    override fun equals(other: Any?): Boolean {
        return when (other) {
            !is MediaItem -> false
            else -> this === other || path == other.path && name == other.name
        }
    }

    override fun hashCode(): Int {
        return 31 + (path?.hashCode() ?: 0)
    }

    override fun getItemType(): Int {
        return 2
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(path)
        dest.writeString(mimeType)
        dest.writeLong(size)
        dest.writeLong(addTime)
        dest.writeLong(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

}
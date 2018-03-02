package com.haoyuinfo.mediapicker.model

import android.content.Context
import android.provider.MediaStore
import com.haoyuinfo.mediapicker.R
import com.haoyuinfo.mediapicker.entity.MediaFolder
import com.haoyuinfo.mediapicker.entity.MediaItem
import java.io.File


object MediaModel {

    fun queryImages(context: Context): MutableList<MediaItem> {
        val projection = arrayOf(//查询图片需要的数据列
                MediaStore.Images.Media.DISPLAY_NAME, //图片的显示名称
                MediaStore.Images.Media.DATA, //图片的真实路径
                MediaStore.Images.Media.SIZE, //图片的大小，long型
                MediaStore.Images.Media.MIME_TYPE, //图片的类型     image/jpeg
                MediaStore.Images.Media.DATE_ADDED)    //图片被添加的时间，long型
        val mResolver = context.contentResolver
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor = mResolver.query(uri, projection, null, null, "${projection[4]} DESC")
        val mDatas = ArrayList<MediaItem>()
        cursor.let {
            while (it.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(projection[0]))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(projection[1]))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(projection[2]))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(projection[3]))
                val added = cursor.getLong(cursor.getColumnIndexOrThrow(projection[4]))
                val item = MediaItem().apply {
                    this.name = name
                    this.path = path
                    this.size = size
                    this.mimeType = type
                    this.addTime = added
                }
                mDatas.add(item)
            }
        }
        cursor?.close()
        return mDatas
    }

    fun queryVideos(context: Context): MutableList<MediaItem> {
        val projection = arrayOf(
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION)
        val mResolver = context.contentResolver
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val cursor = mResolver.query(uri, projection, null, null, "${projection[2]} DESC")
        val mDatas = ArrayList<MediaItem>()
        cursor?.let {
            while (cursor.moveToNext()) {
                val path = cursor.getString(cursor.getColumnIndexOrThrow(projection[0]))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(projection[1]))
                val added = cursor.getLong(cursor.getColumnIndexOrThrow(projection[2]))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(projection[3]))
                val duration = cursor.getLong(cursor.getColumnIndexOrThrow(projection[4]))
                val item = MediaItem().apply {
                    this.path = path
                    this.name = name
                    this.addTime = added
                    this.size = size
                    this.duration = duration
                }
                mDatas.add(item)
            }
        }
        cursor?.close()
        return mDatas
    }

    fun splitFolders(context: Context, isImage: Boolean): MutableList<MediaFolder> {
        val mDatas = if (isImage) queryImages(context) else queryVideos(context)
        val folders = ArrayList<MediaFolder>()
        folders.add(MediaFolder().apply {
            mediaItems.addAll(mDatas)
            name = if (isImage) context.resources.getString(R.string.allPhotos)
            else context.resources.getString(R.string.allVideos)
        })
        for (i in 0 until mDatas.size) {
            val item = mDatas[i]
            val parent = File(item.path).parentFile
            val folder = MediaFolder(parent.absolutePath, parent.name)
            if (!folders.contains(folder)) {
                folder.firstImagePath = item.path
                folder.mediaItems.add(item)
                folders.add(folder)
            } else {
                folders[folders.indexOf(folder)].mediaItems.add(item)
            }
        }
        return folders
    }

}
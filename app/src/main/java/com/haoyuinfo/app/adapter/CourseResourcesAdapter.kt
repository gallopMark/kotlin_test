package com.haoyuinfo.app.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.TextView
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapterhelper.BaseArrayRecyclerAdapter
import com.haoyuinfo.app.entity.MFileInfo
import com.haoyuinfo.filepicker.FileHelper
import com.haoyuinfo.library.utils.FileTypeUtils

/**
 * 创建日期：2018/3/16.
 * 描述:课程资源列表适配器
 * 作者:xiaoma
 */
class CourseResourcesAdapter(private val context: Context, mDatas: MutableList<MFileInfo>) : BaseArrayRecyclerAdapter<MFileInfo>(mDatas) {
    override fun bindView(viewType: Int): Int {
        return R.layout.course_resource_item
    }

    override fun onBindHoder(holder: RecyclerHolder, t: MFileInfo, position: Int) {
        val mFileTypeIv = holder.obtainView<ImageView>(R.id.mFileTypeIv)
        val mFileNameTv = holder.obtainView<TextView>(R.id.mFileNameTv)
        val mFileSizeTv = holder.obtainView<TextView>(R.id.mFileSizeTv)
        mFileTypeIv.setColorFilter(ContextCompat.getColor(context, com.haoyuinfo.filepicker.R.color.colorPrimary))
        mFileTypeIv.contentDescription = FileTypeUtils.getDescription(context, t.url)
        mFileTypeIv.setImageResource(FileTypeUtils.getIco(t.url))
        mFileNameTv.text = t.getTitle()
        mFileSizeTv.text = FileHelper.getReadableFileSize(t.fileSize)
    }
}
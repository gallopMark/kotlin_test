package com.haoyuinfo.mediapicker.adapter

import android.content.Context
import android.support.v4.util.ArrayMap
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.haoyuinfo.app.adapterhelper.BaseArrayRecyclerAdapter
import com.haoyuinfo.mediapicker.R
import com.haoyuinfo.mediapicker.entity.MediaItem

class ImageAdapter(private val context: Context, mDatas: MutableList<MediaItem>) : BaseArrayRecyclerAdapter<MediaItem>(mDatas) {
    private var mSelectItem: Int = 0
    private val checkItems = ArrayMap<Int, Boolean>()

    init {
        for (i in 0 until mDatas.size) {
            checkItems[i] = true
        }
    }

    fun setSelectedItem(position: Int) {
        mSelectItem = position
        notifyDataSetChanged()
    }

    fun setUnCheckItem(position: Int, isCheck: Boolean) {
        checkItems[position] = isCheck
        notifyDataSetChanged()
    }

    fun getUnCheckItems(): ArrayMap<Int, Boolean> {
        return checkItems
    }

    override fun bindView(viewType: Int): Int {
        return R.layout.image_item
    }

    override fun onBindHoder(holder: RecyclerHolder, t: MediaItem, position: Int) {
        val flSelected = holder.obtainView<FrameLayout>(R.id.flSelected)
        val ivImage = holder.obtainView<ImageView>(R.id.ivImage)
        val flUnCheck = holder.obtainView<FrameLayout>(R.id.flUnCheck)
        Glide.with(context).load(t.path)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .crossFade()
                .centerCrop()
                .into(ivImage)
        if (mSelectItem == position) {
            flSelected.setBackgroundResource(R.drawable.image_selected)
        } else {
            flSelected.setBackgroundResource(0)
        }
        if (checkItems[position] == true) {
            flUnCheck.visibility = View.GONE
        } else {
            flUnCheck.visibility = View.VISIBLE
        }
    }

}
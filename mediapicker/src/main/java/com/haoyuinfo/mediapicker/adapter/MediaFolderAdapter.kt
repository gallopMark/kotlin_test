package com.haoyuinfo.mediapicker.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.haoyuinfo.app.adapterhelper.BaseArrayRecyclerAdapter
import com.haoyuinfo.mediapicker.R
import com.haoyuinfo.mediapicker.entity.MediaFolder

class MediaFolderAdapter(private val context: Context, mDatas: MutableList<MediaFolder>) : BaseArrayRecyclerAdapter<MediaFolder>(mDatas) {

    private var mSelectItem: Int = 0

    fun setSelectedItem(position: Int) {
        mSelectItem = position
        notifyDataSetChanged()
    }

    override fun bindView(viewType: Int): Int {
        return R.layout.media_folder_item
    }

    override fun onBindHoder(holder: RecyclerHolder, t: MediaFolder, position: Int) {
        val ivImage = holder.obtainView<ImageView>(R.id.ivImage)
        val tvName = holder.obtainView<TextView>(R.id.tvName)
        val tvCount = holder.obtainView<TextView>(R.id.tvCount)
        val ivSelect = holder.obtainView<ImageView>(R.id.ivSelect)
        Glide.with(context).load(t.firstImagePath)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .crossFade()
                .centerCrop()
                .into(ivImage)
        tvName.text = t.name
        val textCount = "${t.mediaItems.size}张"
        tvCount.text = textCount
        ivSelect.visibility = if (mSelectItem == position) View.VISIBLE else View.GONE
    }

}
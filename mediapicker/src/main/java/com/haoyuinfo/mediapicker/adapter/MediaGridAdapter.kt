package com.haoyuinfo.mediapicker.adapter

import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.haoyuinfo.app.adapterhelper.BaseArrayRecyclerAdapter
import com.haoyuinfo.library.utils.PixelFormat
import com.haoyuinfo.library.utils.ScreenUtils
import com.haoyuinfo.mediapicker.R
import com.haoyuinfo.mediapicker.entity.MediaItem
import com.haoyuinfo.mediapicker.entity.MultiItem
import java.io.File


class MediaGridAdapter(private val context: Context,
                       mDatas: MutableList<MultiItem>,
                       private val type: Int,
                       private val isMutily: Boolean,
                       private val limit: Int) : BaseArrayRecyclerAdapter<MultiItem>(mDatas) {
    private var listener: OnItemClickListener? = null
    private var mSelects = ArrayList<MediaItem>()
    private val mImageSize: Int

    init {
        val mWidth = ScreenUtils.getScreenWidth(context)
        val columnSpace = PixelFormat.dp2px(context, 2f)
        mImageSize = (mWidth - columnSpace * 3) / 4
    }

    fun setSelects(images: ArrayList<MediaItem>) {
        mSelects = images
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {   //单选才显示拍照按钮
        return mDatas[position].getItemType()
    }

    override fun bindView(viewType: Int): Int {
        return if (viewType == 1) R.layout.camera_item else R.layout.media_item
    }

    override fun onBindHoder(holder: RecyclerHolder, t: MultiItem, position: Int) {
        holder.itemView.layoutParams = FrameLayout.LayoutParams(mImageSize, mImageSize)
        val viewType = holder.itemViewType
        if (viewType == 1) {
            val ivCamera = holder.obtainView<ImageView>(R.id.ivCamera)
            val tvTake = holder.obtainView<TextView>(R.id.tvTake)
            if (type == MediaItem.TYPE_PHOTO) {
                ivCamera.setImageResource(R.drawable.ic_camera_50dp)
                tvTake.text = context.resources.getString(R.string.takePhoto)
            } else {
                ivCamera.setImageResource(R.drawable.ic_videocam_50dp)
                tvTake.text = context.resources.getString(R.string.recordVideo)
            }
            holder.itemView.setOnClickListener { listener?.onCamera() }
        } else {
            val ivPhoto = holder.obtainView<ImageView>(R.id.ivPhoto)
            val checkBox = holder.obtainView<CheckBox>(R.id.checkBox)
            val tvDuration = holder.obtainView<TextView>(R.id.tvDuration)
            val flSelected = holder.obtainView<FrameLayout>(R.id.flSelected)
            val item = mDatas[position] as MediaItem
            Glide.with(context).load(File(item.path))
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .crossFade()
                    .centerCrop()
                    .override(mImageSize, mImageSize)
                    .into(ivPhoto)
            if (isMutily) {
                checkBox.visibility = View.VISIBLE
            } else {
                checkBox.visibility = View.GONE
            }
            if (type == MediaItem.TYPE_VIDEO) {
                tvDuration.text = item.getDuration()
                tvDuration.visibility = View.VISIBLE
            } else {
                tvDuration.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if (isMutily) {
                    if (checkBox.isChecked) {
                        mSelects.remove(item)
                        checkBox.isChecked = false
                    } else {
                        if (mSelects.contains(item)) {
                            mSelects.remove(item)
                        }
                        if (mSelects.size < limit) {
                            mSelects.add(item)
                            checkBox.isChecked = true
                        } else {
                            checkBox.isChecked = false
                            listener?.onOverSelected(limit)
                        }
                    }
                } else {
                    mSelects.clear()
                    mSelects.add(item)
                }
                listener?.onSelected(mSelects)
            }
            checkBox.setOnClickListener {
                if (mSelects.contains(item)) {
                    checkBox.isChecked = false
                    mSelects.remove(item)
                } else {
                    if (mSelects.size < limit) {
                        mSelects.add(item)
                        checkBox.isChecked = true
                    } else {
                        checkBox.isChecked = false
                        listener?.onOverSelected(limit)
                    }
                }
                listener?.onSelected(mSelects)
            }
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                flSelected.visibility = if (isChecked) View.VISIBLE else View.GONE
            }
            checkBox.isChecked = mSelects.contains(item)
        }
    }

    interface OnItemClickListener {
        fun onCamera()
        fun onOverSelected(limit: Int)
        fun onSelected(mDatas: ArrayList<MediaItem>)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.listener = onItemClickListener
    }
}
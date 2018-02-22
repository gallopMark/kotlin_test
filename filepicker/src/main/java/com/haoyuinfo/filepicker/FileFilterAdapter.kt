package com.haoyuinfo.filepicker

import android.support.v7.widget.AppCompatImageView
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.haoyuinfo.app.adapterhelper.BaseArrayRecyclerAdapter
import java.io.File


class FileFilterAdapter(mDatas: MutableList<File>, private val filter: LFileFilter, private val isMutily: Boolean) : BaseArrayRecyclerAdapter<File>(mDatas) {

    private var mCheckedFlags: BooleanArray = BooleanArray(mDatas.size)
    private var onItemClickListener: OnItemClickListener? = null
    override fun bindView(viewType: Int): Int {
        return R.layout.filefilter_item
    }

    override fun onBindHoder(holder: RecyclerHolder, t: File, position: Int) {
        val itemRoot = holder.obtainView<LinearLayout>(R.id.itemRoot)
        val ivType = holder.obtainView<AppCompatImageView>(R.id.iv_type)
        val tvName = holder.obtainView<TextView>(R.id.tv_name)
        val tvDetail = holder.obtainView<TextView>(R.id.tv_detail)
        val cbChoose = holder.obtainView<CheckBox>(R.id.cb_choose)
        tvName.text = t.name
        if (t.isFile) {
            val fileSize = "文件大小：${FileMamagerHelper.getReadableFileSize(t.length())}"
            tvDetail.text = fileSize
            cbChoose.visibility = View.VISIBLE
        } else {
            ivType.setImageResource(R.drawable.ic_folder_yellow_24dp)
            val files = t.listFiles(filter)
            val length = files?.size ?: 0
            val text = "$length 项"
            tvDetail.text = text
            cbChoose.visibility = View.GONE
        }
        if (!isMutily) {
            cbChoose.visibility = View.GONE
        }
        itemRoot.setOnClickListener {
            if (t.isFile) {
                cbChoose.isChecked = !cbChoose.isChecked
            }
            onItemClickListener?.click(position)
        }
        cbChoose.setOnClickListener {
            //同步复选框和外部布局点击的处理
            onItemClickListener?.click(position)
        }
        cbChoose.setOnCheckedChangeListener { _, isChecked -> mCheckedFlags[position] = isChecked }
        cbChoose.isChecked = mCheckedFlags[position]//用数组中的值设置CheckBox的选中状态
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun click(position: Int)
    }

    fun setDatas(mListData: MutableList<File>) {
        this.mDatas = mListData
        mCheckedFlags = BooleanArray(mListData.size)
    }
}
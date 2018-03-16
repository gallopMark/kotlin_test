package com.haoyuinfo.filepicker

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.haoyuinfo.app.adapterhelper.BaseArrayRecyclerAdapter
import com.haoyuinfo.library.utils.FileTypeUtils
import java.io.File


class FileFilterAdapter(private val context: Context, mDatas: MutableList<File>, private val filter: LFileFilter, private val isMutily: Boolean) : BaseArrayRecyclerAdapter<File>(mDatas) {

    private val mCheckMap = HashMap<String, BooleanArray>()
    private var mCheckedFlags: BooleanArray = BooleanArray(mDatas.size)
    private var listener: OnItemClickListener? = null
    override fun bindView(viewType: Int): Int {
        return R.layout.filefilter_item
    }

    override fun onBindHoder(holder: RecyclerHolder, t: File, position: Int) {
        val itemRoot = holder.obtainView<LinearLayout>(R.id.itemRoot)
        val ivType = holder.obtainView<ImageView>(R.id.iv_type)
        val tvName = holder.obtainView<TextView>(R.id.tv_name)
        val tvDetail = holder.obtainView<TextView>(R.id.tv_detail)
        val cbChoose = holder.obtainView<CheckBox>(R.id.cb_choose)
        tvName.text = t.name
        ivType.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
        ivType.contentDescription = ""
        ivType.setImageResource(FileTypeUtils.getIco(t.absolutePath))
        ivType.contentDescription = FileTypeUtils.getDescription(context, t.path)
        if (t.isFile) {
            val fileSize = "文件大小：${FileHelper.getReadableFileSize(t.length())}"
            tvDetail.text = fileSize
            cbChoose.visibility = View.VISIBLE
        } else {
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
            listener?.click(position)
        }
        cbChoose.setOnClickListener {
            //同步复选框和外部布局点击的处理
            listener?.click(position)
        }
        cbChoose.setOnCheckedChangeListener { _, isChecked ->
            mCheckedFlags[position] = isChecked
            t.parent?.let { mCheckMap.put(it, mCheckedFlags) }
        }
        cbChoose.isChecked = mCheckedFlags[position]//用数组中的值设置CheckBox的选中状态
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.listener = onItemClickListener
    }

    interface OnItemClickListener {
        fun click(position: Int)
    }

    fun setDatas(mDatas: MutableList<File>) {
        this.mDatas = mDatas
        mCheckedFlags = BooleanArray(mDatas.size)
    }

    fun setDatas(mDatas: MutableList<File>, path: String) {
        this.mDatas = mDatas
        mCheckedFlags = mCheckMap[path] ?: BooleanArray(mDatas.size)
    }

}
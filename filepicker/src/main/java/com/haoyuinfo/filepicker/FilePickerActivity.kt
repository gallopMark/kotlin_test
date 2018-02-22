package com.haoyuinfo.filepicker

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.haoyuinfo.library.base.BaseActivity
import com.haoyuinfo.library.dialog.MaterialDialog
import kotlinx.android.synthetic.main.activity_filepicker.*
import java.io.File

class FilePickerActivity : BaseActivity() {
    private var isMutily: Boolean = false
    private lateinit var fileFilter: LFileFilter
    private lateinit var mCurrentPath: String
    private var mDatas = ArrayList<File>()
    private lateinit var adapter: FileFilterAdapter
    private val mListNumbers = ArrayList<String>()//存放选中条目的数据地址
    private val scrolls = HashMap<String, Array<Int>>()

    override fun setLayoutResID(): Int {
        return R.layout.activity_filepicker
    }

    override fun setUp() {
        isMutily = intent.getBooleanExtra("isMutily", false)
        val fileTypes = intent.getStringArrayExtra("fileTypes")
        if (!isMutily) {
            btnSelected.visibility = View.GONE
        }
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val dialog = MaterialDialog(this)
            dialog.setTitle("提示")
            dialog.setMessage("内存卡可用")
            dialog.setPositiveButton("确定", object : MaterialDialog.ButtonClickListener {
                override fun onClick(v: View, dialog: AlertDialog) {
                    dialog.dismiss()
                }
            })
            dialog.setNegativeButton("取消", null)
            dialog.show()
        }
        mCurrentPath = Environment.getExternalStorageDirectory().absolutePath
        tv_parentName.text = mCurrentPath
        fileFilter = LFileFilter(fileTypes)
        mDatas = FileMamagerHelper.getFilesByPath(mCurrentPath, fileFilter)
        adapter = FileFilterAdapter(mDatas, fileFilter, isMutily)
        recylerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recylerview.adapter = adapter
        setOnClick()
    }

    private fun setOnClick() {
        // 返回目录上一级
        tv_parentName.setOnClickListener(View.OnClickListener {
            val tempPath = File(mCurrentPath).parent
            if (tempPath == null || !File(tempPath).exists())
                return@OnClickListener
            mCurrentPath = tempPath
            updateData()
        })
        adapter.setOnItemClickListener(object : FileFilterAdapter.OnItemClickListener {
            override fun click(position: Int) {
                if (isMutily) {
                    if (mDatas[position].isDirectory) {
                        //如果当前是目录，则进入继续查看目录
                        chekInDirectory(position)
                    } else {
                        //如果已经选择则取消，否则添加进来
                        if (mListNumbers.contains(mDatas[position].absolutePath)) {
                            mListNumbers.remove(mDatas[position].absolutePath)
                        } else {
                            mListNumbers.add(mDatas[position].absolutePath)
                        }
                        val text = "选中（${mListNumbers.size}）"
                        btnSelected.text = text
                    }
                } else {
                    //单选模式直接返回
                    if (mDatas[position].isDirectory) {
                        chekInDirectory(position)
                    } else {
                        mListNumbers.clear()
                        mListNumbers.add(mDatas[position].absolutePath)
                        chooseDone()
                    }
                }

            }
        })
        recylerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.layoutManager != null) {
                    getPositionAndOffset()
                }
            }
        })
        btnSelected.setOnClickListener({
            if (mListNumbers.size < 1) {
                Toast.makeText(this, "请选择导入的图书", Toast.LENGTH_LONG).show()
            } else {
                //返回
                chooseDone()
            }
        })
    }

    private fun chekInDirectory(position: Int) {
        mCurrentPath = mDatas[position].absolutePath
        updateData()
    }

    private fun updateData() {
        tv_parentName.text = mCurrentPath
        mDatas = getFileList(mCurrentPath)
        if (mDatas.size > 0) {
            emptyView.visibility = View.GONE
            recylerview.visibility = View.VISIBLE
            adapter.setDatas(mDatas)
            adapter.notifyDataSetChanged()
            scrollToPosition()
        } else {
            recylerview.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
    }

    private fun getPositionAndOffset() {
        val layoutManager = recylerview.layoutManager as LinearLayoutManager
        //获取可视的第一个view
        val topView = layoutManager.getChildAt(0)
        if (topView != null) {
            //获取与该view的顶部的偏移量
            val lastOffset = topView.top
            //得到该View的数组位置
            val lastPosition = layoutManager.getPosition(topView)
            scrolls[mCurrentPath] = arrayOf(lastPosition, lastOffset)
        }
    }

    /**
     * 让RecyclerView滚动到指定位置
     */
    private fun scrollToPosition() {
        if (recylerview.layoutManager != null) {
            scrolls[mCurrentPath]?.let {
                (recylerview.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(it[0], it[1])
            }
        }
    }

    /**
     * 完成提交
     */
    private fun chooseDone() {
        val intent = Intent()
        intent.putStringArrayListExtra("paths", mListNumbers)
        setResult(Activity.RESULT_OK, intent)
        this.finish()
    }

    /**
     * 根据地址获取当前地址下的所有目录和文件，并且排序
     *
     * @param path
     * @return List<File>
    </File> */
    private fun getFileList(path: String?): ArrayList<File> {
        return if (path == null || !File(path).exists()) ArrayList() else FileMamagerHelper.getFilesByPath(path, fileFilter)
    }
}
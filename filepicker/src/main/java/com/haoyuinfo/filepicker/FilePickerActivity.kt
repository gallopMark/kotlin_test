package com.haoyuinfo.filepicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.haoyuinfo.library.base.BaseActivity
import com.haoyuinfo.library.dialog.MaterialDialog
import kotlinx.android.synthetic.main.activity_filepicker.*
import java.io.File


class FilePickerActivity : BaseActivity() {
    private var isShow: Boolean = false
    private var isMutily: Boolean = false
    private lateinit var fileFilter: LFileFilter
    private lateinit var mCurrentPath: String
    private lateinit var mDatas: MutableList<File>
    private var adapter: FileFilterAdapter? = null
    private val mListNumbers = ArrayList<String>()//存放选中条目的数据地址
    private val scrolls = HashMap<String, Array<Int>>()
    private lateinit var selected: String

    override fun setLayoutResID(): Int {
        return R.layout.activity_filepicker
    }

    override fun setUp(savedInstanceState: Bundle?) {
        setToolTitle("我的文件")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                onNext()
            else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        } else {
            onNext()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onNext()
        } else {
            llTips.visibility = View.VISIBLE
            bt_settings.setOnClickListener { openSettings() }
        }
    }

    private fun onNext() {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            val dialog = MaterialDialog(this)
            dialog.setTitle("提示")
            dialog.setMessage("存储卡不可用，请确认存储卡已经挂载！")
            dialog.setPositiveButton("确定", null)
            dialog.show()
            dialog.setOnDismissListener { finish() }
            return
        }
        isShow = true
        container.visibility = View.VISIBLE
        selected = resources.getString(R.string.selected)
        isMutily = intent.getBooleanExtra("isMutily", false)
        if (!isMutily) {
            btnSelected.visibility = View.GONE
        }
        val fileTypes = intent.getStringArrayExtra("fileTypes")
        mCurrentPath = Environment.getExternalStorageDirectory().absolutePath
        fileFilter = LFileFilter(fileTypes)
        icFolder.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary))
        recylerview.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.VERTICAL }
        updateData()
        setOnClick()
    }

    private fun setOnClick() {
        // 返回目录上一级
        llParant.setOnClickListener(View.OnClickListener {
            val tempPath = File(mCurrentPath).parent
            if (tempPath == null || !File(tempPath).exists())
                return@OnClickListener
            mCurrentPath = tempPath
            updateData()
        })
        recylerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
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
        tvParent.text = mCurrentPath
        mDatas = getFileList(mCurrentPath)
        if (adapter == null) {
            adapter = FileFilterAdapter(this, mDatas, fileFilter, isMutily)
            recylerview.adapter = adapter
        } else {
            if (mDatas.size > 0) {
                tvEmpty.visibility = View.GONE
                recylerview.visibility = View.VISIBLE
                adapter?.let {
                    if (isMutily) {
                        it.setDatas(mDatas, mCurrentPath)
                    } else {
                        it.setDatas(mDatas)
                    }
                    it.notifyDataSetChanged()
                }
                scrollToPosition()
            } else {
                recylerview.visibility = View.GONE
                tvEmpty.visibility = View.VISIBLE
            }
        }
        adapter?.setOnItemClickListener(object : FileFilterAdapter.OnItemClickListener {
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
                        val text = "$selected（${mListNumbers.size}）"
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
    }

    private fun getPositionAndOffset() {
        val layoutManager = recylerview.layoutManager as LinearLayoutManager
        val topView = layoutManager.getChildAt(0)  //获取可视的第一个view
        if (topView != null) {
            val lastOffset = topView.top //获取与该view的顶部的偏移量
            val lastPosition = layoutManager.getPosition(topView) //得到该View的数组位置
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
        finish()
    }

    /**
     * 根据地址获取当前地址下的所有目录和文件，并且排序
     *
     * @param path
     * @return List<File>
    </File> */
    private fun getFileList(path: String?): MutableList<File> {
        return if (path == null || !File(path).exists()) ArrayList() else FileHelper.getFilesByPath(path, fileFilter)
    }

    override fun onRestart() {
        super.onRestart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && !isShow) {
                llTips.visibility = View.GONE
                onNext()
            }
        }
    }

    private fun openSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + packageName)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
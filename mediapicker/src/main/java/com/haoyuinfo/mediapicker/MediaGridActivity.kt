package com.haoyuinfo.mediapicker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.View
import com.haoyuinfo.app.adapterhelper.BaseRecyclerAdapter
import com.haoyuinfo.library.base.BaseActivity
import com.haoyuinfo.library.dialog.MaterialDialog
import com.haoyuinfo.library.utils.AnimationUtil
import com.haoyuinfo.library.utils.PixelFormat
import com.haoyuinfo.mediapicker.adapter.MediaFolderAdapter
import com.haoyuinfo.mediapicker.adapter.MediaGridAdapter
import com.haoyuinfo.mediapicker.entity.CameraItem
import com.haoyuinfo.mediapicker.entity.MediaFolder
import com.haoyuinfo.mediapicker.entity.MediaItem
import com.haoyuinfo.mediapicker.entity.MultiItem
import com.haoyuinfo.mediapicker.model.MediaModel
import com.haoyuinfo.mediapicker.utils.MediaPicker
import com.haoyuinfo.mediapicker.view.GridItemDecoration
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_media_grid.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MediaGridActivity : BaseActivity() {
    private var isShow: Boolean = false
    private var type = MediaItem.TYPE_PHOTO
    private var isMutily = false
    private var isCrop = false
    private var limit = 1
    private val mFolders = ArrayList<MediaFolder>()
    private lateinit var folderAdapter: MediaFolderAdapter
    private val mDatas = ArrayList<MultiItem>()
    private lateinit var adapter: MediaGridAdapter
    private var disposable: Disposable? = null
    private var cameraPath: String? = null

    override fun setLayoutResID(): Int {
        return R.layout.activity_media_grid
    }

    override fun setUp(savedInstanceState: Bundle?) {
        type = intent.getIntExtra("mode", MediaItem.TYPE_PHOTO)
        if (type == MediaItem.TYPE_PHOTO) {
            setToolTitle(resources.getString(R.string.photos))
        } else {
            setToolTitle(resources.getString(R.string.videos))
        }
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
        isMutily = intent.getBooleanExtra("mMutilyMode", false)
        limit = intent.getIntExtra("limit", 1)
        isCrop = intent.getBooleanExtra("isCrop", false)
        if (type == MediaItem.TYPE_PHOTO && isMutily) {
            tvFinish.visibility = View.VISIBLE
            tvFinish.isEnabled = false
            tvPreview.visibility = View.VISIBLE
            tvPreview.isEnabled = false
        } else {
            tvFinish.visibility = View.GONE
            tvPreview.visibility = View.GONE
        }
        container.visibility = View.VISIBLE
        rvFolder.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.VERTICAL }
        folderAdapter = MediaFolderAdapter(this, mFolders)
        rvFolder.adapter = folderAdapter
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(GridItemDecoration(4, PixelFormat.dp2px(this, 4f), false))
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        (recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        adapter = MediaGridAdapter(this, mDatas, type, isMutily, limit)
        recyclerView.adapter = adapter
        loadMedia()
    }

    private fun loadMedia() {
        disposable = Flowable.fromCallable {
            if (type == MediaItem.TYPE_PHOTO)
                MediaModel.splitFolders(this, true)
            else
                MediaModel.splitFolders(this, false)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            mFolders.addAll(it)
            folderAdapter.notifyDataSetChanged()
            if (mFolders.size > 0) {
                tvText.text = mFolders[0].name
                if (!isMutily) {
                    mDatas.add(CameraItem())
                }
                mDatas.addAll(mFolders[0].mediaItems)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun setListener() {
        adapter.setOnItemClickListener(object : MediaGridAdapter.OnItemClickListener {
            override fun onCamera() {
                if (type == MediaItem.TYPE_PHOTO) {
                    takePhoto()
                } else {
                    recordVideo()
                }
            }

            override fun onOverSelected(limit: Int) {
                toast("您最多只能选择${limit}张图片")
            }

            override fun onSelected(mDatas: ArrayList<MediaItem>) {
                if (isMutily) {
                    dealWith(mDatas)
                } else {
                    onResult(mDatas)
                }
            }
        })
        folderAdapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(dapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                folderAdapter.setSelectedItem(position)
                mDatas.clear()
                if (!isMutily) {
                    mDatas.add(CameraItem())
                }
                mDatas.addAll(mFolders[position].mediaItems)
                adapter.notifyDataSetChanged()
                tvText.text = mFolders[position].name
                closeFolder()
            }
        })
        flFolder.setOnClickListener { closeFolder() }
        rlFolder.setOnClickListener {
            if (flFolder.visibility == View.VISIBLE) {
                closeFolder()
            } else {
                openFolder()
            }
        }
    }

    private fun takePhoto() {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            val uri = getMediaFileUri(1)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(intent, 1)
        } catch (e: Exception) {

        }
    }

    private fun recordVideo() {
        try {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            val uri = getMediaFileUri(2)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024 * 1024)
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
            startActivityForResult(intent, 2)
        } catch (e: Exception) {
        }
    }

    private fun getMediaFileUri(type: Int): Uri? {
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath)
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return null
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
        val file = if (type == 1) {
            File(dir.path + File.separator + "IMG_" + timeStamp + ".jpg")
        } else {
            File(dir.path + File.separator + "VIDEO_" + timeStamp + ".mp4")
        }
        cameraPath = file.absolutePath
        val authority = BuildConfig.APPLICATION_ID + ".provider"
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FileProvider.getUriForFile(this, authority, file)//通过FileProvider创建一个content类型的Uri
        } else {
            Uri.fromFile(file)
        }
    }

    private fun dealWith(mDatas: ArrayList<MediaItem>) {
        var preview = resources.getString(R.string.preview)
        var finish = resources.getString(R.string.finish)
        if (mDatas.size > 0) {
            tvFinish.isEnabled = true
            tvPreview.isEnabled = true
            finish += "(${mDatas.size}/$limit)"
            preview += "(${mDatas.size})"
        } else {
            tvFinish.isEnabled = false
            tvPreview.isEnabled = false
        }
        tvFinish.text = finish
        tvPreview.text = preview
        tvPreview.setOnClickListener {
            if (rlFolder.visibility == View.VISIBLE) closeFolder()
            val intent = Intent(this@MediaGridActivity, PreViewActivity::class.java)
            intent.putParcelableArrayListExtra("images", mDatas)
            startActivityForResult(intent, 3)
        }
        tvFinish.setOnClickListener { onResult(mDatas) }
    }

    private fun openFolder() {
        AnimationUtil.bottomMoveToViewLocation(flFolder, 300)
    }

    /*** 收起文件夹列表*/
    private fun closeFolder() {
        AnimationUtil.moveToViewBottom(flFolder, 300)
    }

    private fun onResult(mDatas: ArrayList<MediaItem>) {
        val intent = Intent()
        intent.putParcelableArrayListExtra(MediaPicker.EXTRA_PATHS, mDatas)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN && flFolder.visibility == View.VISIBLE) {
            closeFolder()
            return true
        }
        return super.onKeyDown(keyCode, event)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> {    //拍照完成回调
                if (resultCode == RESULT_OK && File(cameraPath).exists()) {
                    if (isCrop) {

                    }
                }
            }
            2 -> {    //录像完成回调
                if (resultCode == RESULT_OK && File(cameraPath).exists()) {

                }
            }
            3 -> {
                val images = data?.getParcelableArrayListExtra<MediaItem>("images")
                images?.let { dealWith(it) }
                when (resultCode) {
                    RESULT_OK -> images?.let { onResult(it) }
                    RESULT_CANCELED -> images?.let {
                        adapter.setSelects(it)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}
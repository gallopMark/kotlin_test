package com.haoyuinfo.mediapicker

import android.content.Intent
import android.os.Bundle
import android.support.v4.util.ArrayMap
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.haoyuinfo.app.adapterhelper.BaseRecyclerAdapter
import com.haoyuinfo.library.base.BaseActivity
import com.haoyuinfo.library.utils.AnimationUtil
import com.haoyuinfo.mediapicker.adapter.ImageAdapter
import com.haoyuinfo.mediapicker.adapter.ImagePageAdapter
import com.haoyuinfo.mediapicker.entity.MediaItem
import kotlinx.android.synthetic.main.activity_preview.*

class PreViewActivity : BaseActivity() {

    private val images = ArrayList<MediaItem>()
    private val checkItems = ArrayMap<Int, MediaItem>()
    private lateinit var adapter: ImagePageAdapter
    private lateinit var imageAdapter: ImageAdapter
    private var isOnClickBack = false
    override fun setLayoutResID(): Int {
        return R.layout.activity_preview
    }

    override fun setUp(savedInstanceState: Bundle?) {
        val arrays = intent.getParcelableArrayListExtra<MediaItem>("images")
        arrays?.let {
            images.addAll(it)
            for (i in 0 until it.size) {
                checkItems[i] = it[i]
            }
        }
        adapter = ImagePageAdapter(this, images)
        viewPager.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.HORIZONTAL }
        imageAdapter = ImageAdapter(this, images)
        recyclerView.adapter = imageAdapter
        checkBox.isChecked = true
        if (this.images.size > 0) {
            setToolTitle("1/${images.size}")
            setFinishButton()
        } else {
            setToolTitle("0/0")
        }
        setListener()
    }

    private fun setListener() {
        adapter.setOnItemClickListener(object : ImagePageAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, path: MediaItem) {
                if (llBottom.visibility == View.VISIBLE) {
                    AnimationUtil.moveToViewBottom(llBottom, 200)
                } else {
                    AnimationUtil.bottomMoveToViewLocation(llBottom, 200)
                }
                actionBar?.let {
                    if (it.visibility == View.VISIBLE) {
                        mMain.systemUiVisibility = View.INVISIBLE
                        AnimationUtil.moveToViewTop(it, 200)
                    } else {
                        mMain.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                        AnimationUtil.topMoveToViewLocation(it, 200)
                    }
                }
            }
        })
        imageAdapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(dapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                viewPager.currentItem = position
                imageAdapter.setSelectedItem(position)
            }
        })
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                setToolTitle("${position + 1}/${images.size}")
                imageAdapter.setSelectedItem(position)
                recyclerView.smoothScrollToPosition(position)
                imageAdapter.getUnCheckItems()[position]?.let { checkBox.isChecked = it }
            }
        })
        llCheck.setOnClickListener { checkBox.isChecked = !checkBox.isChecked }
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) checkItems.remove(viewPager.currentItem) else checkItems[viewPager.currentItem] = images[viewPager.currentItem]
            imageAdapter.setUnCheckItem(viewPager.currentItem, isChecked)
            setFinishButton()
        }
        tvFinish.setOnClickListener {
            isOnClickBack = true
            finish()
        }
    }

    private fun setFinishButton() {
        val text = "${resources.getString(R.string.finish)}(${checkItems.size}/${images.size})"
        tvFinish.text = text
        tvFinish.isEnabled = checkItems.size != 0
    }

    override fun finish() {
        val intent = Intent().apply {
            val images = ArrayList<MediaItem>().apply { addAll(checkItems.values) }
            putParcelableArrayListExtra("images", images)
        }
        val resultCode = if (isOnClickBack) RESULT_OK else RESULT_CANCELED
        setResult(resultCode, intent)
        super.finish()
    }
}
package com.haoyuinfo.app.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.haoyuinfo.app.R
import com.haoyuinfo.app.fragment.page.*
import com.haoyuinfo.library.base.BaseActivity
import kotlinx.android.synthetic.main.activity_courselearn.*


/**
 * 创建日期：2018/3/15.
 * 描述:课程学习主页面
 * 作者:xiaoma
 */
class CourseLearnActivity : BaseActivity() {
    private var courseId: String? = null
    private var training = false
    private val fragments = ArrayList<Fragment>()
    private val tabSpecs = arrayOf("learn", "resource", "discuss", "question", "progress")

    override fun setLayoutResID(): Int {
        return R.layout.activity_courselearn
    }

    override fun setUp(savedInstanceState: Bundle?) {
//        savedInstanceState?.let { tabhost.setCurrentTabByTag(it.getString("tab")) }
        savedInstanceState?.let { viewPager.currentItem = it.getInt("tab") }
        training = intent.getBooleanExtra("training", false)
        courseId = intent.getStringExtra("courseId")
        val title = intent.getStringExtra("courseTitle")
        setToolTitle(title)
        fragments.add(LearnFragment().apply {
            arguments = Bundle().apply {
                putString("courseId", courseId)
                putBoolean("training", training)
            }
        })
        fragments.add(ResourceFragment().apply { arguments = Bundle().apply { putString("courseId", courseId) } })
        fragments.add(DiscussFragment().apply { arguments = Bundle().apply { putString("courseId", courseId) } })
        fragments.add(QuestionFragment().apply { arguments = Bundle().apply { putString("courseId", courseId) } })
        fragments.add(ProgressFragment().apply { arguments = Bundle().apply { putString("courseId", courseId) } })
//        tabhost.setup(this, supportFragmentManager, R.id.content)
//        tabhost.addTab(tabhost.newTabSpec(tabSpecs[0]).setIndicator("A"), LearnFragment::class.java, Bundle().apply {
//            putString("courseId", courseId)
//            putBoolean("training", training)
//        })
//        tabhost.addTab(tabhost.newTabSpec(tabSpecs[1]).setIndicator("B"), ResourceFragment::class.java, Bundle().apply { putString("courseId", courseId) })
//        tabhost.addTab(tabhost.newTabSpec(tabSpecs[2]).setIndicator("C"), DiscussFragment::class.java, Bundle().apply { putString("courseId", courseId) })
//        tabhost.addTab(tabhost.newTabSpec(tabSpecs[3]).setIndicator("D"), QuestionFragment::class.java, Bundle().apply { putString("courseId", courseId) })
//        tabhost.addTab(tabhost.newTabSpec(tabSpecs[4]).setIndicator("E"), ProgressFragment::class.java, Bundle().apply { putString("courseId", courseId) })
        val adapter = MyPageAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbLearn -> viewPager.currentItem = 0
                R.id.rbResource -> viewPager.currentItem = 1
                R.id.rbDiscuss -> viewPager.currentItem = 2
                R.id.rbQuestion -> viewPager.currentItem = 3
                R.id.rbProgress -> viewPager.currentItem = 4
            }
        }
//        tabhost.setOnTabChangedListener {
//            when (it) {
//                tabSpecs[0] -> radioGroup.check(R.id.rbLearn)
//                tabSpecs[1] -> radioGroup.check(R.id.rbResource)
//                tabSpecs[2] -> radioGroup.check(R.id.rbDiscuss)
//                tabSpecs[3] -> radioGroup.check(R.id.rbQuestion)
//                tabSpecs[4] -> radioGroup.check(R.id.rbProgress)
//            }
//        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> radioGroup.check(R.id.rbLearn)
                    1 -> radioGroup.check(R.id.rbResource)
                    2 -> radioGroup.check(R.id.rbDiscuss)
                    3 -> radioGroup.check(R.id.rbQuestion)
                    4 -> radioGroup.check(R.id.rbProgress)
                }
            }

        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        outState.putString("tab", tabhost.currentTabTag)
        outState.putInt("tab", viewPager.currentItem)
    }

    inner class MyPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

    }
}
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
    override fun setLayoutResID(): Int {
        return R.layout.activity_courselearn
    }

    override fun setUp(savedInstanceState: Bundle?) {
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

    inner class MyPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

    }
}
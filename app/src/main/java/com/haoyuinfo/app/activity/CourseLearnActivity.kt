package com.haoyuinfo.app.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
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
        savedInstanceState?.let { viewPager.setCurrentItem(it.getInt("tab"), false) }
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
        viewPager.setPageTransformer(true, MTransformer())
        val adapter = MPageAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbLearn -> viewPager.setCurrentItem(0, false)
                R.id.rbResource -> viewPager.setCurrentItem(1, false)
                R.id.rbDiscuss -> viewPager.setCurrentItem(2, false)
                R.id.rbQuestion -> viewPager.setCurrentItem(3, false)
                R.id.rbProgress -> viewPager.setCurrentItem(4, false)
            }
        }
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
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
        outState.putInt("tab", viewPager.currentItem)
    }

    inner class MTransformer : ViewPager.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val normalizedposition = Math.abs(Math.abs(position) - 1)
            page.alpha = normalizedposition
        }
    }

    inner class MPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }
}
package com.haoyuinfo.app.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.widget.Toast
import com.haoyuinfo.app.R
import com.haoyuinfo.library.base.BaseActivity
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity() {
    override fun setLayoutResID(): Int {
        return R.layout.activity_search
    }

    override fun setUp(savedInstanceState: Bundle?) {
        toolbar.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        searchView.queryHint = "输入搜索内容"
        searchView.setIconifiedByDefault(true)
        searchView.isSubmitButtonEnabled = false
        searchView.onActionViewExpanded()
        val mSearchAutoComplete = searchView.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
        mSearchAutoComplete?.setHintTextColor(ContextCompat.getColor(this, R.color.gainsboro))
        mSearchAutoComplete?.textSize = 16f
        mSearchAutoComplete?.setTextColor(ContextCompat.getColor(this, R.color.white))
        ivSearch.setOnClickListener {
            val text = mSearchAutoComplete.text.toString().trim()
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_LONG).show()
            }
        }
    }
}
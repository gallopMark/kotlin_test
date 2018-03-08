package com.haoyuinfo.library.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.haoyuinfo.library.utils.PreferenceUtils

abstract class BaseFragment : Fragment() {
    lateinit var context: Activity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.context = context as Activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(setLayoutResID(), container, false)
    }

    abstract fun setLayoutResID(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUp()
        setListener()
    }

    open fun setUp() {}

    open fun setListener() {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    open fun initData() {}

    fun getAvatar(): String {
        return PreferenceUtils.getAvatar(context)
    }

    fun getUserId(): String {
        return PreferenceUtils.getUserId(context)
    }

    fun getRealName(): String {
        return PreferenceUtils.getRealName(context)
    }

    fun getDeptName(): String {
        return PreferenceUtils.getDeptName(context)
    }

    fun getRole(): String {
        return PreferenceUtils.getRole(context)
    }

    open fun getAccount(): String {
        return PreferenceUtils.getAccount(context)
    }

    fun getPassWord(): String {
        return PreferenceUtils.getPassWord(context)
    }

    fun isLogin(): Boolean {
        return PreferenceUtils.isLogin(context)
    }

    fun isRemember(): Boolean {
        return PreferenceUtils.isRemember(context)
    }
}
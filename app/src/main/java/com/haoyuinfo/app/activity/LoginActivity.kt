package com.haoyuinfo.app.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.widget.ScrollView
import com.google.gson.Gson
import com.haoyuinfo.app.R
import com.haoyuinfo.app.entity.MobileUser
import com.haoyuinfo.app.entity.UserInfoResult
import com.haoyuinfo.app.utils.Constants
import com.haoyuinfo.app.utils.OkHttpUtils
import com.haoyuinfo.app.utils.PreferenceUtils
import com.haoyuinfo.library.base.BaseActivity
import com.haoyuinfo.library.dialog.LoadingDialog
import com.haoyuinfo.library.utils.Common
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : BaseActivity() {

    private var requestUN = true

    override fun setLayoutResID(): Int {
        return R.layout.activity_login
    }

    override fun setUp(savedInstanceState: Bundle?) {
        val account = PreferenceUtils.getAccount(this)
        etAccount.setText(account)
        etAccount.setSelection(account.length)//将光标移至文字末尾
        cbRemember.isChecked = PreferenceUtils.isRemember(this)
        if (PreferenceUtils.isRemember(this)) etPassword.setText(PreferenceUtils.getPassWord(this))
        etAccount.setOnTouchListener { _, _ ->
            requestUN = true
            false
        }
        etPassword.setOnTouchListener { _, _ ->
            requestUN = false
            false
        }
        bt_login.setOnClickListener {
            Common.hideSoftInput(this)
            val textAccount = etAccount.text.toString()
            if (textAccount.trim().isEmpty()) {
                toast("请输入账号")
                return@setOnClickListener
            }
            val password = etPassword.text.toString()
            if (password.trim().isEmpty()) {
                toast("请输入密码")
                return@setOnClickListener
            }
            login(textAccount, password)
            bt_login.isEnabled = false
        }
        controlKeyboardLayout()
    }

    private fun controlKeyboardLayout() {
        rootView.viewTreeObserver.addOnGlobalLayoutListener({
            val rect = Rect()
            //获取root在窗体的可视区域
            rootView.getWindowVisibleDisplayFrame(rect)
            //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
            val rootInvisibleHeight = rootView.rootView.height - rect.bottom
            //若不可视区域高度大于100，则键盘显示
            if (rootInvisibleHeight > 100) {
                rootView.fullScroll(ScrollView.FOCUS_DOWN) //滚动到底部
            } else {
                //键盘隐藏
                rootView.fullScroll(ScrollView.FOCUS_UP) //滚动到顶部
            }
            requestFocus()
        })
    }

    private fun requestFocus() {
        if (requestUN) {
            etAccount.requestFocus()
            etPassword.clearFocus()
        } else {
            etAccount.clearFocus()
            etPassword.requestFocus()
        }
    }

    private fun login(account: String, password: String) {
        val dialog = LoadingDialog(this).setLoadingText("正在登录…")
        dialog.setCancelable(true)
        dialog.show()
        val disposable = Flowable.fromCallable {
            val map = HashMap<String, String>().apply {
                put("username", account)
                put("password", password)
            }
            val tgt = OkHttpUtils.postAsJson(this, Constants.LOGIN_URL, map)
            var result: UserInfoResult? = null
            if (tgt != null && !tgt.contains("error")) {
                map.clear()
                map["service"] = Constants.SERVICE
                val st = OkHttpUtils.postAsJson(this, tgt, map)
                st?.let {
                    val url = "${Constants.SERVICE}?ticket=$st"
                    val json = OkHttpUtils.getAsJson(this, url)
                    result = Gson().fromJson(json, UserInfoResult::class.java)
                }
            }
            result
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            dialog.dismiss()
            val user = it?.getResponseData()
            val role = user?.role
            if (role != null && role.contains("student")) {
                saveUser(user)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                showCompatDialog("提示", "您不是学员身份，请选择正确版本的App登录")
            }
            bt_login.isEnabled = true
        }, {
            dialog.dismiss()
            bt_login.isEnabled = true
            toast("登录失败")
        })
        dialog.setOnCancelListener { disposable.dispose() }
    }

    private fun saveUser(user: MobileUser) {
        val map = HashMap<String, Any>()
        user.id?.let { map["id"] = it }
        user.avatar?.let { map["avatar"] = it }
        user.realName?.let { map["realName"] = it }
        user.deptName?.let { map["deptName"] = it }
        user.role?.let { map["role"] = it }
        map["account"] = etAccount.text.toString()
        val password = if (cbRemember.isChecked) etPassword.text.toString() else ""
        map["password"] = password
        map["isLogin"] = true
        map["remember"] = cbRemember.isChecked
        PreferenceUtils.saveUser(this, map)
    }
}
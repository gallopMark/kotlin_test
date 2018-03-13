package com.haoyuinfo.app.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.widget.ScrollView
import android.widget.Toast
import com.haoyuinfo.app.R
import com.haoyuinfo.app.base.BaseResult
import com.haoyuinfo.app.entity.MobileUser
import com.haoyuinfo.app.utils.Constants
import com.haoyuinfo.app.utils.OkHttpUtils
import com.haoyuinfo.app.utils.PreferenceUtils
import com.haoyuinfo.library.base.BaseActivity
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Request
import java.util.*

class LoginActivity : BaseActivity() {

    private var requestUN = true
    private var remember = false

    override fun setLayoutResID(): Int {
        return R.layout.activity_login
    }

    override fun setUp(savedInstanceState: Bundle?) {
        etAccount.setText(getAccount())
        if (isRemember()) {
            etPassword.setText(getPassWord())
        }
        cbRemember.setOnCheckedChangeListener { _, isChecked -> remember = isChecked }
        etAccount.setOnTouchListener { _, _ ->
            requestUN = true
            false
        }
        etPassword.setOnTouchListener { _, _ ->
            requestUN = false
            false
        }
        bt_login.setOnClickListener {
            val account = etAccount.text.toString()
            val password = etPassword.text.toString()
            login(account, password)
            bt_login.isEnabled = false
        }
        controlKeyboardLayout()
    }

    private fun controlKeyboardLayout() {
        val rootView = findViewById<ScrollView>(R.id.rootView)
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
        Flowable.fromCallable {
            val map = HashMap<String, String>().apply {
                put("username", account)
                put("password", password)
            }
            val tgt = OkHttpUtils.post(this, Constants.LOGIN_URL, map)
            var st: String? = ""
            if (tgt != null && !tgt.contains("error")) {
                map.clear()
                map["service"] = Constants.SERVICE
                st = OkHttpUtils.post(this, tgt, map)
            }
            st
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            if (!TextUtils.isEmpty(it)) {
                login(it)
            } else {
                bt_login.isEnabled = true
                Toast.makeText(this, "登录失败", Toast.LENGTH_LONG).show()
            }
        }, {
            bt_login.isEnabled = true
            Toast.makeText(this, "登录失败", Toast.LENGTH_LONG).show()
        })
    }

    private fun login(st: String?) {
        val url = "${Constants.SERVICE}?ticket=$st"
        OkHttpUtils.getAsync(this, url, object : OkHttpUtils.ResultCallback<BaseResult<MobileUser>>() {
            override fun onError(request: Request, e: Throwable) {
                bt_login.isEnabled = true
            }

            override fun onResponse(response: BaseResult<MobileUser>?) {
                response?.responseData?.let {
                    it.role?.let {
                        if (it.contains("student")) {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                    saveUser(it)
                }
            }
        })
    }

    private fun saveUser(user: MobileUser) {
        val map = HashMap<String, Any>()
        user.id?.let { map["id"] = it }
        user.avatar?.let { map["avatar"] = it }
        user.realName?.let { map["realName"] = it }
        user.deptName?.let { map["deptName"] = it }
        user.role?.let { map["role"] = it }
        map["account"] = etAccount.text.toString()
        map["password"] = if (remember) {
            etPassword.text.toString()
        } else {
            ""
        }
        map["isLogin"] = remember
        map["remember"] = remember
        PreferenceUtils.saveUser(this, map)
    }
}
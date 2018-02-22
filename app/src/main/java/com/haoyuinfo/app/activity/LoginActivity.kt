package com.haoyuinfo.app.activity

import android.content.Intent
import android.text.TextUtils
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

    private var remember = false
    override fun setLayoutResID(): Int {
        return R.layout.activity_login
    }

    override fun setUp() {
        etAccount.setText(getAccount())
        if (isRemember()) {
            etPassword.setText(getPassWord())
        }
        cbRemember.setOnCheckedChangeListener { _, isChecked -> remember = isChecked }
        bt_login.setOnClickListener {
            val account = etAccount.text.toString()
            val password = etPassword.text.toString()
            login(account, password)
            bt_login.isEnabled = false
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
        map["remember"] = remember
        map["isLogin"] = true
        PreferenceUtils.saveUser(this, map)
    }
}
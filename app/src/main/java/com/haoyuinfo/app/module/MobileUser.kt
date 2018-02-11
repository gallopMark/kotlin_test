package com.haoyuinfo.app.module

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * id	用户ID
 * userName	用户名
 * realName	姓名
 * deptId	单位ID
 * deptName	单位名
 * avatar	用户头像地址
 */
class MobileUser : Serializable {
    @SerializedName("avatar")
    var avatar: String? = null
    @SerializedName("id")
    var id: String? = null
    @SerializedName("realName")
    var realName: String? = null
    @SerializedName("userName")
    var userName: String? = null
    @SerializedName("deptName")
    var deptName: String? = null
    @SerializedName("role")
    var role: String? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("phone")
    var phone: String? = null

}
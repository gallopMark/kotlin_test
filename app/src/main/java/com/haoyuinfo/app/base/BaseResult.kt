package com.haoyuinfo.app.base

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class BaseResult<T> : Serializable {
    @SerializedName("responseCode")
    var responseCode: String? = null
    @SerializedName("responseData")
    var responseData: T? = null
    @SerializedName("responseMsg")
    var responseMsg: String? = null
    @SerializedName("success")
    val success: Boolean? = false

    override fun toString(): String {
        return ("responseCode:" + this.responseCode + "\tsuccess:"
                + this.success + "\tresponseData:" + this.responseData)
    }
}

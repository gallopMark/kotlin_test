package com.haoyuinfo.app.base

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class BaseResult<T> : Serializable {
    @SerializedName("responseCode")
    private var responseCode: String? = null
    @SerializedName("responseData")
    private var responseData: T? = null
    @SerializedName("responseMsg")
    private var responseMsg: String? = null
    @SerializedName("success")
    private val success = false

    fun getResponseCode(): String? {
        return responseCode
    }

    fun getResponseData(): T? {
        return responseData
    }

    fun getResponseMsg(): String? {
        return responseMsg
    }

    fun isSuccess(): Boolean {
        return success
    }

    override fun toString(): String {
        return ("responseCode:" + this.responseCode + "\tsuccess:"
                + this.success + "\tresponseData:" + this.responseData)
    }
}

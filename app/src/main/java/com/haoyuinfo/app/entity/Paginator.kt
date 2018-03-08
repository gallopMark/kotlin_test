package com.haoyu.app.entity

import com.google.gson.annotations.SerializedName

class Paginator {
    @SerializedName("limit")
    var limit: Int = 0
    @SerializedName("page")
    var page: Int = 0
    @SerializedName("totalCount")
    var totalCount: Int = 0
    @SerializedName("offset")
    var offset: Int = 0
    @SerializedName("firstPage")
    var firstPage: Boolean = false
    @SerializedName("lastPage")
    var lastPage: Boolean = false
    @SerializedName("prePage")
    var prePage: Int = 0
    @SerializedName("nextPage")
    var nextPage: Int = 0
    @SerializedName("hasPrePage")
    var hasPrePage: Boolean = false
    @SerializedName("hasNextPage")
    var hasNextPage: Boolean = false
    @SerializedName("startRow")
    var startRow: Int = 0
    @SerializedName("endRow")
    var endRow: Int = 0
    @SerializedName("totalPages")
    var totalPages: Int = 0
    @SerializedName("slider")
    var slider: List<Int>? = null
}

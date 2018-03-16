package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName


class TimePeriod {
    @SerializedName("startTime")
    var startTime: Long = 0
    @SerializedName("endTime")
    var endTime: Long = 0
    @SerializedName("minutes")
    var minutes: Long = 0
    @SerializedName("state")
    var state: String? = null
}
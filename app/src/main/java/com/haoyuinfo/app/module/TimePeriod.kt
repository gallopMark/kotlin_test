package com.haoyuinfo.app.module

import com.google.gson.annotations.SerializedName


class TimePeriod {
    @SerializedName("startTime")
    private var startTime: Long = 0
    @SerializedName("endTime")
    private var endTime: Long = 0
    @SerializedName("minutes")
    private var minutes: Long = 0
    @SerializedName("state")
    private var state: String? = null
}
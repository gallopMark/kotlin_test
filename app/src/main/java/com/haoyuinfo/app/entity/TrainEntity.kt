package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName

class TrainEntity {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("mTrainingTime")
    var mTrainingTime: TimePeriod? = null
}
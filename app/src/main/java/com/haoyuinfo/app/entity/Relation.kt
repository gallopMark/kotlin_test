package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName

class Relation {
    @SerializedName("id")
    private var id: String? = null
    @SerializedName("type")
    private var type: String? = null

    fun getId(): String? {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getType(): String? {
        return type
    }

    fun setType(type: String) {
        this.type = type
    }
}
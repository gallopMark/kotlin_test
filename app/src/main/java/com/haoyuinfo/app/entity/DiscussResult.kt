package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import com.haoyu.app.entity.Paginator
import com.haoyuinfo.app.base.BaseResult

class DiscussResult : BaseResult<DiscussResult.MData>() {
    inner class MData {
        @SerializedName("mDiscussions")
        var discussions: List<DiscussEntity>? = null
            get() = if (field == null) ArrayList() else field
        @SerializedName("paginator")
        var paginator: Paginator? = null
    }
}
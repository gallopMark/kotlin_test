package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import com.haoyu.app.entity.Paginator
import com.haoyuinfo.app.base.BaseResult

class PeerResult : BaseResult<PeerResult.MData>() {

    inner class MData {
        @SerializedName("mUsers")
        var mUsers: List<MobileUser>? = null
            get() = if (field == null) ArrayList() else field
        @SerializedName("paginator")
        var paginator: Paginator? = null
    }
}
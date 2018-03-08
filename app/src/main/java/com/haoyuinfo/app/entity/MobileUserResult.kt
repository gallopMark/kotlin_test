package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import com.haoyu.app.entity.Paginator
import com.haoyuinfo.app.base.BaseResult

class MobileUserResult : BaseResult<MobileUserResult.MData>() {

    inner class MData {
        @SerializedName("mUsers")
        private var mUsers: List<MobileUser>? = null
        @SerializedName("paginator")
        private var paginator: Paginator? = null

        fun getmUsers(): List<MobileUser> {
            mUsers?.let { return it }
            return ArrayList()
        }

        fun setmUsers(mUsers: List<MobileUser>) {
            this.mUsers = mUsers
        }

        fun getPaginator(): Paginator? {
            return paginator
        }

        fun setPaginator(paginator: Paginator) {
            this.paginator = paginator
        }
    }
}
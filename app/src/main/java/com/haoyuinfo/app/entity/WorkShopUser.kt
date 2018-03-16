package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName

/**
 * mWorkshop	工作坊	MWorkshop	N
 * mUser	用户	Object	Y
 * role	角色	String	Y	member:助理
 * master:坊主
 * student:学员
 * evaluate	评价	String	N	null:未评价
 * excellent：优秀
 * qualified：合格
 * fail：不合格
 * point	积分	BigDecimal	N
 * completeActivityNum	完成活动数量	int	N
 * faqQuestionNum	提出问题数量	int	 N
 * uploadResourceNum	上传资源数	Int	N
 */
class WorkShopUser {
    @SerializedName("mWorkshop")
    private var mWorkshop: WorkShop? = null
    @SerializedName("mUser")
    private var mUser: MobileUser? = null
    @SerializedName("role")
    private var role: String? = null
    @SerializedName("evaluate")
    private var evaluate: String? = null
    @SerializedName("point")
    private var point: Double = 0.toDouble()
    @SerializedName("completeActivityNum")
    private var completeActivityNum: Int = 0
    @SerializedName("faqQuestionNum")
    private var faqQuestionNum: Int = 0
    @SerializedName("uploadResourceNum")
    private var uploadResourceNum: Int = 0
    @SerializedName("state")
    private var state: String? = null
    @SerializedName("studyHours")
    private var studyHours: Int = 0

    fun getmWorkshop(): WorkShop? {
        return mWorkshop
    }

    fun getmUser(): MobileUser? {
        return mUser
    }

    fun getRole(): String? {
        return role
    }

    fun getEvaluate(): String? {
        return evaluate
    }

    fun getPoint(): Double {
        return point
    }

    fun getCompleteActivityNum(): Int {
        return completeActivityNum
    }

    fun getFaqQuestionNum(): Int {
        return faqQuestionNum
    }

    fun getUploadResourceNum(): Int {
        return uploadResourceNum
    }

    fun getState(): String? {
        return state
    }

    fun getStudyHours(): Int {
        return studyHours
    }
}
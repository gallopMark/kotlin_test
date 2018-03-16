package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * id	工作坊id	String	Y
 * title	标题	String	Y
 * summary	描述	String	N
 * imageUrl	封面链接	String	N
 * type	类型	String	Y	personal：个人工作坊 train：项目工作坊   template:示范性工作坊
 * qualifiedPoint	工作坊达标分数	BigDecimal	N
 * studentNum	学员数量	int	N
 * memberNum	成员数量	Int	N
 * activityNum	活动数量	int	N
 * faqQuestionNum	问答数量	int	N
 * resourceNum	资源数量	int	N
 * studyHours	学时	Int	N
 * mWorkshopSections	工作坊阶段列表	List	N
 * timePeriod	工作坊开放时间	Object	N
 * summaryExamine	学员考核说明	String	N
 * trainName	所属培训	String	N
 * createTime	创建时间	long	N
 * creator	创建人	MUser	N
 * masters	坊主集合	List<MUser>	N
 */
class WorkShop : Serializable {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("summary")
    var summary: String? = null
    @SerializedName("imageUrl")
    var imageUrl: String? = null
    @SerializedName("type")
    var type: String? = null
    @SerializedName("qualifiedPoint")
    var qualifiedPoint: Int = 0
    @SerializedName("studentNum")
    var studentNum: Int = 0
    @SerializedName("memberNum")
    var memberNum: Int = 0
    @SerializedName("activityNum")
    var activityNum: Int = 0
    @SerializedName("faqQuestionNum")
    var faqQuestionNum: Int = 0
    @SerializedName("resourceNum")
    var resourceNum: Int = 0
    @SerializedName("studyHours")
    var studyHours: Int = 0
    @SerializedName("timePeriod")
    var timePeriod: TimePeriod? = null
    @SerializedName("mtimePeriod")
    private var mTimePeriod: TimePeriod? = null
    @SerializedName("relation")
    var relation: Relation? = null
    @SerializedName("summaryExamine")
    var summaryExamine: String? = null
    @SerializedName("trainName")
    var trainName: String? = null
    @SerializedName("createTime")
    var createTime: Long = 0
    @SerializedName("creator")
    var creator: MobileUser? = null
    @SerializedName("masters")
    var masters: List<MobileUser>? = null
        get() = if (field == null) ArrayList() else field
    var point: Double = 0.toDouble()  //工作坊得分

    fun getmTimePeriod(): TimePeriod? {
        return mTimePeriod
    }

    fun setmTimePeriod(mTimePeriod: TimePeriod) {
        this.mTimePeriod = mTimePeriod
    }
}
package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import com.haoyuinfo.app.base.BaseResult

/**
 * 创建日期：2017/1/7 on 11:14
 * 描述: 个人培训信息
 * 作者:马飞奔 Administrator
 */
class MyTrainInfo : BaseResult<MyTrainInfo.MData>() {

    inner class MData {
        @SerializedName("trainResult")
        var trainResult: TrainResult? = null
        @SerializedName("mCourseRegisters")
        var mCourseRegisters: List<CourseRegisters>? = null
            get() = if (field == null) ArrayList() else field
        @SerializedName("mWorkshopUsers")
        var mWorkshopUsers: List<WorkShopUser>? = null
            get() = if (field == null) ArrayList() else field
        @SerializedName("mCommunityResult")
        var mCommunityResult: CommunityResult? = null
    }

    inner class TrainResult {
        /**
         * * courseStudyHours	课程学时	Int	Y
         * wstsStudyHours	工作坊学时	Int	Y
         * cmtsStudyHours	社区学时	Int	Y
         * registerCourseNum	课程数	Int	Y
         * passCourseNum	合格课程数	Int	Y
         * wstsPoint	工作坊积分	Int	Y
         * getWstsPoint	已获工作坊积分	Int	Y
         * wstsState	工作坊评价	String	Y	excellent:优秀
         * qualified:合格
         * fail:未达标
         * null:未评价
         * cmtsPoint	社区积分	Int	Y
         * getCmtsPoint	已获社区积分	Int	Y
         *
         *
         * trainType	培训考核类型	String	Y	例子：
         * 返回：course,workshop,community
         * 表示考核内容有课程，工作坊，社区
         * 返回：course
         * 只有课程需要考核
         * studyHoursType	学时类型	String	Y	no_limit：不限学时
         * 其他值均为限制学时
         * trainCourseConfig	培训课程配置	String	  Y	noTopic：无主题
         * hasTopic:有主题
         * trainWorkshopConfig   String   train:参与工作坊考核  create:创建工作坊考核
         * chooseCourseType	培训选课方式	String	N	self:自主选课
         */
        @SerializedName("courseStudyHours")
        var courseStudyHours: Int = 0
        @SerializedName("wstsStudyHours")
        var wstsStudyHours: Int = 0
        @SerializedName("cmtsStudyHours")
        var cmtsStudyHours: Int = 0
        @SerializedName("registerCourseNum")
        var registerCourseNum: Int = 0
        @SerializedName("passCourseNum")
        var passCourseNum: Int = 0
        @SerializedName("wstsPoint")
        var wstsPoint: Int = 0
        @SerializedName("getWstsPoint")
        var getWstsPoint: Int = 0
        @SerializedName("wstsState")
        var wstsState: String? = null
        @SerializedName("cmtsPoint")
        var cmtsPoint: Int = 0
        @SerializedName("getCmtsPoint")
        var getCmtsPoint: Int = 0
        @SerializedName("trainType")
        var trainType: String? = null
        @SerializedName("studyHoursType")
        var studyHoursType: String? = null
        @SerializedName("trainCourseConfig")
        var trainCourseConfig: String? = null
        @SerializedName("trainWorkshopConfig")
        var trainWorkshopConfig: String? = null
        @SerializedName("chooseCourseType")
        var chooseCourseType: String? = null
    }

    inner class CourseRegisters {
        @SerializedName("state")
        val state: String? = null
        @SerializedName("mCourse")
        private val mCourse: CourseEntity? = null

        fun getmCourse(): CourseEntity? {
            return mCourse
        }
    }

    inner class CommunityResult {
        @SerializedName("score")
        val score: Int = 0
        @SerializedName("state")
        val state: String? = null
        @SerializedName("mCommunityRelation")
        private val mCommunityRelation: CommunityRelation? = null

        fun getmCommunityRelation(): CommunityRelation? {
            return mCommunityRelation
        }

        inner class CommunityRelation {
            /* score	社区积分	Int	Y
     studyHours	社区学时	Int	Y
     timePeriod	工作坊起止时间	TimePeriod	N*/
            @SerializedName("score")
            val score: Int = 0
            @SerializedName("studyHours")
            val studyHours: Int = 0
            @SerializedName("timePeriod")
            val timePeriod: TimePeriod? = null
        }
    }
}

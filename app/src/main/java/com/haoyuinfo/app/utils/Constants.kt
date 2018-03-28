package com.haoyuinfo.app.utils


object Constants {
    /*登录url*/
    const val LOGIN_URL = "http://nealogin.gdei.edu.cn/v1/tickets"
    /*域名*/
    const val OUTRT_NET = "http://neancts.gdei.edu.cn/app"
    const val REFERER = "http://nealogin.gdei.edu.cn"
    const val SERVICE = "$OUTRT_NET/shiro-cas"
    /*首页个人培训列表*/
    const val MAIN_URL = "$OUTRT_NET/m/uc/listMyTrain"
    /*获取培训信息*/
    const val TRAIN_INFO = "$OUTRT_NET/m/uc/getUserTrainInfo?trainId=%s"
    const val NOSESSION = "\"responseMsg\":\"no session\""
    /*同行*/
    const val PEER_URL = "$OUTRT_NET/m/user?page=%s&limit=%s"
    /*获取消息列表*/
    const val MESSAGE_URL = "$OUTRT_NET/m/message?page=%s&limit=%s"

    /*获取课程学习章节列表*/
    const val COURSE_LEARN = "$OUTRT_NET/%s/study/m/course/%s/study"
    /*获取课程学习资源列表*/
    const val COURSE_RESOURCE = "$OUTRT_NET/m/resource/ncts?resourceRelations[0].relation.id=%s" +
            "&resourceRelations[0].relation.type=course&page=%s&limit=%s&orders=CREATE_TIME.DESC"
    /*获取课程学习讨论列表*/
    const val COURSE_DISCUSS = "$OUTRT_NET/m/discussion?discussionRelations[0].relation.id=%s" +
            "&discussionRelations[0].relation.type=courseStudy&orders=CREATE_TIME.DESC&page=%s&limit=20"
    /*获取课程学习进度*/
    const val COURSE_PROGRESS = "$OUTRT_NET/%s/study/m/course/%s/study_progress"

}
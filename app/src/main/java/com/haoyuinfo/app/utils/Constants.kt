package com.haoyuinfo.app.utils


object Constants {
    const val LOGIN_URL = "http://nealogin.gdei.edu.cn/v1/tickets"
    const val OUTRT_NET = "http://neancts.gdei.edu.cn/app"
    const val REFERER = "http://nealogin.gdei.edu.cn"
    const val SERVICE = "$OUTRT_NET/shiro-cas"
    const val MAIN_URL = "$OUTRT_NET/m/uc/listMyTrain"
    const val TRAIN_INFO = "$OUTRT_NET/m/uc/getUserTrainInfo"
    const val NOSESSION = "\"responseMsg\":\"no session\""
    const val PEER_URL = "$OUTRT_NET/m/user"
    const val COURSE_LEARN = "$OUTRT_NET/%s/study/m/course/%s/study"
    const val COURSE_RESOURCE = "$OUTRT_NET/m/resource/ncts?resourceRelations[0].relation.id=%s" +
            "&resourceRelations[0].relation.type=course&page=%s&limit=%s&orders=CREATE_TIME.DESC"
    const val COURSE_DISCUSS = "$OUTRT_NET/m/discussion?discussionRelations[0].relation.id=%s" +
            "&discussionRelations[0].relation.type=courseStudy&orders=CREATE_TIME.DESC&page=%s"
    const val COURSE_PROGRESS = "$OUTRT_NET/%s/study/m/course/%s/study_progress"
}
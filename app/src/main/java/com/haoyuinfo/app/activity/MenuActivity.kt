package com.haoyuinfo.app.activity

import android.os.Bundle
import com.haoyuinfo.app.R
import com.haoyuinfo.app.fragment.menu.*
import com.haoyuinfo.library.base.BaseActivity
import com.uuzuche.zxing.utils.CodeUtils

class MenuActivity : BaseActivity() {
    companion object {
        const val TYPE_CAPTURE_RESULT = "capture_result"
        const val TYPE_CMTS = "cmts"
        const val TYPE_PEER = "peer"
        const val TYPE_MESSAGE = "message"
        const val TYPE_WORKSHOP = "workshop"
        const val TYPE_CONSULT = "consultation"
        const val TYPE_SETTINGS = "settings"
    }

    override fun setLayoutResID(): Int {
        return R.layout.activity_menu
    }

    override fun setUp(savedInstanceState: Bundle?) {
        val type = intent.type
        val transacation = supportFragmentManager.beginTransaction()
        var title = ""
        when (type) {
            TYPE_CAPTURE_RESULT -> {
                title = resources.getString(R.string.scan_result)
                transacation.replace(R.id.content, CaptureResultFragment().apply {
                    arguments = Bundle().apply {
                        putString(CodeUtils.RESULT_STRING, intent.getStringExtra(CodeUtils.RESULT_STRING))
                    }
                })
            }
            TYPE_CMTS -> {
                title = resources.getString(R.string.teachingResearch)
                transacation.replace(R.id.content, CommunityFragment())
            }
            TYPE_PEER -> {
                title = resources.getString(R.string.peer)
                transacation.replace(R.id.content, PeerFragment())
            }
            TYPE_MESSAGE -> {
                title = resources.getString(R.string.message)
                transacation.replace(R.id.content, MessageFragment())
            }
            TYPE_WORKSHOP -> {
                title = resources.getString(R.string.workshopGroup)
                transacation.replace(R.id.content, WorkShopFragment())
            }
            TYPE_CONSULT -> {
                title = resources.getString(R.string.consulting)
                transacation.replace(R.id.content, ConsultationFragment())
            }
            TYPE_SETTINGS -> {
                title = resources.getString(R.string.settings)
                transacation.replace(R.id.content, SettingsFragment())
            }
        }
        setToolTitle(title)
        transacation.commit()
    }
}
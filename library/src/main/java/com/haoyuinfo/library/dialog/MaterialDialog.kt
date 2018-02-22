package com.haoyuinfo.library.dialog

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.haoyuinfo.library.R
import com.haoyuinfo.library.utils.ScreenUtils

class MaterialDialog(context: Context) : AlertDialog(context) {
    private var titleTV: TextView? = null
    private var messageTV: TextView? = null
    private var btnNeutral: Button? = null
    private var btnPositive: Button? = null
    private var btnNegative: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_material)
        titleTV = findViewById(R.id.tv_tips)
        messageTV = findViewById(R.id.tv_message)
        btnNeutral = findViewById(R.id.bt_neutral)
        btnPositive = findViewById(R.id.bt_makesure)
        btnNegative = findViewById(R.id.bt_cancel)
        val maxWidth = ScreenUtils.getScreenWidth(context) / 4 * 3
        val maxHeight = ScreenUtils.getScreenHeight(context) / 3 * 2
        messageTV?.maxWidth = maxWidth
        messageTV?.maxHeight = maxHeight
    }

    override fun setTitle(title: CharSequence?) {
        if (title == null || title.isEmpty()) {
            titleTV?.visibility = View.GONE
        } else {
            titleTV?.visibility = View.VISIBLE
            titleTV?.text = title
        }
    }

    override fun setMessage(message: CharSequence?) {
        if (message == null || message.isEmpty()) {
            messageTV?.visibility = View.GONE
        } else {
            messageTV?.visibility = View.VISIBLE
            messageTV?.text = message
        }
    }

    fun setNeutralButton(text: String, listener: ButtonClickListener?) {
        btnNeutral?.setText(text)
        btnNeutral?.setVisibility(View.VISIBLE)
        btnNeutral?.setOnClickListener(View.OnClickListener {
            listener?.onClick(it, this@MaterialDialog)
        })
    }

    fun setPositiveButton(text: String, listener: ButtonClickListener?) {
        btnPositive?.visibility = View.VISIBLE
        btnPositive?.text = text
        btnPositive?.setOnClickListener {
            dismiss()
            listener?.onClick(it, this@MaterialDialog)
        }
    }

    fun setNegativeButton(text: String, listener: ButtonClickListener?) {
        btnNegative?.text = text
        btnNegative?.visibility = View.VISIBLE
        btnNegative?.setOnClickListener({
            dismiss()
            listener?.onClick(it, this@MaterialDialog)
        })
    }

    interface ButtonClickListener {
        fun onClick(v: View, dialog: AlertDialog)
    }
}
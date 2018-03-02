package com.haoyuinfo.library.dialog

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.haoyuinfo.library.R
import com.haoyuinfo.library.utils.ScreenUtils


class MaterialDialog(context: Context) : AlertDialog(context,R.style.AppDialog) {
    private var v: View = LayoutInflater.from(context).inflate(R.layout.dialog_material, LinearLayout(context), false)
    private var titleTV: TextView
    private var messageTV: TextView
    private var btnNeutral: TextView
    private var btnPositive: TextView
    private var btnNegative: TextView

    init {
        titleTV = v.findViewById(R.id.tv_tips)
        messageTV = v.findViewById(R.id.tv_message)
        btnNeutral = v.findViewById(R.id.tv_neutral)
        btnPositive = v.findViewById(R.id.tv_makesure)
        btnNegative = v.findViewById(R.id.tv_cancel)
        val maxWidth = ScreenUtils.getScreenWidth(context) / 4 * 3
        val maxHeight = ScreenUtils.getScreenHeight(context) / 3 * 2
        messageTV.maxWidth = maxWidth
        messageTV.maxHeight = maxHeight
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(v)
    }

    override fun setTitle(title: CharSequence?) {
        if (title == null || title.isEmpty()) {
            titleTV.visibility = View.GONE
        } else {
            titleTV.visibility = View.VISIBLE
            titleTV.text = title
        }
    }

    override fun setMessage(message: CharSequence?) {
        if (message == null || message.isEmpty()) {
            messageTV.visibility = View.GONE
        } else {
            messageTV.visibility = View.VISIBLE
            messageTV.text = message
        }
    }

    fun setNeutralButton(text: String, listener: ButtonClickListener?) {
        btnNeutral.setText(text)
        btnNeutral.setVisibility(View.VISIBLE)
        btnNeutral.setOnClickListener({
            listener?.onClick(it, this@MaterialDialog)
        })
    }

    fun setPositiveButton(text: String, listener: ButtonClickListener?) {
        btnPositive.visibility = View.VISIBLE
        btnPositive.text = text
        btnPositive.setOnClickListener {
            dismiss()
            listener?.onClick(it, this@MaterialDialog)
        }
    }

    fun setNegativeButton(text: String, listener: ButtonClickListener?) {
        btnNegative.text = text
        btnNegative.visibility = View.VISIBLE
        btnNegative.setOnClickListener({
            dismiss()
            listener?.onClick(it, this@MaterialDialog)
        })
    }

    interface ButtonClickListener {
        fun onClick(v: View, dialog: AlertDialog)
    }
}
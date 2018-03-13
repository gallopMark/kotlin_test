package com.haoyuinfo.app.fragment.menu

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.net.http.SslError
import android.os.Build
import android.view.View
import android.webkit.*
import com.haoyuinfo.app.R
import com.haoyuinfo.library.base.BaseFragment
import com.uuzuche.zxing.utils.CodeUtils
import kotlinx.android.synthetic.main.fragment_capture_result.*

class CaptureResultFragment : BaseFragment() {
    override fun setLayoutResID(): Int {
        return R.layout.fragment_capture_result
    }

    override fun setUp() {
        val qrCode = arguments?.getString(CodeUtils.RESULT_STRING)
        qrCode?.let {
            if (it.startsWith("http") || it.startsWith("https")) {
                loadUrl(it)
            } else {
                mQRCodeTv.visibility = View.VISIBLE
                mQRCodeTv.text = it
            }
        }
    }

    private fun loadUrl(url: String) {
        webView.visibility = View.VISIBLE
        initWebSettings()
        webView.loadUrl(url)
        webView.webViewClient = object : WebViewClient() {
            @Suppress("OverridingDeprecatedMember")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return true
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                handler.proceed()    //表示等待证书响应
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebSettings() {
        //声明WebSettings子类
        val webSettings = webView.settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.javaScriptEnabled = true
// 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        webSettings.allowFileAccess = false
//设置自适应屏幕，两者合用
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true; // 缩放至屏幕的大小
//缩放操作
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = false //隐藏原生的缩放控件

//其他细节操作
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK //关闭webview中缓存
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
    }

    override fun onResume() {
        webView.onResume()
        webView.resumeTimers()
        super.onResume()
    }

    override fun onPause() {
        webView.onPause()
        webView.pauseTimers()
        super.onPause()
    }

    override fun onDestroyView() {
        webView.destroy()
        super.onDestroyView()
    }
}
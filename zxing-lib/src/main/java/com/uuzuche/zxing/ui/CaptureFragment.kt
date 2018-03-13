@file:Suppress("DEPRECATION")

package com.uuzuche.zxing.ui

import android.content.Context
import android.graphics.Bitmap
import android.hardware.Camera
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.text.TextUtils
import android.view.SurfaceHolder
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.haoyuinfo.library.base.BaseFragment
import com.uuzuche.zxing.R
import com.uuzuche.zxing.camera.CameraManager
import com.uuzuche.zxing.decoding.CaptureActivityHandler
import com.uuzuche.zxing.decoding.InactivityTimer
import com.uuzuche.zxing.utils.CodeUtils
import kotlinx.android.synthetic.main.fragment_capture.*
import java.io.IOException
import java.util.*

class CaptureFragment : BaseFragment(), SurfaceHolder.Callback {
    private var handler: CaptureActivityHandler? = null
    private var hasSurface: Boolean = false
    private var decodeFormats: Vector<BarcodeFormat>? = null
    private var characterSet: String? = null
    private var inactivityTimer: InactivityTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var playBeep = false
    private var vibrate = false
    private var surfaceHolder: SurfaceHolder? = null
    private var analyzeCallback: CodeUtils.AnalyzeCallback? = null
    private var camera: Camera? = null

    fun setAnalyzeCallBack(analyzeCallback: CodeUtils.AnalyzeCallback) {
        this.analyzeCallback = analyzeCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CameraManager.init(context.application)
        hasSurface = false
        inactivityTimer = InactivityTimer(context)
    }

    override fun setLayoutResID(): Int {
        return R.layout.fragment_capture
    }

    override fun setUp() {
        surfaceHolder = surfaceView?.holder
    }

    override fun onResume() {
        super.onResume()
        if (hasSurface) {
            initCamera(surfaceHolder)
        } else {
            surfaceHolder?.addCallback(this)
            surfaceHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
        decodeFormats = null
        characterSet = null
        playBeep = true
        val audioService = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioService.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false
        }
        initBeepSound()
        vibrate = true
    }

    fun handleDecode(result: Result?, barcode: Bitmap) {
        inactivityTimer?.onActivity()
        playBeepSoundAndVibrate()
        if (result == null || TextUtils.isEmpty(result.text)) {
            analyzeCallback?.onAnalyzeFailed()
        } else {
            analyzeCallback?.onAnalyzeSuccess(barcode, result.text)
        }
    }

    private fun initCamera(surfaceHolder: SurfaceHolder?) {
        try {
            CameraManager.get().openDriver(surfaceHolder)
            camera = CameraManager.get().camera
        } catch (e: Exception) {
            return
        }
        if (handler == null) {
            handler = CaptureActivityHandler(this, decodeFormats, characterSet, viewfinderView)
        }
    }

    private fun initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            context.volumeControlStream = AudioManager.STREAM_MUSIC
            mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setOnCompletionListener { mp -> mp?.seekTo(0) }
                val file = resources.openRawResourceFd(R.raw.beep)
                try {
                    setDataSource(file.fileDescriptor, file.startOffset, file.length)
                    file.close()
                    setVolume(0.10f, 0.10f)
                    prepare()
                } catch (e: IOException) {
                    mediaPlayer = null
                }
            }
        }
    }

    private fun playBeepSoundAndVibrate() {
        if (playBeep) {
            mediaPlayer?.start()
        }
        if (vibrate) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(200)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        if (!hasSurface) {
            hasSurface = true
            initCamera(holder)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        hasSurface = false
        camera?.let {
            if (CameraManager.get().isPreviewing) {
                if (!CameraManager.get().isUseOneShotPreviewCallback) {
                    it.setPreviewCallback(null)
                }
                it.stopPreview()
                CameraManager.get().previewCallback.setHandler(null, 0)
                CameraManager.get().autoFocusCallback.setHandler(null, 0)
                CameraManager.get().isPreviewing = false
            }
        }
    }

    fun getHandler(): Handler? {
        return handler
    }

    fun drawViewfinder() {
        viewfinderView.drawViewfinder()
    }

    override fun onPause() {
        super.onPause()
        handler?.quitSynchronously()
        handler = null
        CameraManager.get().closeDriver()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        inactivityTimer?.shutdown()
    }
}
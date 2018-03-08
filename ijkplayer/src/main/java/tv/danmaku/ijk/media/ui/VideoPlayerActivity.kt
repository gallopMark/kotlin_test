package tv.danmaku.ijk.media.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import com.haoyuinfo.library.base.BaseActivity
import com.haoyuinfo.library.utils.NetworkUtils
import kotlinx.android.synthetic.main.activity_videoplayer.*
import kotlinx.android.synthetic.main.video_attrs_turn.*
import kotlinx.android.synthetic.main.video_controller.*
import kotlinx.android.synthetic.main.video_network_mobile.*
import kotlinx.android.synthetic.main.video_player_error.*
import kotlinx.android.synthetic.main.video_progress_turn.*
import tv.danmaku.ijk.media.R
import tv.danmaku.ijk.media.player.IMediaPlayer
import java.text.SimpleDateFormat
import java.util.*


class VideoPlayerActivity : BaseActivity() {

    private var path: String? = null
    private var isHttps = true
    private var isOpenPlayer = false
    private var isPrepared = false
    private var isCompleted = false

    companion object {
        private const val STATE_IDLE = 1
        private const val STATE_PREPARED = 2
        private const val STATE_PLAYING = 3
        private const val STATE_PAUSED = 4
        private const val STATE_COMPLETED = 5
        private const val STATE_ERROR = 6
        private const val STATE_NETWORK = 7

        private const val CODE_PROGRESS = 10
        private const val CODE_ATTRBUTE = 11
        private const val CODE_ENDGESTURE = 12
        private const val CODE_SYSTEM_TIME = 13

        fun openVideo(context: Context, videoPath: String, isHttps: Boolean) {
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra("videoPath", videoPath)
            intent.putExtra("isHttps", isHttps)
            context.startActivity(intent)
        }
    }

    private var mCurrentState = STATE_IDLE
    private var mAudioManager: AudioManager? = null
    private var isOnProgress = false
    private var isOnAttrs = false
    private var isLocked = false  //isLocked是否锁住屏幕
    private var currentDuration = -1  //当前播放位置
    private var lastDuration: Long = -1 //最后播放位置（即播放出错时的位置）
    /*** 视频窗口的宽和高 */
    private var maxVolume: Int = 0
    private var currentVolume = -1
    private var mBrightness = -1f // 亮度

    override fun setLayoutResID(): Int {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)  //保持屏幕常亮
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        return R.layout.activity_videoplayer
    }

    override fun setUp() {
        path = intent.getStringExtra("videoPath")
        isHttps = intent.getBooleanExtra("isHttps", true)
        val paths = arrayOf("https://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8",
                "https://video19.ifeng.com/video09/2014/06/16/1989823-102-086-0009.mp4",
                "http://lom.zqgame.com/v1/video/LOM_Promo~2.flv")
        path = paths[Random().nextInt(paths.size)]
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManager?.let { maxVolume = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }
        volumeControlStream = AudioManager.STREAM_MUSIC
        setVideoController()
        setListener()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        if (isHttps) {   //如果是网络视频，则监听网络变化
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        }
        registerReceiver(receiver, filter)
    }

    private fun setVideoController() {
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            private var firstTouch = false
            private var gesture_progress = false
            private var gesture_volume = false
            private var gesture_bright = false

            override fun onDown(e: MotionEvent): Boolean {
                firstTouch = true
                mController.visibility = View.VISIBLE
                handler.sendEmptyMessage(CODE_SYSTEM_TIME)
                return false
            }

            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                val mOldX = e1.x
                val mOldY = e1.y
                val deltaY = mOldY - e2.y
                val deltaX = mOldX - e2.x
                if (firstTouch) {
                    gesture_progress = Math.abs(distanceX) >= Math.abs(distanceY)
                    gesture_volume = mOldX > mVideoLayout.width * 3.0 / 5 // 音量
                    gesture_bright = mOldX < mVideoLayout.width * 2.0 / 5 // 亮度
                    firstTouch = false
                }
                if (gesture_progress) {
                    val percentage = -deltaX / mVideoLayout.width
                    onProgressSlide(percentage)
                } else {
                    val percent = deltaY / mVideoLayout.height
                    if (gesture_volume) {
                        onVolumeSlide(percent)
                    } else if (gesture_bright) {
                        onBrightnessSlide(percent)
                    }
                }
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return false
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                return false
            }

        })
        mVideoLayout.setOnTouchListener(View.OnTouchListener { _, event ->
            // 手势里除了singleTapUp，没有其他检测up的方法
            if (!isPrepared) return@OnTouchListener true
            ivLock.visibility = View.VISIBLE
            if (isLocked) {
                mController.visibility = View.GONE
                return@OnTouchListener true
            }
            if (event.action == MotionEvent.ACTION_UP) {
                endGesture()
            }
            gestureDetector.onTouchEvent(event)
        })
    }

    /*改变播放进度（快进或快退）*/
    private fun onProgressSlide(percentage: Float) {
        mProgressLayout.visibility = View.VISIBLE
        isOnProgress = true
        val position = videoView.currentPosition
        val duration = videoView.duration
        val deltaMax = Math.min(100 * 1000, duration - position)
        var delta = (deltaMax * percentage).toInt()
        currentDuration = delta + position
        if (currentDuration > duration) {
            currentDuration = duration
        } else if (currentDuration <= 0) {
            currentDuration = 0
            delta = -position
        }
        val showDelta = delta / 1000
        if (showDelta > 0) {
            ivProgress.setImageResource(R.drawable.ic_fast_forward_24dp)
        } else {
            ivProgress.setImageResource(R.drawable.ic_fast_rewind_24dp)
        }
        val fastTxt = "${generateTime(currentDuration.toLong())}/${generateTime(duration.toLong())}"
        tvDuration.text = fastTxt
    }

    /*改变音量大小*/
    private fun onVolumeSlide(percent: Float) {
        mAttrLayout.visibility = View.VISIBLE
        isOnAttrs = true
        if (currentVolume < 0) {
            mAudioManager?.let {
                currentVolume = it.getStreamVolume(AudioManager.STREAM_MUSIC) // 获取当前值
            }
            if (currentVolume < 0) {
                currentVolume = 0
            }
        }
        var mVolume = (percent * maxVolume).toInt() + currentVolume
        if (mVolume > maxVolume) {
            mVolume = maxVolume
        } else if (mVolume < 0) {
            mVolume = 0
        }
        if (mVolume > 0) {
            ivAttrIco.setImageResource(R.drawable.ic_volume_up_24dp)
        } else {
            ivAttrIco.setImageResource(R.drawable.ic_volume_off_24dp)
        }
        attrBar.max = maxVolume
        attrBar.progress = mVolume
        // 变更声音
        mAudioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, 0)
    }

    /*改变亮度*/
    private fun onBrightnessSlide(percent: Float) {
        mAttrLayout.visibility = View.VISIBLE
        isOnAttrs = true
        if (mBrightness < 0) {
            mBrightness = window.attributes.screenBrightness
            if (mBrightness <= 0.00f) {
                mBrightness = 0.50f
            } else if (mBrightness < 0.01f) {
                mBrightness = 0.01f
            }
        }
        val lpa = window.attributes
        lpa.screenBrightness = mBrightness + percent
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f
        }
        window.attributes = lpa
        when {
            lpa.screenBrightness in 0.45..0.55 -> ivAttrIco.setImageResource(R.drawable.ic_brightness_medium_24dp)
            lpa.screenBrightness > 0.55 -> ivAttrIco.setImageResource(R.drawable.ic_brightness_high_24dp)
            else -> ivAttrIco.setImageResource(R.drawable.ic_brightness_low_24dp)
        }
        attrBar.max = 100
        attrBar.progress = (lpa.screenBrightness * 100).toInt()
    }

    private fun endGesture() {
        currentVolume = -1
        mBrightness = -1f
        if (isOnAttrs) {
            isOnAttrs = false
            handler.removeMessages(CODE_ATTRBUTE)
            handler.sendEmptyMessageDelayed(CODE_ATTRBUTE, 2000)
        }
        if (isOnProgress) {
            mProgressLayout.visibility = View.GONE
            isOnProgress = false
            videoView.seekTo(currentDuration)
        }
        handler.removeMessages(CODE_ENDGESTURE)
        handler.sendEmptyMessageDelayed(CODE_ENDGESTURE, 5000)
    }

    private fun setListener() {
        mBackView.setOnClickListener { finish() }
        ivLock.setOnClickListener {
            if (isLocked) {
                ivLock.setImageResource(R.drawable.ic_lock_open_24dp)
                mController.visibility = View.VISIBLE
                handler.sendEmptyMessageDelayed(CODE_ENDGESTURE, 5000)
                isLocked = false
            } else {
                ivLock.setImageResource(R.drawable.ic_lock_close_24dp)
                mController.visibility = View.GONE
                isLocked = true
            }
        }
        ivPlay.setOnClickListener { startVideo() }
        ivPlayState.setOnClickListener {
            if (mCurrentState == STATE_PLAYING) {
                statusChange(STATE_PAUSED)
            } else {
                statusChange(STATE_PLAYING)
            }
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser && !videoView.isPlaying) {
                    statusChange(STATE_PLAYING)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                videoView.seekTo(seekBar.progress)
            }
        })
    }

    override fun initData() {
        if (NetworkUtils.isConnected(this)) {
            if (NetworkUtils.isWifiConnected(this)) {   //如果是wifi网络则打开播放器
                openVideo()
            } else if (NetworkUtils.isMobileConnected(this)) {  //如果是移动网络数据则提醒用户播放产生流量费用
                onMobileNetwork()
            } else {
                ivPlay.visibility = View.VISIBLE
            }
        } else {   //无网络连接
            statusChange(STATE_NETWORK)
        }
    }

    private fun startVideo() {
        if (!isOpenPlayer) {
            openVideo()
        } else {
            ivPlay.visibility = View.GONE
            statusChange(STATE_PLAYING)
        }
    }

    private fun onMobileNetwork() {
        mNetWorkMobile.visibility = View.VISIBLE
        mCloseIv.setOnClickListener { finish() }
        mContinueBt.setOnClickListener { startVideo() }
    }

    private fun openVideo() {
        statusChange(STATE_IDLE)
        videoView.setOnPreparedListener(IMediaPlayer.OnPreparedListener { iMediaPlayer ->
            statusChange(STATE_PREPARED)
            if (lastDuration > 0) {
                iMediaPlayer.seekTo(lastDuration)
                lastDuration = -1
            }
            statusChange(STATE_PLAYING)
            val duration = iMediaPlayer.duration.toInt()
            seekBar.max = duration
            tv_videoSize.text = generateTime(duration.toLong())
        })
        videoView.setOnBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener { _, precent ->
            val duration = videoView.duration
            val secondary = precent * duration / 100
            seekBar.secondaryProgress = secondary
        })
        videoView.setOnErrorListener(IMediaPlayer.OnErrorListener { iMediaPlayer, framework_err, impl_err ->
            if (lastDuration < 0) {
                lastDuration = iMediaPlayer.currentPosition
            }
            statusChange(STATE_ERROR)
            false
        })
        videoView.setOnCompletionListener(IMediaPlayer.OnCompletionListener { statusChange(STATE_COMPLETED) })
    }

    private fun statusChange(newStatu: Int) {
        when (newStatu) {
            STATE_IDLE -> onIdle()
            STATE_PREPARED -> onPrepared()
            STATE_PLAYING -> start()
            STATE_PAUSED -> pause()
            STATE_COMPLETED -> complete()
            STATE_ERROR -> error()
            STATE_NETWORK -> onWithOutNet()
        }
    }

    private fun onIdle() {
        mCurrentState = STATE_IDLE
        mHIntTv.text = "即将播放"
        mHIntTv.visibility = View.VISIBLE
        path?.let { videoView.setVideoPath(it) }
        videoView.setBufferingIndicator(indicator)
        ivPlay.visibility = View.GONE
        isOpenPlayer = true
    }

    private fun onPrepared() {
        mCurrentState = STATE_PREPARED
        mHIntTv.visibility = View.GONE
        isPrepared = true
    }

    private fun start() {
        mCurrentState = STATE_PLAYING
        if (ivPlay.visibility != View.GONE) ivPlay.visibility = View.GONE
        videoView.start()
        ivPlayState.setImageResource(R.drawable.ic_pause_24dp)
        handler.sendEmptyMessage(CODE_PROGRESS)
    }

    private fun pause() {
        mCurrentState = STATE_PAUSED
        videoView.pause()
        ivPlayState.setImageResource(R.drawable.ic_play_24dp)
        handler.removeMessages(CODE_PROGRESS)
    }

    private fun complete() {
        mCurrentState = STATE_COMPLETED
        isCompleted = true
        ivPlayState.setImageResource(R.drawable.ic_play_24dp)
        handler.removeMessages(CODE_PROGRESS)
        mController.visibility = View.GONE
        ivPlay.visibility = View.VISIBLE
    }

    private fun error() {
        mCurrentState = STATE_ERROR
        release()
        if (!NetworkUtils.isConnected(this)) {
            onWithOutNet()
        } else {
            onPlayerError()
        }
        mController.visibility = View.GONE
        ivPlayState.setImageResource(R.drawable.ic_play_24dp)
    }

    private fun onWithOutNet() {
        mCurrentState = STATE_NETWORK
        ivPlay.visibility = View.GONE
        mNetworkOutLine.visibility = View.VISIBLE
    }

    private fun onPlayerError() {
        mPlayerError.visibility = View.VISIBLE
        mCloseIv2.setOnClickListener { finish() }
    }

    private fun generateTime(position: Long): String {
        val totalSeconds = (position / 1000).toInt()
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) String.format("%02d:%02d:%02d", hours, minutes, seconds) else String.format("%02d:%02d", minutes, seconds)
    }

    private fun getSystemTime(): String {
        val systemTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = Date(systemTime)
        return dateFormat.format(date)
    }

    private fun release() {
        isOpenPlayer = false
        isPrepared = false
        isCompleted = false
        handler.removeMessages(CODE_PROGRESS)
        videoView.stopPlayback()
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CODE_PROGRESS -> {
                    val position = videoView.currentPosition
                    seekBar.progress = position
                    tv_current.text = generateTime(position.toLong())
                    sendEmptyMessageDelayed(CODE_PROGRESS, 1000)
                }
                CODE_ATTRBUTE -> mAttrLayout.visibility = View.GONE
                CODE_ENDGESTURE -> {
                    ivLock.visibility = View.GONE
                    mController.visibility = View.GONE
                    removeMessages(CODE_SYSTEM_TIME)
                }
                CODE_SYSTEM_TIME -> {
                    tvCurrentTime.text = getSystemTime()
                    sendEmptyMessageDelayed(CODE_SYSTEM_TIME, 60 * 1000)
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                if (NetworkUtils.isConnected(context)) {
                    mNetworkOutLine.visibility = View.GONE
                    if (NetworkUtils.isWifiConnected(context)) {
                        if (mNetWorkMobile.visibility != View.GONE) mNetWorkMobile.visibility = View.GONE
                        startVideo()
                    } else if (NetworkUtils.isMobileConnected(context)) {
                        onMobileNetwork()
                    } else {
                        if (mCurrentState != STATE_ERROR)
                            ivPlay.visibility = View.VISIBLE
                    }
                }
            } else if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                batterView.setPower(level)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        if (isOpenPlayer && !isCompleted) {
            statusChange(STATE_PAUSED)
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (isOpenPlayer && !isCompleted) {
            if (NetworkUtils.isWifiConnected(this)) {
                statusChange(STATE_PLAYING)
            } else if (NetworkUtils.isMobileConnected(this)) {
                mNetWorkMobile.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        videoView.stopPlayback()
        handler.removeCallbacksAndMessages(null)
        unregisterReceiver(receiver)
        super.onDestroy()
    }
}
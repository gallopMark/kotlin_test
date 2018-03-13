package tv.danmaku.ijk.media.widget

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.MediaController
import tv.danmaku.ijk.media.FileMediaDataSource
import tv.danmaku.ijk.media.IMediaController
import tv.danmaku.ijk.media.IRenderView
import tv.danmaku.ijk.media.R
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.File
import java.util.*

@Suppress("DEPRECATION")
class VideoView : FrameLayout, MediaController.MediaPlayerControl {
    // settable by the client
    private var mUri: Uri? = null
    private var mHeaders: Map<String, String>? = null

    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    companion object {
        /* 空闲*/
        const val STATE_IDLE = 330
        /* 播放出错*/
        const val STATE_ERROR = 331
        /* 准备中/加载中*/
        const val STATE_PREPARING = 332
        /* 准备完成*/
        const val STATE_PREPARED = 333
        /* 播放中*/
        const val STATE_PLAYING = 334
        /* 暂停*/
        const val STATE_PAUSED = 335
        /* 播放完成*/
        const val STATE_COMPLETED = 336

        const val RENDER_NONE = 0
        const val RENDER_SURFACE_VIEW = 1
        const val RENDER_TEXTURE_VIEW = 2
    }

    private var mCurrentState = STATE_IDLE
    private var mTargetState = STATE_IDLE
    private var indicator: View? = null
    // All the stuff we need for playing and showing a video
    private var mSurfaceHolder: IRenderView.ISurfaceHolder? = null
    private var mMediaPlayer: IMediaPlayer? = null
    // private int         mAudioSession;
    private var mVideoWidth: Int = 0
    private var mVideoHeight: Int = 0
    private var mSurfaceWidth: Int = 0
    private var mSurfaceHeight: Int = 0
    private var mVideoRotationDegree: Int = 0
    private var mMediaController: IMediaController? = null
    private var mOnCompletionListener: IMediaPlayer.OnCompletionListener? = null
    private var mOnPreparedListener: IMediaPlayer.OnPreparedListener? = null
    private var mOnBufferingUpdateListener: IMediaPlayer.OnBufferingUpdateListener? = null
    private var mCurrentBufferPercentage: Int = 0
    private var mOnErrorListener: IMediaPlayer.OnErrorListener? = null
    private var mOnInfoListener: IMediaPlayer.OnInfoListener? = null
    private var mOnVideoSizeChangedListener: IMediaPlayer.OnVideoSizeChangedListener? = null
    private var mOnSeekCompleteListener: IMediaPlayer.OnSeekCompleteListener? = null
    private var mSeekWhenPrepared: Long = 0  // recording the seek position while preparing
    private val mCanPause = true
    /** Subtitle rendering widget overlaid on top of the video. */
    // private RenderingWidget mSubtitleWidget;
    /**
     * Listener for changes to subtitle data, used to redraw when needed.
     */
    // private RenderingWidget.OnChangedListener mSubtitlesChangedListener;
    private var mAppContext: Context? = null
    private var mRenderView: IRenderView? = null
    private var mVideoSarNum: Int = 0
    private var mVideoSarDen: Int = 0
    private var usingMediaCodec = false
    private var usingMediaCodecAutoRotate = false
    private var usingOpenSLES = false
    private val pixelFormat = ""//Auto Select=,RGB 565=fcc-rv16,RGB 888X=fcc-rv32,YV12=fcc-yv12,默认为RGB 888X
    private var enableSurfaceView = true
    private var enableTextureView = false
    private var enableNoView = false

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        val mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.VideoView)
        enableSurfaceView = mTypedArray.getBoolean(R.styleable.VideoView_enableSurfaceView, true)
        enableTextureView = mTypedArray.getBoolean(R.styleable.VideoView_enableTextureView, false)
        enableNoView = mTypedArray.getBoolean(R.styleable.VideoView_enableNoView, false)
        mTypedArray.recycle()
        mAppContext = context.applicationContext
        initRenders()
        mVideoWidth = 0
        mVideoHeight = 0
        // REMOVED: getHolder().addCallback(mSHCallback);
        // REMOVED: getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        // REMOVED: mPendingSubtitleTracks = new Vector<Pair<InputStream, MediaFormat>>();
        mCurrentState = STATE_IDLE
        mTargetState = STATE_IDLE
    }

    private fun setRenderView(renderView: IRenderView?) {
        if (mRenderView != null) {
            mMediaPlayer?.setDisplay(null)
            val renderUIView = mRenderView?.getView()
            mRenderView?.removeRenderCallback(mSHCallback)
            mRenderView = null
            removeView(renderUIView)
        }
        if (renderView == null)
            return
        mRenderView = renderView
        renderView.setAspectRatio(mCurrentAspectRatio)
        if (mVideoWidth > 0 && mVideoHeight > 0)
            renderView.setVideoSize(mVideoWidth, mVideoHeight)
        if (mVideoSarNum > 0 && mVideoSarDen > 0)
            renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen)
        val renderUIView = mRenderView?.getView()
        val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER)
        renderUIView?.layoutParams = lp
        addView(renderUIView)
        mRenderView?.addRenderCallback(mSHCallback)
        mRenderView?.setVideoRotation(mVideoRotationDegree)
    }

    private fun setRender(render: Int) {
        when (render) {
            RENDER_NONE -> setRenderView(null)
            RENDER_TEXTURE_VIEW -> {
                val renderView = TextureRenderView(context)
                mMediaPlayer?.let {
                    renderView.getSurfaceHolder().bindToMediaPlayer(mMediaPlayer)
                    renderView.setVideoSize(it.videoWidth, it.videoHeight)
                    renderView.setVideoSampleAspectRatio(it.videoSarNum, it.videoSarDen)
                    renderView.setAspectRatio(mCurrentAspectRatio)
                }
                setRenderView(renderView)
            }
            RENDER_SURFACE_VIEW -> {
                val renderView = SurfaceRenderView(context)
                setRenderView(renderView)
            }
        }
    }

    fun setBufferingIndicator(var1: View) {
        this.indicator = var1
        if (var1.visibility != View.GONE) {
            var1.visibility = View.GONE
        }
    }

    fun setUsingMediaCodec(usingMediaCodec: Boolean) {
        this.usingMediaCodec = usingMediaCodec
    }

    fun setUsingMediaCodecAutoRotate(usingMediaCodecAutoRotate: Boolean) {
        this.usingMediaCodecAutoRotate = usingMediaCodecAutoRotate
    }

    fun setUsingOpenSLES(usingOpenSLES: Boolean) {
        this.usingOpenSLES = usingOpenSLES
    }

    /**
     * Sets video path.
     *
     * @param path the path of the video.
     */
    fun setVideoPath(path: String) {
        setVideoURI(Uri.parse(path))
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    fun setVideoURI(uri: Uri) {
        setVideoURI(uri, null)
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri     the URI of the video.
     * @param headers the headers for the URI request.
     * Note that the cross domain redirection is allowed by default, but that can be
     * changed with key/value pairs through the headers parameter with
     * "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     * to disallow or allow cross domain redirection.
     */
    fun setVideoURI(uri: Uri, headers: Map<String, String>?) {
        mUri = uri
        mHeaders = headers
        mSeekWhenPrepared = 0
        openVideo()
        requestLayout()
        invalidate()
    }

    private fun openVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return
        }
        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false)
        val am = mAppContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        try {
            mMediaPlayer = IjkMediaPlayer().apply {
                //                ijkMediaPlayer.native_setLogLevel(isDebug ? IjkMediaPlayer.IJK_LOG_DEBUG : IjkMediaPlayer.IJK_LOG_ERROR);
                if (usingMediaCodec) {
                    setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
                    if (usingMediaCodecAutoRotate) {
                        setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1)
                    } else {
                        setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0)
                    }
                } else {
                    setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0)
                }
                if (usingOpenSLES) {
                    setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1)
                } else {
                    setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0)
                }
                if (TextUtils.isEmpty(pixelFormat)) {
                    setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32.toLong())
                } else {
                    setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat)
                }
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1)
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0)
                //                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1)
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
                setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 10000000)
                setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1)
                setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48)
            }.apply {
                setOnPreparedListener(mPreparedListener)
                setOnVideoSizeChangedListener(mSizeChangedListener)
                setOnCompletionListener(mCompletionListener)
                setOnErrorListener(mErrorListener)
                setOnInfoListener(mInfoListener)
                setOnBufferingUpdateListener(mBufferingUpdateListener)
                setOnVideoSizeChangedListener(mVideoSizeChangedListener)
                setOnSeekCompleteListener(mSeekCompleteListener)
                mCurrentBufferPercentage = 0
                val scheme = mUri?.scheme
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (TextUtils.isEmpty(scheme) || scheme.equals("file", ignoreCase = true))) {
                    val dataSource = FileMediaDataSource(File(mUri.toString()))
                    setDataSource(dataSource)
                } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    setDataSource(mAppContext, mUri, mHeaders)
                } else {
                    dataSource = mUri.toString()
                }
                bindSurfaceHolder(mMediaPlayer, mSurfaceHolder)
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setScreenOnWhilePlaying(true)
                prepareAsync()
                mCurrentState = STATE_PREPARING
                attachMediaController()
            }
        } catch (ex: Exception) {
            mCurrentState = STATE_ERROR
            mTargetState = STATE_ERROR
            mErrorListener.onError(mMediaPlayer, IMediaPlayer.MEDIA_ERROR_UNKNOWN, 0)
            return
        } finally {
            // REMOVED: mPendingSubtitleTracks.clear();
        }
    }

    fun setMediaController(controller: IMediaController) {
        mMediaController?.hide()
        mMediaController = controller
        attachMediaController()
    }

    private fun attachMediaController() {
        if (mMediaPlayer != null) {
            mMediaController?.let {
                it.setMediaPlayer(this)
                val anchorView = if (this.parent is View)
                    this.parent as View
                else
                    this
                it.setAnchorView(anchorView)
                it.setEnabled(isInPlaybackState())
            }
        }
    }

    private val mSizeChangedListener = IMediaPlayer.OnVideoSizeChangedListener { mp, _, _, _, _ ->
        mVideoWidth = mp.videoWidth
        mVideoHeight = mp.videoHeight
        mVideoSarNum = mp.videoSarNum
        mVideoSarDen = mp.videoSarDen
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            mRenderView?.let {
                it.setVideoSize(mVideoWidth, mVideoHeight)
                it.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen)
            }
            // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
            requestLayout()
        }
    }

    private val mPreparedListener = IMediaPlayer.OnPreparedListener { mp ->
        mCurrentState = STATE_PREPARED
        // Get the capabilities of the player for this stream
        // REMOVED: Metadata
        mOnPreparedListener?.onPrepared(mMediaPlayer)
        mMediaController?.setEnabled(true)
        mVideoWidth = mp.videoWidth
        mVideoHeight = mp.videoHeight
        val seekToPosition = mSeekWhenPrepared  // mSeekWhenPrepared may be changed after seekTo() call
        if (seekToPosition != 0L) {
            seekTo(seekToPosition.toInt())
        }
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            //Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
            // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
            mRenderView?.let {
                it.setVideoSize(mVideoWidth, mVideoHeight)
                it.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen)
                if (!it.shouldWaitForResize() || mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                    // We didn't actually change the size (it was already at the size
                    // we need), so we won't get a "surface changed" callback, so
                    // start the video here instead of in the callback.
                    if (mTargetState == STATE_PLAYING) {
                        start()
                        mMediaController?.show()
                    } else if (!isPlaying && (seekToPosition != 0L || currentPosition > 0)) {
                        // Show the media controls when we're paused into a video and make 'em stick.
                        mMediaController?.show(0)
                    }
                }
            }
        } else {
            // We don't know the video size yet, but should start anyway.
            // The video size might be reported to us later.
            if (mTargetState == STATE_PLAYING) {
                start()
            }
        }
    }

    private val mCompletionListener = IMediaPlayer.OnCompletionListener {
        mCurrentState = STATE_COMPLETED
        mTargetState = STATE_COMPLETED
        mMediaController?.hide()
        mOnCompletionListener?.onCompletion(mMediaPlayer)
    }

    private val mInfoListener = IMediaPlayer.OnInfoListener { mp, what, extra ->
        mOnInfoListener?.onInfo(mp, what, extra)
        indicator?.let {
            if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                it.visibility = View.VISIBLE
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END ||
                    what == IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START ||
                    what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                it.visibility = View.GONE
            }
        }
        when (what) {
            IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED -> {
                mVideoRotationDegree = extra
                mRenderView?.setVideoRotation(extra)
            }
        }
        true
    }

    private val mErrorListener = IMediaPlayer.OnErrorListener { _, framework_err, impl_err ->
        mCurrentState = STATE_ERROR
        mTargetState = STATE_ERROR
        mMediaController?.hide()
        /* If an error handler has been supplied, use it and finish. */
        mOnErrorListener?.onError(mMediaPlayer, framework_err, impl_err)
        return@OnErrorListener true
    }

    private val mBufferingUpdateListener = IMediaPlayer.OnBufferingUpdateListener { mp, percent ->
        var p = percent
        mOnBufferingUpdateListener?.onBufferingUpdate(mp, percent)
        if (p > 95) {
            p = 100
        }
        mCurrentBufferPercentage = p
    }

    private val mVideoSizeChangedListener = IMediaPlayer.OnVideoSizeChangedListener { iMediaPlayer, i, i1, i2, i3 ->
        mOnVideoSizeChangedListener?.onVideoSizeChanged(iMediaPlayer, i, i1, i2, i3)
    }

    private val mSeekCompleteListener = IMediaPlayer.OnSeekCompleteListener { iMediaPlayer ->
        mOnSeekCompleteListener?.onSeekComplete(iMediaPlayer)
    }

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    fun setOnPreparedListener(l: IMediaPlayer.OnPreparedListener) {
        mOnPreparedListener = l
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    fun setOnCompletionListener(l: IMediaPlayer.OnCompletionListener) {
        mOnCompletionListener = l
    }

    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param l The callback that will be run
     */
    fun setOnErrorListener(l: IMediaPlayer.OnErrorListener) {
        mOnErrorListener = l
    }

    /**
     * Register a callback to be invoked when an informational event
     * occurs during playback or setup.
     *
     * @param l The callback that will be run
     */
    fun setOnInfoListener(l: IMediaPlayer.OnInfoListener) {
        mOnInfoListener = l
    }

    fun setOnBufferingUpdateListener(l: IMediaPlayer.OnBufferingUpdateListener) {
        mOnBufferingUpdateListener = l
    }

    fun setmOnVideoSizeChangedListener(mOnVideoSizeChangedListener: IMediaPlayer.OnVideoSizeChangedListener) {
        this.mOnVideoSizeChangedListener = mOnVideoSizeChangedListener
    }

    fun setOnSeekCompleteListener(l: IMediaPlayer.OnSeekCompleteListener) {
        mOnSeekCompleteListener = l
    }

    // REMOVED: mSHCallback
    private fun bindSurfaceHolder(mp: IMediaPlayer?, holder: IRenderView.ISurfaceHolder?) {
        if (mp == null)
            return
        if (holder == null) {
            mp.setDisplay(null)
            return
        }
        holder.bindToMediaPlayer(mp)
    }

    private var mSHCallback: IRenderView.IRenderCallback = object : IRenderView.IRenderCallback {
        override fun onSurfaceChanged(holder: IRenderView.ISurfaceHolder, format: Int, width: Int, height: Int) {
            if (holder.getRenderView() != mRenderView) {
                return
            }
            mSurfaceWidth = width
            mSurfaceHeight = height
            val isValidState = mTargetState == STATE_PLAYING
            mRenderView?.let {
                val hasValidSize = !it.shouldWaitForResize() || mVideoWidth == width && mVideoHeight == height
                if (mMediaPlayer != null && isValidState && hasValidSize) {
                    if (mSeekWhenPrepared != 0L) {
                        seekTo(mSeekWhenPrepared.toInt())
                    }
                    start()
                }
            }
        }

        override fun onSurfaceCreated(holder: IRenderView.ISurfaceHolder, width: Int, height: Int) {
            if (holder.getRenderView() !== mRenderView) {
                return
            }
            mSurfaceHolder = holder
            if (mMediaPlayer != null)
                bindSurfaceHolder(mMediaPlayer, holder)
            else
                openVideo()
        }

        override fun onSurfaceDestroyed(holder: IRenderView.ISurfaceHolder) {
            if (holder.getRenderView() !== mRenderView) {
                return
            }
            mSurfaceHolder = null
            releaseWithoutStop()
        }
    }

    fun releaseWithoutStop() {
        mMediaPlayer?.setDisplay(null)
    }

    /*
     * release the media player in any state
     */
    private fun release(cleartargetstate: Boolean) {
        mMediaPlayer?.let {
            it.reset()
            it.release()
        }
        mMediaPlayer = null
        mCurrentState = STATE_IDLE
        if (cleartargetstate) {
            mTargetState = STATE_IDLE
        }
        val am = mAppContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.abandonAudioFocus(null)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisiblity()
        }
        return false
    }

    override fun onTrackballEvent(ev: MotionEvent): Boolean {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisiblity()
        }
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK &&
                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                keyCode != KeyEvent.KEYCODE_VOLUME_MUTE &&
                keyCode != KeyEvent.KEYCODE_MENU &&
                keyCode != KeyEvent.KEYCODE_CALL &&
                keyCode != KeyEvent.KEYCODE_ENDCALL
        if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                mMediaPlayer?.let {
                    if (it.isPlaying) {
                        pause()
                        mMediaController?.show()
                    } else {
                        start()
                        mMediaController?.hide()
                    }
                }
                return true
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                mMediaPlayer?.let {
                    if (it.isPlaying) {
                        start()
                        mMediaController?.hide()
                    }
                }
                return true
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                mMediaPlayer?.let {
                    if (it.isPlaying) {
                        pause()
                        mMediaController?.show()
                    }
                }
                return true
            } else {
                toggleMediaControlsVisiblity()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun toggleMediaControlsVisiblity() {
        mMediaController?.let {
            if (it.isShowing()) {
                it.hide()
            } else {
                it.show()
            }
        }
    }

    override fun start() {
        if (isInPlaybackState()) {
            mMediaPlayer?.start()
            mCurrentState = STATE_PLAYING
        }
        mTargetState = STATE_PLAYING
    }

    override fun pause() {
        if (isInPlaybackState()) {
            mMediaPlayer?.let {
                it.pause()
                mCurrentState = STATE_PAUSED
            }
        }
        mTargetState = STATE_PAUSED
        indicator?.visibility = View.GONE
    }

    override fun getDuration(): Int {
        mMediaPlayer?.let {
            return it.duration.toInt()
        }
        return -1
    }

    override fun getCurrentPosition(): Int {
        mMediaPlayer?.let {
            if (isInPlaybackState())
                return it.currentPosition.toInt()
        }
        return 0
    }

    override fun seekTo(msec: Int) {
        mSeekWhenPrepared = if (isInPlaybackState()) {
            mMediaPlayer?.seekTo(msec.toLong())
            0
        } else {
            msec.toLong()
        }
    }

    override fun isPlaying(): Boolean {
        mMediaPlayer?.let {
            return isInPlaybackState() && it.isPlaying
        }
        return false
    }

    override fun getBufferPercentage(): Int {
        return if (mMediaPlayer != null) {
            mCurrentBufferPercentage
        } else 0
    }

    private fun isInPlaybackState(): Boolean {
        return mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING
    }

    fun getCurrentState(): Int {
        return mCurrentState
    }

    override fun canPause(): Boolean {
        return mCanPause
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getAudioSessionId(): Int {
        return 0
    }

    // REMOVED: getAudioSessionId();
    // REMOVED: onAttachedToWindow();
    // REMOVED: onDetachedFromWindow();
    // REMOVED: onLayout();
    // REMOVED: draw();
    // REMOVED: measureAndLayoutSubtitleWidget();
    // REMOVED: setSubtitleWidget();
    // REMOVED: getSubtitleLooper();
    //-------------------------
    // Extend: Aspect Ratio
    //-------------------------
    private val s_allAspectRatio = intArrayOf(IRenderView.AR_ASPECT_FIT_PARENT, IRenderView.AR_ASPECT_FILL_PARENT, IRenderView.AR_ASPECT_WRAP_CONTENT, IRenderView.AR_MATCH_PARENT, IRenderView.AR_16_9_FIT_PARENT, IRenderView.AR_4_3_FIT_PARENT)
    private var mCurrentAspectRatioIndex = 0
    private var mCurrentAspectRatio = s_allAspectRatio[mCurrentAspectRatioIndex]

    fun toggleAspectRatio(): Int {
        mCurrentAspectRatioIndex++
        mCurrentAspectRatioIndex %= s_allAspectRatio.size
        mCurrentAspectRatio = s_allAspectRatio[mCurrentAspectRatioIndex]
        mRenderView?.setAspectRatio(mCurrentAspectRatio)
        return mCurrentAspectRatio
    }

    private val mAllRenders = ArrayList<Int>()
    private var mCurrentRenderIndex = 0
    private var mCurrentRender = RENDER_NONE

    private fun initRenders() {
        mAllRenders.clear()
        if (enableSurfaceView) {
            mAllRenders.add(RENDER_SURFACE_VIEW)
        } else if (enableTextureView && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mAllRenders.add(RENDER_TEXTURE_VIEW)
        } else if (enableNoView) {
            mAllRenders.add(RENDER_NONE)
        }
        if (mAllRenders.isEmpty())
            mAllRenders.add(RENDER_SURFACE_VIEW)
        mCurrentRender = mAllRenders[mCurrentRenderIndex]
        setRender(mCurrentRender)
    }

    fun toggleRender(): Int {
        mCurrentRenderIndex++
        mCurrentRenderIndex %= mAllRenders.size
        mCurrentRender = mAllRenders[mCurrentRenderIndex]
        setRender(mCurrentRender)
        return mCurrentRender
    }

    fun setAspectRatio(aspectRatio: Int) {
        for (i in s_allAspectRatio.indices) {
            if (s_allAspectRatio[i] == aspectRatio) {
                mCurrentAspectRatioIndex = i
                mRenderView?.setAspectRatio(mCurrentAspectRatio)
                break
            }
        }
    }

    fun stopPlayback() {
        mMediaPlayer?.let {
            it.stop()
            it.release()
        }
        mMediaPlayer = null
        mCurrentState = STATE_IDLE
        mTargetState = STATE_IDLE
        val am = mAppContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.abandonAudioFocus(null)
    }

    fun getTcpSpeed(): Long {
        return if (mMediaPlayer != null) (mMediaPlayer as IjkMediaPlayer).tcpSpeed else 0
    }
}
package tv.danmaku.ijk.media.widget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.TextureView
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import tv.danmaku.ijk.media.IRenderView
import tv.danmaku.ijk.media.MeasureHelper
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.ISurfaceTextureHolder
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class TextureRenderView : TextureView, IRenderView {
    private lateinit var mMeasureHelper: MeasureHelper
    private var mSurfaceCallback: SurfaceCallback? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private fun initView() {
        mMeasureHelper = MeasureHelper(this)
        mSurfaceCallback = SurfaceCallback(this)
        surfaceTextureListener = mSurfaceCallback
    }

    override fun getView(): View {
        return this
    }

    override fun shouldWaitForResize(): Boolean {
        return false
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight)
            requestLayout()
        }
    }

    override fun setVideoSampleAspectRatio(videoSarNum: Int, videoSarDen: Int) {
        if (videoSarNum > 0 && videoSarDen > 0) {
            mMeasureHelper.setVideoSampleAspectRatio(videoSarNum, videoSarDen)
            requestLayout()
        }
    }

    override fun setVideoRotation(degree: Int) {
        mMeasureHelper.setVideoRotation(degree)
        rotation = degree.toFloat()
    }

    override fun setAspectRatio(aspectRatio: Int) {
        mMeasureHelper.setAspectRatio(aspectRatio)
        requestLayout()
    }

    override fun addRenderCallback(callback: IRenderView.IRenderCallback) {
        mSurfaceCallback?.addRenderCallback(callback)
    }

    override fun removeRenderCallback(callback: IRenderView.IRenderCallback) {
        mSurfaceCallback?.removeRenderCallback(callback)
    }

    fun getSurfaceHolder(): IRenderView.ISurfaceHolder {
        return InternalSurfaceHolder(this, mSurfaceCallback?.mSurfaceTexture)
    }

    private inner class InternalSurfaceHolder() : IRenderView.ISurfaceHolder {
        private var mTextureView: TextureRenderView? = null
        private var mSurfaceTexture: SurfaceTexture? = null

        constructor(textureView: TextureRenderView?, surfaceTexture: SurfaceTexture?) : this() {
            mTextureView = textureView
            mSurfaceTexture = surfaceTexture
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun bindToMediaPlayer(mp: IMediaPlayer?) {
            mp?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && it is ISurfaceTextureHolder) {
                    val textureHolder = it as ISurfaceTextureHolder
                    mTextureView?.mSurfaceCallback?.setOwnSurfaceTecture(false)
                    val surfaceTexture = textureHolder.surfaceTexture
                    if (surfaceTexture != null) {
                        mTextureView?.surfaceTexture = surfaceTexture
                    } else {
                        textureHolder.surfaceTexture = mSurfaceTexture
                    }
                } else {
                    it.setSurface(openSurface())
                }
            }
        }

        override fun getRenderView(): IRenderView? {
            return mTextureView
        }

        override fun getSurfaceHolder(): SurfaceHolder? {
            return null
        }

        override fun openSurface(): Surface? {
            return if (mSurfaceTexture == null) null else Surface(mSurfaceTexture)
        }

        override fun getSurfaceTexture(): SurfaceTexture? {
            return mSurfaceTexture
        }

    }

    private inner class SurfaceCallback() : TextureView.SurfaceTextureListener {
        var mSurfaceTexture: SurfaceTexture? = null
        private var mIsFormatChanged: Boolean = false
        private var mWidth: Int = 0
        private var mHeight: Int = 0

        private var mOwnSurfaceTecture = true

        private var mWeakRenderView: WeakReference<TextureRenderView>? = null
        private val mRenderCallbackMap = ConcurrentHashMap<IRenderView.IRenderCallback, Any>()

        constructor(renderView: TextureRenderView) : this() {
            mWeakRenderView = WeakReference(renderView)
        }

        fun setOwnSurfaceTecture(ownSurfaceTecture: Boolean) {
            mOwnSurfaceTecture = ownSurfaceTecture
        }

        fun addRenderCallback(callback: IRenderView.IRenderCallback) {
            mRenderCallbackMap[callback] = callback
            var surfaceHolder: IRenderView.ISurfaceHolder? = null
            if (mSurfaceTexture != null) {
                if (surfaceHolder == null)
                    surfaceHolder = InternalSurfaceHolder(mWeakRenderView?.get(), mSurfaceTexture)
                callback.onSurfaceCreated(surfaceHolder, mWidth, mHeight)
            }
            if (mIsFormatChanged) {
                if (surfaceHolder == null)
                    surfaceHolder = InternalSurfaceHolder(mWeakRenderView?.get(), mSurfaceTexture)
                callback.onSurfaceChanged(surfaceHolder, 0, mWidth, mHeight)
            }
        }

        fun removeRenderCallback(callback: IRenderView.IRenderCallback) {
            mRenderCallbackMap.remove(callback)
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            mSurfaceTexture = surface
            mIsFormatChanged = false
            mWidth = 0
            mHeight = 0
            val surfaceHolder = InternalSurfaceHolder(mWeakRenderView?.get(), surface)
            for (renderCallback in mRenderCallbackMap.keys) {
                renderCallback.onSurfaceCreated(surfaceHolder, 0, 0)
            }
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
            mSurfaceTexture = surface
            mIsFormatChanged = true
            mWidth = width
            mHeight = height
            val surfaceHolder = InternalSurfaceHolder(mWeakRenderView?.get(), surface)
            for (renderCallback in mRenderCallbackMap.keys) {
                renderCallback.onSurfaceChanged(surfaceHolder, 0, width, height)
            }
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            mSurfaceTexture = surface
            mIsFormatChanged = false
            mWidth = 0
            mHeight = 0
            val surfaceHolder = InternalSurfaceHolder(mWeakRenderView?.get(), surface)
            for (renderCallback in mRenderCallbackMap.keys) {
                renderCallback.onSurfaceDestroyed(surfaceHolder)
            }
            return mOwnSurfaceTecture
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight())
    }

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.className = TextureRenderView::class.java.name
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.className = TextureRenderView::class.java.name
    }
}
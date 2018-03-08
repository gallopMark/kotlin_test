package tv.danmaku.ijk.media.widget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import tv.danmaku.ijk.media.IRenderView
import tv.danmaku.ijk.media.MeasureHelper
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.ISurfaceTextureHolder
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

@Suppress("DEPRECATION")
class SurfaceRenderView : SurfaceView, IRenderView {

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
        holder.addCallback(mSurfaceCallback)
        //noinspection deprecation
        holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL)
    }

    override fun getView(): View {
        return this
    }

    override fun shouldWaitForResize(): Boolean {
        return true
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight)
            holder.setFixedSize(videoWidth, videoHeight)
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

    private inner class InternalSurfaceHolder() : IRenderView.ISurfaceHolder {
        private var mSurfaceView: SurfaceRenderView? = null
        private var mSurfaceHolder: SurfaceHolder? = null

        constructor(surfaceView: SurfaceRenderView?, surfaceHolder: SurfaceHolder?) : this() {
            mSurfaceView = surfaceView
            mSurfaceHolder = surfaceHolder
        }

        override fun getRenderView(): IRenderView? {
            return mSurfaceView
        }

        override fun getSurfaceHolder(): SurfaceHolder? {
            return mSurfaceHolder
        }

        override fun getSurfaceTexture(): SurfaceTexture? {
            return null
        }

        override fun bindToMediaPlayer(mp: IMediaPlayer?) {
            mp?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && it is ISurfaceTextureHolder) {
                    val textureHolder = mp as ISurfaceTextureHolder
                    textureHolder.surfaceTexture = null
                }
                it.setDisplay(mSurfaceHolder)
            }
        }

        override fun openSurface(): Surface? {
            return mSurfaceHolder?.surface
        }
    }

    private inner class SurfaceCallback() : SurfaceHolder.Callback {
        private var mSurfaceHolder: SurfaceHolder? = null
        private var mIsFormatChanged: Boolean = false
        private var mFormat: Int = 0
        private var mWidth: Int = 0
        private var mHeight: Int = 0
        private var mWeakSurfaceView: WeakReference<SurfaceRenderView>? = null
        private val mRenderCallbackMap = ConcurrentHashMap<IRenderView.IRenderCallback, Any>()

        constructor(surfaceView: SurfaceRenderView) : this() {
            mWeakSurfaceView = WeakReference(surfaceView)
        }

        fun addRenderCallback(callback: IRenderView.IRenderCallback) {
            mRenderCallbackMap[callback] = callback
            var surfaceHolder: IRenderView.ISurfaceHolder? = null
            if (mSurfaceHolder == null) {
                surfaceHolder = InternalSurfaceHolder(mWeakSurfaceView?.get(), mSurfaceHolder)
                callback.onSurfaceCreated(surfaceHolder, mWidth, mHeight)
            }
            if (mIsFormatChanged) {
                if (surfaceHolder == null)
                    surfaceHolder = InternalSurfaceHolder(mWeakSurfaceView?.get(), mSurfaceHolder)
                callback.onSurfaceChanged(surfaceHolder, mFormat, mWidth, mHeight)
            }
        }

        fun removeRenderCallback(callback: IRenderView.IRenderCallback) {
            mRenderCallbackMap.remove(callback)
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            mSurfaceHolder = holder
            mIsFormatChanged = false
            mFormat = 0
            mWidth = 0
            mHeight = 0
            val surfaceHolder = InternalSurfaceHolder(mWeakSurfaceView?.get(), mSurfaceHolder)
            for (renderCallback in mRenderCallbackMap.keys) {
                renderCallback.onSurfaceCreated(surfaceHolder, 0, 0)
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            mSurfaceHolder = holder
            mIsFormatChanged = true
            mFormat = format
            mWidth = width
            mHeight = height
            val surfaceHolder = InternalSurfaceHolder(mWeakSurfaceView?.get(), mSurfaceHolder)
            for (renderCallback in mRenderCallbackMap.keys) {
                renderCallback.onSurfaceChanged(surfaceHolder, format, width, height)
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            mSurfaceHolder = null
            mIsFormatChanged = false
            mFormat = 0
            mWidth = 0
            mHeight = 0
            val surfaceHolder = InternalSurfaceHolder(mWeakSurfaceView?.get(), mSurfaceHolder)
            for (renderCallback in mRenderCallbackMap.keys) {
                renderCallback.onSurfaceDestroyed(surfaceHolder)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight())
    }

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.className = SurfaceRenderView::class.java.name
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            info.className = SurfaceRenderView::class.java.name
        }
    }
}
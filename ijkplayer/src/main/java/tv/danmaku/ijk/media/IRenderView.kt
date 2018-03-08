package tv.danmaku.ijk.media

import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * 创建日期：2018/1/10.
 * 描述:视频渲染view
 * 作者:xiaoma
 */
interface IRenderView {
    companion object {
        const val AR_ASPECT_FIT_PARENT = 0 // without clip
        const val AR_ASPECT_FILL_PARENT = 1 // may clip
        const val AR_ASPECT_WRAP_CONTENT = 2
        const val AR_MATCH_PARENT = 3
        const val AR_16_9_FIT_PARENT = 4
        const val AR_4_3_FIT_PARENT = 5
    }

    /**获取外层界面 */
    fun getView(): View

    /**
     * 是否需要等待重置大小
     */
    fun shouldWaitForResize(): Boolean

    /**
     * 设置视频界面大小
     */
    fun setVideoSize(videoWidth: Int, videoHeight: Int)

    /**
     * 设置视频裁剪方式
     */
    fun setVideoSampleAspectRatio(videoSarNum: Int, videoSarDen: Int)

    /**
     * 设置视频旋转角度
     */
    fun setVideoRotation(degree: Int)

    /**
     * 设置视频裁剪方式
     */
    fun setAspectRatio(aspectRatio: Int)

    /**
     * 添加视频渲染回调
     */
    fun addRenderCallback(callback: IRenderCallback)

    /**
     * 移除视频渲染回调
     */
    fun removeRenderCallback(callback: IRenderCallback)

    interface ISurfaceHolder {
        /**
         * surface界面绑定到mediaplay上
         */
        fun bindToMediaPlayer(mp: IMediaPlayer?)

        /**
         * 获取渲染的view
         */
        fun getRenderView(): IRenderView?

        /**
         * 获取渲染使用的具体view surface
         */
        fun getSurfaceHolder(): SurfaceHolder?

        /**
         * 打开surface界面
         */
        fun openSurface(): Surface?

        /**
         * 获取渲染使用的具体view texture
         */
        fun getSurfaceTexture(): SurfaceTexture?
    }

    interface IRenderCallback {
        /**
         * 创建surface界面大小
         *
         * @param holder
         * @param width  could be 0
         * @param height could be 0
         */
        fun onSurfaceCreated(holder: ISurfaceHolder, width: Int, height: Int)

        /**
         * surface界面大小改变监听
         *
         * @param holder
         * @param format could be 0
         * @param width
         * @param height
         */
        fun onSurfaceChanged(holder: ISurfaceHolder, format: Int, width: Int, height: Int)

        /**
         * 界面回收
         */
        fun onSurfaceDestroyed(holder: ISurfaceHolder)
    }

}

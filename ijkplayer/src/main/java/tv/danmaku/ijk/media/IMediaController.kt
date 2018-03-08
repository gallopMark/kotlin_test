package tv.danmaku.ijk.media

import android.view.View
import android.widget.MediaController

interface IMediaController {
    /**
     * 隐藏标题栏
     */
    fun hide()

    /**
     * 判断是否显示
     */
    fun isShowing(): Boolean

    /**
     * 设置主播界面
     */
    fun setAnchorView(view: View)

    /**
     * 设置是否可用
     */
    fun setEnabled(enabled: Boolean)

    /**
     * 设置媒体播放器
     */
    fun setMediaPlayer(player: MediaController.MediaPlayerControl)

    /**
     * 显示带超时时间
     */
    fun show(timeout: Int)

    /**
     * 显示标题栏
     */
    fun show()

    //----------
    // Extends
    //----------
    fun showOnce(view: View)
}
package com.haoyuinfo.library.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.haoyuinfo.library.R
import com.haoyuinfo.library.dialog.MaterialDialog
import com.haoyuinfo.library.dialog.PromptDialog
import com.haoyuinfo.library.widget.CompatToast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/* viewpager结合fragment使用 使用懒加载处理，避免重复加载数据*/
abstract class BasePageFragment : Fragment() {
    private val TAG: String = javaClass.name
    lateinit var context: Activity
    private var rootView: View? = null
    private var isFragmentVisible: Boolean = false
    private var isFirstVisible: Boolean = false
    private var isViewInited: Boolean = false
    private val rxDisposables = CompositeDisposable()
    private var promptDialog: PromptDialog? = null
    private val mToast: CompatToast? = null
    private var comPatDialog: MaterialDialog? = null
    //setUserVisibleHint()在Fragment创建时会先被调用一次，传入isVisibleToUser = false
    //如果当前Fragment可见，那么setUserVisibleHint()会再次被调用一次，传入isVisibleToUser = true
    //如果Fragment从可见->不可见，那么setUserVisibleHint()也会被调用，传入isVisibleToUser = false
    //总结：setUserVisibleHint()除了Fragment的可见状态发生变化时会被回调外，在new Fragment()时也会被回调
    //如果我们需要在 Fragment 可见与不可见时干点事，用这个的话就会有多余的回调了，那么就需要重新封装一个
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.d(TAG, "setUserVisibleHint...")
        if (rootView == null || !isViewInited) return
        if (isFirstVisible && isVisibleToUser) {
            initData()
            isFirstVisible = false
        }
        if (isVisibleToUser) {
            onFragmentVisibleChange(true)
            isFragmentVisible = true
            return
        }
        if (isFragmentVisible) {
            isFragmentVisible = false
            onFragmentVisibleChange(false)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.d(TAG, "onAttach...")
        this.context = context as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate...")
        isFirstVisible = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView...")
        rootView = rootView ?: inflater.inflate(setLayoutResID(), container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated...")
        if (!isViewInited) {
            setUp(view)
            setListener()
            isViewInited = true
        }
        if (userVisibleHint) {
            if (isFirstVisible) {
                initData()
                isFirstVisible = false
            }
            onFragmentVisibleChange(true)
            isFragmentVisible = true
        }
    }

    abstract fun setLayoutResID(): Int

    open fun setUp(view: View) {}
    open fun setListener() {}
    /**
     * 在fragment首次可见时回调，可在这里进行加载数据，保证只在第一次打开Fragment时才会加载数据，
     * 这样就可以防止每次进入都重复加载数据
     * 该方法会在 onFragmentVisibleChange() 之前调用，所以第一次打开时，可以用一个全局变量表示数据下载状态，
     * 然后在该方法内将状态设置为下载状态，接着去执行下载的任务
     * 最后在 onFragmentVisibleChange() 里根据数据下载状态来控制下载进度ui控件的显示与隐藏
     */
    open fun initData() {}

    /**
     * 去除setUserVisibleHint()多余的回调场景，保证只有当fragment可见状态发生变化时才回调
     * 回调时机在view创建完后，所以支持ui操作，解决在setUserVisibleHint()里进行ui操作有可能报null异常的问题
     * 可在该回调方法里进行一些ui显示与隐藏，比如加载框的显示和隐藏
     * @param isVisible true  不可见 -> 可见
     * false 可见  -> 不可见
     */
    open fun onFragmentVisibleChange(isVisible: Boolean) {}

    fun toast(text: CharSequence) {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_compat_toast, FrameLayout(context))
        val textView = v.findViewById<TextView>(R.id.tv_text)
        textView.text = text
        fromToast().apply { view = v }.show()
    }

    private fun fromToast(): CompatToast {
        return mToast
                ?: CompatToast(context, R.style.CompatToast).apply { duration = Toast.LENGTH_LONG }
    }

    open fun showDialog() {
        hideDialog()
        promptDialog = PromptDialog(context).apply { show() }
    }

    open fun hideDialog() {
        promptDialog?.dismiss()
        promptDialog = null
    }

    open fun showCompatDialog(title: CharSequence, message: CharSequence) {
        comPatDialog?.dismiss()
        comPatDialog = MaterialDialog(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("确定", null)
            show()
        }
    }

    open fun addDisposable(d: Disposable?) {
        d?.let { rxDisposables.add(it) }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart...")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume...")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause...")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop...")
    }

    override fun onDestroyView() {
        rootView?.parent?.let { (it as ViewGroup).removeView(rootView) }
        super.onDestroyView()
        Log.d(TAG, "onDestroyView...")
    }

    override fun onDestroy() {
        super.onDestroy()
        mToast?.cancel()
        rxDisposables.dispose()
        Log.d(TAG, "onDestroy...")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach...")
    }
}
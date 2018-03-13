package com.haoyuinfo.app.activity

import android.os.Bundle
import android.widget.Toast
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.haoyuinfo.app.R
import com.haoyuinfo.library.base.BaseActivity
import kotlinx.android.synthetic.main.activity_amapmapview.*


class AmapActivity : BaseActivity(), LocationSource, AMapLocationListener {
    //定位需要的声明
    private var mLocationClient: AMapLocationClient? = null//定位发起端
    private var mLocationOption: AMapLocationClientOption? = null//定位参数
    private var mListener: LocationSource.OnLocationChangedListener? = null//定位监听器
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private var isFirstLoc = true

    override fun setLayoutResID(): Int {
        return R.layout.activity_amapmapview
    }

    override fun setUp(savedInstanceState: Bundle?) {
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState)
        //修改默认位置(广州市天河区)
        mapView.map.moveCamera(CameraUpdateFactory.changeLatLng(LatLng(113.373047, 23.125149)))
        mapView.map.uiSettings.setLogoBottomMargin(-50)//隐藏logo
        //设置定位监听
        mapView.map.setLocationSource(this)
        // 是否显示定位按钮
        mapView.map.uiSettings.isMyLocationButtonEnabled = true
        // 是否可触发定位并显示定位层
        mapView.map.isMyLocationEnabled = true
        location()
        mapView.map.setOnMapClickListener {
            it?.let {
                mapView.map.moveCamera(CameraUpdateFactory.zoomTo(17f))
                mapView.map.moveCamera(CameraUpdateFactory.changeLatLng(it))
            }
        }
    }

    private fun location() {
        mLocationClient = AMapLocationClient(this).apply {
            //设置定位回调监听
            setLocationListener(this@AmapActivity)
            //初始化定位参数
            mLocationOption = AMapLocationClientOption().apply {
                //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
                locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
                //设置是否返回地址信息（默认返回地址信息）
                isNeedAddress = true
                //设置是否只定位一次,默认为false
                isOnceLocation = false
                //设置是否强制刷新WIFI，默认为强制刷新
                isWifiScan = false
                isMockEnable = false
                interval = 2000 //设置定位间隔,单位毫秒,默认为2000ms
                //给定位客户端对象设置定位参数
            }
            setLocationOption(mLocationOption)
            //启动定位
            startLocation()
        }
    }

    override fun deactivate() {
        mListener = null
        mLocationClient?.let {
            it.stopLocation()
            it.onDestroy()
        }
        mLocationClient = null
    }

    override fun activate(listener: LocationSource.OnLocationChangedListener?) {
        this.mListener = listener
    }

    override fun onLocationChanged(amapLocation: AMapLocation?) {
        amapLocation?.let {
            if (it.errorCode == 0) {
                if (isFirstLoc) {
                    mapView.map.moveCamera(CameraUpdateFactory.zoomTo(17f))
                    mapView.map.moveCamera(CameraUpdateFactory.changeLatLng(LatLng(it.latitude, it.longitude)))
                    mListener?.onLocationChanged(it)
                    //添加图钉
                    mapView.map.addMarker(getMarkerOptions(it))
                    isFirstLoc = false
                }
            } else {
                Toast.makeText(this, "定位失败", Toast.LENGTH_LONG).show()
            }
        }
    }

    //自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
    private fun getMarkerOptions(amapLocation: AMapLocation): MarkerOptions {
        //设置图钉选项
        val options = MarkerOptions()
        //图标
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
        //位置
        options.position(LatLng(amapLocation.latitude, amapLocation.longitude))
        val buffer = StringBuffer()
        buffer.append(amapLocation.country + "" + amapLocation.province + "" + amapLocation.city + "" + amapLocation.district + "" + amapLocation.street + "" + amapLocation.streetNum)
        //标题
        options.title("当前位置")
        options.snippet(buffer.toString())
        options.draggable(true) //设置Marker可拖动
        options.isFlat = true //设置marker平贴地图效果
        return options
    }

    override fun onResume() {
        super.onResume()
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    override fun onStop() {
        super.onStop()
        mLocationClient?.stopLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy()
        mLocationClient?.onDestroy()
    }
}
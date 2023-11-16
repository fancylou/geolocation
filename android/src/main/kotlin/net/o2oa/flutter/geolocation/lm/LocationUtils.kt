package net.o2oa.flutter.geolocation.lm

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import java.util.Locale


/**
 * Created by fancyLou on 2023-11-13.
 * Copyright © 2023 android. All rights reserved.
 */
class LocationUtils private constructor(context: Context) {

    private val TAG = "LocationUtils"


    private val mContext: Context
    private var addressCallback: AddressCallback? = null
    private var locationManager: LocationManager? = null
    private var singleLocation = false
    private var gpsLocationListener: LocationListener = object : LocationListener {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        override fun onStatusChanged(provider: String, status: Int, arg2: Bundle) {}

        // Provider被enable时触发此函数，比如GPS被打开
        override fun onProviderEnabled(provider: String) {}

        // Provider被disable时触发此函数，比如GPS被关闭
        override fun onProviderDisabled(provider: String) {}

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        override fun onLocationChanged(loc: Location) {
            Log.d(TAG, "==onLocationChanged=  gpsLocationListener    =")
            location = loc
            callBackLocation()
            if (singleLocation) {
                endLocation() // 结束定位
            }

        }
    }

    private var networkLocationListener: LocationListener = object : LocationListener {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        override fun onStatusChanged(provider: String, status: Int, arg2: Bundle) {}

        // Provider被enable时触发此函数，比如GPS被打开
        override fun onProviderEnabled(provider: String) {}

        // Provider被disable时触发此函数，比如GPS被关闭
        override fun onProviderDisabled(provider: String) {}

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        override fun onLocationChanged(loc: Location) {
            Log.d(TAG, "==onLocationChanged= networkLocationListener   =")
            location = loc
            callBackLocation()
            if (singleLocation) {
                endLocation() // 结束定位
            }
        }
    }


    /**
     * 获取最有一次定位信息
     * 有可能为空
     */
    fun onGetLastKnownLocation(addressCallback: AddressCallback) {
        Log.d(TAG, "onGetLastKnownLocation 开始")
        this.addressCallback = addressCallback
        locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location == null) {
             location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        callBackLocation()
        endLocation()
    }

    /**
     * 单次定位
     * 开启定位获取到第一次结果后结束定位
     */
    fun onGetCurrentPosition(addressCallback: AddressCallback) {
        Log.d(TAG, "onGetCurrentPosition 开始")
        this.singleLocation = true
        this.addressCallback = addressCallback
        beginSeriesLocation()
    }


    /**
     * 开始定位
     */
    fun startLocation(addressCallback: AddressCallback) {
        Log.d(TAG, "startLocation 开始")
        this.singleLocation = false
        this.addressCallback = addressCallback
        beginSeriesLocation()
    }

    /**
     * 结束定位
     */
    fun endLocation() {
        Log.d(TAG, "endLocation 开始")
        if (locationManager != null  ) {
            locationManager!!.removeUpdates(gpsLocationListener)
            locationManager!!.removeUpdates(networkLocationListener)
            locationManager = null
        }
        if (addressCallback != null) {
            addressCallback = null
        }
    }



    /**
     * 连续定位
     */
    private fun beginSeriesLocation() {
        locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        Log.d(TAG, "PROVIDER ${LocationManager.NETWORK_PROVIDER} ====================")
        // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistance
        //LocationManager 每隔 5 秒钟会检测一下位置的变化情况，当移动距离超过 10 米的时候，
        // 就会调用 LocationListener 的 onLocationChanged() 方法，并把新的位置信息作为参数传入。
        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0f, networkLocationListener)
        Log.d(TAG, "PROVIDER ${LocationManager.GPS_PROVIDER} ====================")
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, gpsLocationListener)
    }


//
//    private fun getLocationManager(): String? {
//        //1.获取位置管理器
//        locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        //2.获取位置提供器，GPS或是NetWork
//        // 查找到服务信息
//        val criteria = Criteria()
//        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
//        criteria.accuracy = Criteria.ACCURACY_FINE
//        // 设置是否允许运营商收费
//        criteria.isCostAllowed = true
//        // 设置对电源的需求
//        criteria.powerRequirement = Criteria.POWER_LOW // 低功耗
//        // 为获取地理位置信息时设置查询条件
//        val list = locationManager!!.getProviders(criteria, true)
//        Log.d(TAG, "======providers "+list.joinToString(","))
//        return locationManager!!.getBestProvider(criteria, true) // 获取GPS信息
//    }



    // 返回定位结果
    private fun callBackLocation() {
        if (location == null) {
            Log.e(TAG, "没有获取到定位信息！！！！！！")
            if (addressCallback != null) {
                addressCallback!!.onGetLocation(null, null, null)
            }
        } else {
            val latitude = location!!.latitude //纬度
            val longitude = location!!.longitude //经度
            Log.d(TAG, "定位信息 latitude $latitude， longitude $longitude")
            getAddress(latitude, longitude)
        }
    }

    // 根据定位经纬度 获取地址信息
    private fun getAddress(latitude: Double, longitude: Double) {
        //Geocoder通过经纬度获取具体信息
        val gc = Geocoder(mContext, Locale.getDefault())
        try {
            val locationList = gc.getFromLocation(latitude, longitude, 1)
            if (locationList != null) {
                val address = locationList[0]
                val countryName = address.countryName //国家
                val countryCode = address.countryCode
                val adminArea = address.adminArea //省
                val locality = address.locality //市
                val subLocality = address.subLocality //区
                val featureName = address.featureName //街道
                var i = 0
                while (address.getAddressLine(i) != null) {
                    val addressLine = address.getAddressLine(i)
                    //街道名称:广东省深圳市罗湖区蔡屋围一街深圳瑞吉酒店
                    Log.d(TAG, "addressLine=====$addressLine")
                    i++
                }
                Log.d(TAG, "countryName $countryName countryCode $countryCode adminArea $adminArea locality $locality subLocality $subLocality featureName $featureName")
                if (addressCallback != null) {
                    addressCallback!!.onGetLocation(latitude, longitude, address)
                }
            } else {
                Log.d(TAG, "latitude $latitude longitude $longitude  没有地址信息")
                if (addressCallback != null) {
                    addressCallback!!.onGetLocation(latitude, longitude, null)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "", e)
        }
    }

    init {
        mContext = context
    }

    interface AddressCallback {
        fun onGetLocation(lat: Double?, lng: Double?, address: Address?)
        fun onFail(msg: String?)
    }

    companion object {
        @Volatile
        private var uniqueInstance: LocationUtils? = null
        private var location: Location? = null

        //采用Double CheckLock(DCL)实现单例
        fun getInstance(context: Context): LocationUtils? {
            if (uniqueInstance == null) {
                synchronized(LocationUtils::class.java) {
                    if (uniqueInstance == null) {
                        uniqueInstance = LocationUtils(context)
                    }
                }
            }
            return uniqueInstance
        }
    }
}
package net.o2oa.flutter.geolocation

import android.app.Activity
import android.content.Context
import android.location.Address
import android.util.Log
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import net.o2oa.flutter.geolocation.lm.LocationUtils


/**
 * Created by fancyLou on 2023-11-14.
 * Copyright © 2023 android. All rights reserved.
 */
class MethodChannelImpl :  MethodChannel.MethodCallHandler  {

    private val TAG = "MethodChannelImpl"
    private val channelName = "net.o2oa.flutter.geolocation/method"

    private var context: Context? = null
    private var channel: MethodChannel? = null
    private var activity: Activity? = null
    private var service: GeoLocationService? = null


    /**
     * 开始绑定 methodChannel
     */
    fun startListening(context: Context, messenger: BinaryMessenger) {
        if (channel != null) {
            Log.w(TAG, "Setting a method call handler before the last was disposed.")
            stopListening()
        }
        channel = MethodChannel(messenger, channelName)
        channel?.setMethodCallHandler(this)
        this.context = context
    }

    /**
     * 结束绑定
     */
    fun stopListening() {
        if (channel == null) {
            Log.d(TAG, "Tried to stop listening when no MethodChannel had been initialized.")
            return
        }
        channel?.setMethodCallHandler(null)
        channel = null
    }

    fun setActivity( activity: Activity?) {
        this.activity = activity
    }
    fun setService(service: GeoLocationService?) {
        this.service = service
    }


    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        Log.d(TAG, "Geolocation onMethodCall ${call.method}")
        when(call.method) {
            "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
            "getLastKnownPosition" -> onGetLastKnownPosition(result)
            "getCurrentPosition" -> onGetCurrentPosition(result)

            else -> result.notImplemented()
        }
    }



    /**
     * 获取最后定位信息
     */
    private fun onGetLastKnownPosition(result: MethodChannel.Result) {
        service?.onGetLastKnownPosition(object :
            LocationUtils.AddressCallback {
            override fun onGetLocation(lat: Double?, lng: Double?, address: Address?) {
                Log.d(TAG, " 定位地址 lat：$lat lng：$lng address: $address ")
                var ad = address?.featureName
                val line0 = address?.getAddressLine(0)
                if (line0 != null) {
                    ad = line0
                }
                val position = GeoPosition(lat, lng, ad)
                result.success(position.toHashMap())
            }

            override fun onFail(msg: String?) {
                Log.e(TAG, "错误信息 $msg")
                result.error("NULL", msg, null)
            }
        })
    }

    /**
     * 获取当前定位信息
     */
    private fun onGetCurrentPosition( result: MethodChannel.Result) {
        service?.onGetCurrentPosition(object :
            LocationUtils.AddressCallback {
            override fun onGetLocation(lat: Double?, lng: Double?, address: Address?) {
                Log.d(TAG, " 定位地址 lat：$lat lng：$lng address: $address ")
                val position = GeoPosition(lat, lng, address?.featureName)
                result.success(position.toHashMap())
            }

            override fun onFail(msg: String?) {
                Log.e(TAG, "错误信息 $msg")
                result.error("NULL", msg, null)
            }
        })
    }


}
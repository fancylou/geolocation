package net.o2oa.flutter.geolocation

import android.app.Activity
import android.content.Context
import android.location.Address
import android.util.Log
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import net.o2oa.flutter.geolocation.lm.LocationUtils

/**
 * Created by fancyLou on 2023-11-14.
 * Copyright © 2023 android. All rights reserved.
 */
class EventChannelImpl: EventChannel.StreamHandler {

    private val TAG = "EventChannelImpl"
    private val channelName = "net.o2oa.flutter.geolocation/event"


    private var eventChannel: EventChannel? = null
    private var eventSink: EventChannel.EventSink? = null
    private var context: Context? = null
    private var activity: Activity? = null
    private var service: GeoLocationService? = null

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        this.eventSink = events
        service?.startLocation(object :
            LocationUtils.AddressCallback {
            override fun onGetLocation(lat: Double?, lng: Double?, address: Address?) {
                Log.d(TAG, " 定位地址 lat：$lat lng：$lng address: $address ")
                var ad = address?.featureName
                val line0 = address?.getAddressLine(0)
                if (line0 != null) {
                    ad = line0
                }
                val position = GeoPosition(lat, lng, ad)
                eventSink?.success(position.toHashMap())
            }

            override fun onFail(msg: String?) {
                Log.e(TAG, "错误信息 $msg")
                eventSink?.error("NULL", msg, null)
            }
        })
    }

    override fun onCancel(arguments: Any?) {
        this.eventSink = null
        service?.endLocation()
    }


    /**
     * 开始绑定 methodChannel
     */
    fun startListening(context: Context, messenger: BinaryMessenger) {
        if (eventChannel != null) {
            Log.w(TAG, "Setting a method call handler before the last was disposed.")
            stopListening()
        }

        this.context = context

        eventChannel =  EventChannel(messenger, channelName)
        eventChannel?.setStreamHandler(this)
    }

    /**
     * 结束绑定
     */
    fun stopListening() {
        if (eventChannel == null) {
            Log.d(TAG, "Tried to stop listening when no MethodChannel had been initialized.")
            return
        }
        eventChannel?.setStreamHandler(null)
        eventChannel = null
    }

    fun setActivity( activity: Activity?) {
        this.activity = activity
    }
    fun setService(service: GeoLocationService?) {
        this.service = service
    }



}
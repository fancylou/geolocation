package net.o2oa.flutter.geolocation

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.location.Address
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.plugin.common.MethodChannel
import net.o2oa.flutter.geolocation.lm.LocationUtils


/**
 * Created by fancyLou on 2023-11-13.
 * Copyright © 2023 geolocation. All rights reserved.
 */
class GeoLocationService : Service() {

    private val TAG = "GeoLocationService"
    private val binder: LocalBinder = LocalBinder(this)

    private var connectedEngines = 0
    private var activity: Activity? = null


    override fun onBind(intent: Intent?): IBinder? {
        Log.i(TAG, "GeoLocationService onBind ....")
        return binder
    }

    fun flutterEngineConnected() {
        connectedEngines++
        Log.d(TAG, "Flutter engine connected. Connected engine count $connectedEngines")
    }

    fun flutterEngineDisconnected() {
        connectedEngines--
        Log.d(TAG, "Flutter engine disconnected. Connected engine count $connectedEngines")
    }


    fun setActivity(activity: Activity?) {
        this.activity = activity
    }

    fun onGetLastKnownPosition(addressCallback: LocationUtils.AddressCallback) {
        LocationUtils.getInstance(applicationContext)?.onGetLastKnownLocation(addressCallback)
    }

    fun onGetCurrentPosition(addressCallback: LocationUtils.AddressCallback) {
        LocationUtils.getInstance(applicationContext)?.onGetCurrentPosition(addressCallback)
        Log.d(TAG, "onGetCurrentPosition is start.......................")
    }

    fun startLocation(addressCallback: LocationUtils.AddressCallback) {
        LocationUtils.getInstance(applicationContext)?.startLocation(addressCallback)
    }

    fun endLocation() {
        LocationUtils.getInstance(applicationContext)?.endLocation()
    }


    internal class LocalBinder(locationService: GeoLocationService) :
        Binder() {
        private val locationService: GeoLocationService

        init {
            this.locationService = locationService
        }

        fun getLocationService(): GeoLocationService {
            return locationService
        }
    }
}
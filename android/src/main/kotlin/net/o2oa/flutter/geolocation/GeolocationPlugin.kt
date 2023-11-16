package net.o2oa.flutter.geolocation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding


/** GeolocationPlugin */
class GeolocationPlugin: FlutterPlugin, ActivityAware {
  private val TAG = "GeolocationPlugin"


  private var foregroundLocationService: GeoLocationService? = null
  private val serviceConnection: ServiceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName, service: IBinder) {
      Log.d(TAG, "Geolocation foreground service connected")
      if (service is GeoLocationService.LocalBinder) {
        initialize((service as GeoLocationService.LocalBinder).getLocationService())
      }
    }

    override fun onServiceDisconnected(name: ComponentName) {
      Log.d(TAG, "Geolocation foreground service disconnected")
      if (foregroundLocationService != null) {
        foregroundLocationService!!.setActivity(null)
        foregroundLocationService = null
      }
    }
  }

  private var methodChannel: MethodChannelImpl? = null
  private var eventChannel: EventChannelImpl? = null

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    methodChannel?.setActivity(binding.activity)
    eventChannel?.setActivity(binding.activity)
  }

  override fun onDetachedFromActivity() {
    methodChannel?.setActivity(null)
    eventChannel?.setActivity(null)
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity()
  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(TAG, "Geolocation onAttachedToEngine ==================")
    methodChannel = MethodChannelImpl()
    methodChannel?.startListening(flutterPluginBinding.applicationContext, flutterPluginBinding.binaryMessenger)
    eventChannel = EventChannelImpl()
    eventChannel?.startListening(flutterPluginBinding.applicationContext, flutterPluginBinding.binaryMessenger)
    bindForegroundService(flutterPluginBinding.applicationContext)
  }


  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(TAG, "Geolocation onDetachedFromEngine ==================")
    unbindForegroundService(binding.applicationContext)
    methodChannel?.stopListening()
    eventChannel?.stopListening()
    methodChannel?.setService(null)
    eventChannel?.setService(null)
  }



  private fun bindForegroundService(context: Context) {
    context.bindService(
      Intent(context, GeoLocationService::class.java),
      serviceConnection,
      Context.BIND_AUTO_CREATE
    )
  }

  private fun unbindForegroundService(context: Context) {
    foregroundLocationService?.flutterEngineDisconnected()
    context.unbindService(serviceConnection)
  }

  private fun initialize(service: GeoLocationService) {
    Log.d(TAG, "Initializing Geolocation services")
    foregroundLocationService = service
    foregroundLocationService!!.flutterEngineConnected()
    methodChannel?.setService(service)
    eventChannel?.setService(service)
  }



}

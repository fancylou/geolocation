
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'geolocation_platform_interface.dart';
import 'models/position.dart';

/// An implementation of [GeolocationPlatform] that uses method channels.
class MethodChannelGeolocation extends GeolocationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('net.o2oa.flutter.geolocation/method');

  /// The event channel used to receive [GeoPosition] updates from the native
  /// platform.
  final _eventChannel = const EventChannel('net.o2oa.flutter.geolocation/event');

  Stream<GeoPosition>? _positionStream;

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<GeoPosition?> getLastKnownPosition() async {
    try {
      final position = await methodChannel.invokeMethod<dynamic>('getLastKnownPosition');
      return GeoPosition.fromJson(position.cast<String, dynamic>());
    } on Exception catch (e) {
      return Future.error(e);
    }
  }

  @override
  Future<GeoPosition?> getCurrentPosition() async {
    try {
      final position = await methodChannel.invokeMethod<dynamic>('getCurrentPosition');
      return GeoPosition.fromJson(position.cast<String, dynamic>());
    } on Exception catch (e) {
      return Future.error(e);
    }
  }

  @override
  Stream<GeoPosition> getPositionStream() {
    if (_positionStream != null) {
      return _positionStream!;
    }
    var originalStream = _eventChannel.receiveBroadcastStream(
      // 参数传入
    );
    final positionStream = originalStream.asBroadcastStream(onCancel: (s){
      s.cancel();
      _positionStream = null;
    });
    _positionStream = positionStream.map<GeoPosition>((event) => GeoPosition.fromJson(event.cast<String, dynamic>())).handleError((err){
      throw err;
    });
    return _positionStream!;
  } 
  
}

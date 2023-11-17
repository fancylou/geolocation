import 'dart:math';

import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'geolocation_method_channel.dart';
import 'models/position.dart';

abstract class GeolocationPlatform extends PlatformInterface {
  /// Constructs a GeolocationPlatform.
  GeolocationPlatform() : super(token: _token);

  static final Object _token = Object();

  static GeolocationPlatform _instance = MethodChannelGeolocation();

  /// The default instance of [GeolocationPlatform] to use.
  ///
  /// Defaults to [MethodChannelGeolocation].
  static GeolocationPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [GeolocationPlatform] when
  /// they register themselves.
  static set instance(GeolocationPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  /// 最后一次定位信息
  Future<GeoPosition?> getLastKnownPosition() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  /// 当前位置信息
  Future<GeoPosition?> getCurrentPosition() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  /// 连续定位
  Stream<GeoPosition> getPositionStream(){
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  /// 计算两点之间的距离
  double distanceBetween(
    GeoPosition start,
    GeoPosition end,
  ) {
    final startLatitude = start.latitude;
    final startLongitude = start.longitude;
    final endLatitude = end.latitude;
    final endLongitude = end.longitude;

    const earthRadius = 6378137.0;
    var dLat = _toRadians(endLatitude - startLatitude);
    var dLon = _toRadians(endLongitude - startLongitude);

    var a = pow(sin(dLat / 2), 2) +
        pow(sin(dLon / 2), 2) *
            cos(_toRadians(startLatitude)) *
            cos(_toRadians(endLatitude));
    var c = 2 * asin(sqrt(a));

    return earthRadius * c;
  }

  double _toRadians(double degree) {
    return degree * pi / 180;
  }
  
}

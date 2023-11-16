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

  Future<GeoPosition?> getLastKnownPosition() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<GeoPosition?> getCurrentPosition() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Stream<GeoPosition> getPositionStream(){
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}

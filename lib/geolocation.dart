
import 'geolocation_platform_interface.dart';
import 'models/position.dart';

class Geolocation {
  static Future<String?> getPlatformVersion() => GeolocationPlatform.instance.getPlatformVersion();

  static Future<GeoPosition?> getLastKnownPosition() =>  GeolocationPlatform.instance.getLastKnownPosition();

  static Future<GeoPosition?> getCurrentPosition() => GeolocationPlatform.instance.getCurrentPosition();

 

  static Stream<GeoPosition> getPositionStream() => GeolocationPlatform.instance.getPositionStream();
}

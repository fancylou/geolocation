import 'package:flutter_test/flutter_test.dart';
import 'package:geolocation/geolocation.dart';
import 'package:geolocation/geolocation_platform_interface.dart';
import 'package:geolocation/geolocation_method_channel.dart';
import 'package:geolocation/models/position.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockGeolocationPlatform
    with MockPlatformInterfaceMixin
    implements GeolocationPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
  
  @override
  Future<GeoPosition?> getCurrentPosition() => Future.value(null);
  
  @override
  Future<GeoPosition?> getLastKnownPosition() => Future.value(null);

  @override
  Stream<GeoPosition> getPositionStream() {
    throw UnimplementedError();
  }
  double distanceBetween(GeoPosition start, GeoPosition end) => 0;
   
}

void main() {
  final GeolocationPlatform initialPlatform = GeolocationPlatform.instance;

  test('$MethodChannelGeolocation is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelGeolocation>());
  });

  test('getPlatformVersion', () async {
    MockGeolocationPlatform fakePlatform = MockGeolocationPlatform();
    GeolocationPlatform.instance = fakePlatform;

    expect(await Geolocation.getPlatformVersion(), '42');
  });
}

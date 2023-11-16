# Flutter geolocation Plugin

A simple positioning related Flutter plugin project, supporting Android and iOS platforms.

## Getting Started

This will add a line like this to your package's pubspec.yaml (and run an implicit flutter pub get):

``` yaml
dependencies:
  geolocation: ^0.0.1

```

  
<details>
<summary>Android</summary>

**AndroidX** 

The geolocation plugin requires the AndroidX version of the Android Support Libraries. This means you need to make sure your Android project supports AndroidX. Detailed instructions can be found [here](https://flutter.dev/docs/development/packages-and-plugins/androidx-compatibility). 


1. Add the following to your "gradle.properties" file:

```
android.useAndroidX=true
android.enableJetifier=true
```
 
2. Make sure you replace all the `android.` dependencies to their AndroidX counterparts (a full list can be found here: [Migrating to AndroidX](https://developer.android.com/jetpack/androidx/migrate)).

**Permissions**

On Android you'll need to add either the `ACCESS_COARSE_LOCATION` or the `ACCESS_FINE_LOCATION` permission to your Android Manifest. To do so open the AndroidManifest.xml file (located under android/app/src/main) and add one of the following two lines as direct children of the `<manifest>` tag (when you configure both permissions the `ACCESS_FINE_LOCATION` will be used by the geolocation plugin):

``` xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```


> **NOTE:** Specifying the `ACCESS_COARSE_LOCATION` permission results in location updates with an accuracy approximately equivalent to a city block. It might take a long time (minutes) before you will get your first locations fix as `ACCESS_COARSE_LOCATION` will only use the network services to calculate the position of the device. More information can be found [here](https://developer.android.com/training/location/retrieve-current#permissions). 


</details>

<details>
<summary>iOS</summary>

On iOS you'll need to add the following entries to your Info.plist file (located under ios/Runner) in order to access the device's location. Simply open your Info.plist file and add the following (make sure you update the description so it is meaningfull in the context of your App):

``` xml
<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
<string>APP需要您的同意，才能持续访问定位位置</string>
<key>NSLocationWhenInUseUsageDescription</key>
<string>APP需要您的同意，才能访问定位位置信息</string>
```

</details>



### Example

The code below shows an example on how to acquire the current position of the device, including checking if the location services are enabled and checking / requesting permission to access the position of the device:

```dart
import 'package:permission_handler/permission_handler.dart';
import 'package:geolocation/geolocation.dart';
import 'package:geolocation/models/position.dart';


StreamSubscription<GeoPosition>? _stream;

/// check permission
/// you need install permission_handler plugin
Future<bool> _locationPermission() async {
  var status = await Permission.location.status;
  if (status == PermissionStatus.granted) {
    return true;
  } else {
    if (kDebugMode) {
      print('request.......................');
    }
    status = await Permission.location.request();
    if (status == PermissionStatus.granted) {
      return true;
    }
  }
  return false;
}

/// get last known location
Future<void> _getLastKnownPosition() async {
  bool hasLocationPermission = await _locationPermission();
  if (!hasLocationPermission) {
    return;
  }
  final result = await Geolocation.getLastKnownPosition();
  if (kDebugMode) {
    print(result?.toJson());
  }
}

/// get current location
Future<void> _getCurrentPosition() async {
  bool hasLocationPermission = await _locationPermission();
  if (!hasLocationPermission) {
    return;
  }
  final result = await Geolocation.getCurrentPosition();
  if (kDebugMode) {
    print(result?.toJson());
  }
}

/// start series location
Future<void> _startLocation() async {
  bool hasLocationPermission = await _locationPermission();
  if (!hasLocationPermission) {
    return;
  }
  if (_stream != null) {
    _stream!.cancel();
    _stream = null;
  }
  _stream = Geolocation.getPositionStream().listen((position) {
  if (kDebugMode) {
    print(position.toJson());
  }
  });
  _stream?.onError((err){
  if (kDebugMode) {
    print(err.toString());
  }
  });
}

/// stop series location
Future<void> _endLocation() async {
  if (_stream != null) {
    _stream!.cancel();
    _stream = null;
  }
}
```
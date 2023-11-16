import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:geolocation/geolocation.dart';
import 'package:geolocation/models/position.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  StreamSubscription<GeoPosition>? _stream;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  /// 定位权限判断
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
  /// 定位权限 不通过就提示错误信息
  void _requestLocationPermission() async {
    // 申请权限
    bool hasLocationPermission = await _locationPermission();
    if (hasLocationPermission) {
      // 权限申请通过
      if (kDebugMode) {
        print('定位权限已开启，开始定位');
      }
    } else {
      if (kDebugMode) {
        print('没有权限，无法定位');
      }
    }
  }

  void _getLastKnownPosition() async {
    final result = await Geolocation.getLastKnownPosition();
    if (kDebugMode) {
      print(result?.toJson());
    }
  }
  void _getCurrentPosition() async {
    final result = await Geolocation.getCurrentPosition();
    if (kDebugMode) {
      print(result?.toJson());
    }
  }
  void _startLocation() async {
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
  void _endLocation() async {
    if (_stream != null) {
      _stream!.cancel();
      _stream = null;
    }
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion =
          await Geolocation.getPlatformVersion() ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }
    if (!mounted) return;
    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: 
          Column(children: [
            Text('Running on: $_platformVersion\n'),
            TextButton(onPressed: ()=>_requestLocationPermission(), child: const Text('请求权限！'),),
            TextButton(onPressed: ()=>_getLastKnownPosition(), child: const Text('最近定位'),),
            TextButton(onPressed: ()=>_getCurrentPosition(), child: const Text('单次定位'),),
            TextButton(onPressed: ()=>_startLocation(), child: const Text('开始定位'),),
            TextButton(onPressed: ()=>_endLocation(), child: const Text('结束定位'),),
          ],)
          
          
        ),
      ),
    );
  }
}

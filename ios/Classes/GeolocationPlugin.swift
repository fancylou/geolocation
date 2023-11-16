import Flutter
import UIKit

public class GeolocationPlugin: NSObject, FlutterPlugin {
    public static let errorCode1 = "10001" // exception
    public static let errorCode2 = "10002" // null

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "net.o2oa.flutter.geolocation/method", binaryMessenger: registrar.messenger())
        let eventChannel = FlutterEventChannel(name: "net.o2oa.flutter.geolocation/event", binaryMessenger: registrar.messenger())

        let instance = GeolocationPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)

        let geoStreamHandler = GeoLocationStreamHandler(locationHelper: instance.getLocationHelper())

        eventChannel.setStreamHandler(geoStreamHandler)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "getPlatformVersion":
            result("iOS " + UIDevice.current.systemVersion)
            break
        case "getLastKnownPosition":
            getLastKnownPosition(result: result)
            break
        case "getCurrentPosition":
            getCurrentPosition(result: result)
            break
        default:
            result(FlutterMethodNotImplemented)
            break
        }
    }

    var geoLocationHelper: GeoLocationHelper?

    func getLocationHelper() -> GeoLocationHelper {
        if geoLocationHelper == nil {
            geoLocationHelper = GeoLocationHelper()
        }
        return geoLocationHelper!
    }

    private func getLastKnownPosition(result: @escaping FlutterResult) {
        let helper = getLocationHelper()
        let position = helper.getLastKnownPosition()
        if position != nil {
            helper.geoReverse(location: position!) { dic in
                result(dic)
            }
        } else {
            result(GeolocationPlugin.errorCode2)
        }
    }

    private func getCurrentPosition(result: @escaping FlutterResult) {
        let helper = getLocationHelper()
        helper.getCurrentPosition(result: { position in
            helper.geoReverse(location: position) { dic in
                result(dic)
            }
        }, error: { code, _ in
            result(code)
        })
    }
}

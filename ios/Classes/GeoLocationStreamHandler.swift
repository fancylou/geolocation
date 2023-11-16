//
//  GeoLocationStreamHandler.swift
//  geolocation
//
//  Created by FancyLou on 2023/11/16.
//

import Flutter
import UIKit

class GeoLocationStreamHandler: NSObject, FlutterStreamHandler {
    private var locationHelper: GeoLocationHelper?

    init(locationHelper: GeoLocationHelper) {
        self.locationHelper = locationHelper
    }

    func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        locationHelper?.startSeriesLocation(result: { position in
            self.locationHelper?.geoReverse(location: position) { dic in
                events(dic)
            }
        }, error: { errorCode, _ in
            events(errorCode)
        })

        return nil
    }

    func onCancel(withArguments arguments: Any?) -> FlutterError? {
        locationHelper?.stopLocation()
        return nil
    }
}

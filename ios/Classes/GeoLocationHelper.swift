//
//  GeoLocationHelper.swift
//  geolocation
//
//  Created by FancyLou on 2023/11/15.
//

import CoreLocation

typealias GeoLocationResult = (_ position: CLLocation) -> Void
typealias GeoLocationError = (_ errorCode: String, _ errorMsg: String?) -> Void

class GeoLocationHelper: NSObject {
    var locationManager: CLLocationManager?
    var currentResultHandler: GeoLocationResult?
    var resultHandler: GeoLocationResult?
    var errorHandler: GeoLocationError?
    // is it positioning once
    var isListeningUpdatePosition = false

    /// get the last known position
    public func getLastKnownPosition() -> CLLocation? {
        let lm = getLocationManager()
        return lm.location
    }

    /// get position once
    public func getCurrentPosition(result: @escaping GeoLocationResult, error: @escaping GeoLocationError) {
        currentResultHandler = result
        errorHandler = error
        isListeningUpdatePosition = false
        startLocation()
    }

    /// continuous get position
    public func startSeriesLocation(result: @escaping GeoLocationResult, error: @escaping GeoLocationError) {
        resultHandler = result
        errorHandler = error
        isListeningUpdatePosition = true
        startLocation()
    }

    private func startLocation() {
        print("startLocation  ====== ")
        let lm = getLocationManager()
        lm.distanceFilter = kCLDistanceFilterNone
          if  #available(iOS 6.0, macOS 10.15, *) {
              lm.activityType = CLActivityType.other
          }
        lm.startUpdatingLocation()
    }

    public func stopLocation() {
        print("stopLocation  ====== ")
        isListeningUpdatePosition = false
        resultHandler = nil
        errorHandler = nil
        let lm = getLocationManager()
        lm.stopUpdatingLocation()
    }

    public func geoReverse(location: CLLocation, addressCall: @escaping (_: Dictionary<String, Any>) -> Void) {
        print("geoReverse    \(location) ====== ")
        // 地理编码的类
        let gecoder = CLGeocoder()
        // 反地理编码 转换成 具体的地址
        gecoder.reverseGeocodeLocation(location) { placeMarks, _ in
            // CLPlacemark －－ 国家 城市 街道
            if let placeMark = placeMarks?.first {
                let add = "\(placeMark.name ?? ""), \(placeMark.country ?? "")\(placeMark.administrativeArea ?? "")\(placeMark.locality ?? "") \(placeMark.subLocality ?? "")\(placeMark.thoroughfare ?? "")"
                print("\(placeMark)")
                addressCall(self.toDic(location: location, address: add))
            } else {
                addressCall(self.toDic(location: location, address: nil))
            }
        }
    }

    public func toDic(location: CLLocation?, address: String?) -> Dictionary<String, Any> {
        var dic = Dictionary<String, Any>()
        dic["latitude"] = location?.coordinate.latitude ?? 0.0
        dic["longitude"] = location?.coordinate.longitude ?? 0.0
        dic["address"] = address ?? ""
        return dic
    }

    private func getLocationManager() -> CLLocationManager {
        if locationManager == nil {
            locationManager = CLLocationManager()
            locationManager?.delegate = self
            if #available(iOS 6.0, macOS 10.15, *) {
                locationManager!.pausesLocationUpdatesAutomatically = false
            }
        }
        return locationManager!
    }
}

extension GeoLocationHelper: CLLocationManagerDelegate {
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        print("location didUpdateLocations \(locations.count)")
        if let last = locations.last {
            currentResultHandler?(last)
            resultHandler?(last)
        }

        currentResultHandler = nil

        if !isListeningUpdatePosition {
            stopLocation()
        }
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("location error \(error.localizedDescription)")
        errorHandler?(GeolocationPlugin.errorCode1, error.localizedDescription)
    }
}

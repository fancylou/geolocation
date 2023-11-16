package net.o2oa.flutter.geolocation


/**
 * Created by fancyLou on 2023-11-15.
 * Copyright © 2023 android. All rights reserved.
 */
data class GeoPosition(
    var latitude: Double?,
    var longitude: Double?,
    var address: String?,
) {

    fun toHashMap(): Map<String, Any> {
        val position: HashMap<String, Any> = HashMap()
        position["latitude"] = latitude ?: 0.0
        position["longitude"] = longitude ?: 0.0
        position["address"] = address  ?: ""
        return position
    }
}
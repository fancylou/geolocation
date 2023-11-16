class GeoPosition {
  GeoPosition({required this.latitude, required this.longitude, this.address});

  final double latitude;
  final double longitude;
  String? address;

  factory GeoPosition.fromJson(Map<String, dynamic> json) => GeoPosition(
        latitude: json["latitude"] ?? 0.0,
        longitude: json["longitude"] ?? 0.0,
        address: json["address"],
      );

  Map<String, dynamic> toJson() => {
        "latitude": latitude,
        "longitude": longitude,
        "address": address,
      };
}

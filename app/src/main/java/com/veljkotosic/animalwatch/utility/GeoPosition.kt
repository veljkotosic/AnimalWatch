package com.veljkotosic.animalwatch.utility

import android.location.Location
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

fun GeoPoint.toLatLng() : LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun GeoPoint.toGeoLocation() : GeoLocation {
    return GeoLocation(this.latitude, this.longitude)
}

fun LatLng.toGeoPoint() : GeoPoint {
    return GeoPoint(this.latitude, this.longitude)
}

fun LatLng.toGeoLocation() : GeoLocation {
    return GeoLocation(this.latitude, this.longitude)
}

fun GeoLocation.toLatLng() : LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun GeoLocation.toGeoPoint() : GeoPoint {
    return GeoPoint(this.latitude, this.longitude)
}

fun Location.toGeoLocation() : GeoLocation {
    return GeoLocation(this.latitude, this.longitude)
}
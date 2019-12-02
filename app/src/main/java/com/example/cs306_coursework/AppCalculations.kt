package com.example.cs306_coursework

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*
import kotlin.random.Random

object AppCalculations {

    fun createRandomMarkerLocation(currentLocation: LatLng, radius: Int): LatLng {

        // Convert radius from meters to degrees
        val radiusInDegrees = (radius / 111000f).toDouble()

        val u = Random.nextDouble()
        val v = Random.nextDouble()
        val w = radiusInDegrees * sqrt(u)
        val t = 2.0 * Math.PI * v
        val x = w * cos(t)
        val y = w * sin(t)

        // Adjust the x-coordinate for the shrinking of the east-west distances
        val newX = x / cos(Math.toRadians(currentLocation.latitude))

        val foundLongitude = newX + currentLocation.longitude
        val foundLatitude = y + currentLocation.latitude

        return  LatLng(foundLatitude, foundLongitude)
    }

    fun findDistance(point1: LatLng, point2: LatLng): Double {
        val lon1 = Math.toRadians(point1.longitude)
        val lon2 = Math.toRadians(point2.longitude)
        val lat1 = Math.toRadians(point1.latitude)
        val lat2 = Math.toRadians(point2.latitude)

        val dlon = lon2 - lon1
        val dlat = lat2 - lat1
        val a = sin(dlat / 2).pow(2.0) + (cos(lat1) * cos(lat2)
                * Math.pow(sin(dlon / 2), 2.0))

        val c = 2 * asin(sqrt(a))

        // Radius of earth in kilometers. Use 3956
        // for miles 6371.0
        val r = 3956

        // calculate the result
        return c * r
    }

}
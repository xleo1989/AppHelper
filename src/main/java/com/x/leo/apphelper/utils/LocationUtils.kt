package com.x.leo.apphelper.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable
import java.io.IOException


/**
 * @作者:XJY
 * @创建日期: 2017/11/15 13:46
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */
object LocationUtils{
    @SuppressLint("MissingPermission")
    fun getLocation(ctx: Context): Location? {
        val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_MEDIUM
        criteria.bearingAccuracy = Criteria.ACCURACY_LOW
        criteria.horizontalAccuracy = Criteria.ACCURACY_MEDIUM
        criteria.isAltitudeRequired = true
        criteria.isBearingRequired = false
        criteria.isCostAllowed = true
        criteria.isSpeedRequired = false
        criteria.verticalAccuracy = Criteria.ACCURACY_LOW
        criteria.powerRequirement = Criteria.NO_REQUIREMENT
        val bestProvider = locationManager.getBestProvider(criteria, true)
        if (bestProvider != null) {
            val lastKnowLocation = locationManager.getLastKnownLocation(bestProvider)
            if (lastKnowLocation != null) {
                return lastKnowLocation
            }
        }
        if (locationManager.allProviders.contains(LocationManager.GPS_PROVIDER)) {
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                return lastKnownLocation
            }
        }
        if(locationManager.allProviders.contains(LocationManager.PASSIVE_PROVIDER)){
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            if (lastKnownLocation != null) {
                return lastKnownLocation
            }
        }

        if (locationManager.allProviders.contains(LocationManager.NETWORK_PROVIDER)){
            val lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (lastKnowLocation != null) {
                return lastKnowLocation
            }
        }
        return null
    }

    fun getLocationInfo(location: Location){

    }

    fun getAddresses(ctx: Context, latitude: Double, longitude: Double): Observable<List<Address>> {
        return Observable.create { subscriber ->
            try {
                val fromLocation = Geocoder(ctx.applicationContext).getFromLocation(latitude, longitude, 100)
                subscriber.onNext(fromLocation)
            } catch (e: IOException) {
                subscriber.onError(e)
            }
            subscriber.onCompleted()
        }
    }

    val BaseUrl = "http://maps.googleapis.com"
    internal interface GeoApi {
        @GET("maps/api/geocode/json?sensor=true&language=in")
        fun getLocationByLocation(@Query("latlng") latlng: String): Observable<GeoLocationBean>
    }

}
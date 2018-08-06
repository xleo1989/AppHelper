package com.x.leo.apphelper.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Looper
import android.os.NetworkOnMainThreadException
import com.x.leo.apphelper.log.xlog.XLog
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import rx.Observable
import java.io.IOException
import java.io.Serializable
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit


/**
 * @作者:XJY
 * @创建日期: 2017/11/15 13:46
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */
object LocationUtils {
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
        if (locationManager.allProviders.contains(LocationManager.PASSIVE_PROVIDER)) {
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            if (lastKnownLocation != null) {
                return lastKnownLocation
            }
        }

        if (locationManager.allProviders.contains(LocationManager.NETWORK_PROVIDER)) {
            val lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (lastKnowLocation != null) {
                return lastKnowLocation
            }
        }
        return null
    }

    fun getLocationInfo(location: Location) {

    }

    fun getLocationInfoByBaidu() {

    }

    fun getAddresses(ctx: Context, latitude: Double, longitude: Double): Observable<List<Address>> {
        return Observable.create { subscriber ->
            try {
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    throw NetworkOnMainThreadException()
                }
                val fromLocation = Geocoder(ctx.applicationContext).getFromLocation(latitude, longitude, 100)
                if (fromLocation.size > 0) {
                    fromLocation.get(0).maxAddressLineIndex
                }
                subscriber.onNext(fromLocation)
            } catch (e: IOException) {
                subscriber.onError(e)
            }
            subscriber.onCompleted()
        }
    }

    class BaiduSn{

        fun getC2LSn(paramsMap:LinkedHashMap<String,String>,sk:String):String{
            val toQueryString = toQueryString(paramsMap)
            val wholeStr = "/cloudrgc/v1?" + toQueryString + sk
            val mD5 = MD5(URLEncoder.encode(wholeStr!!, "UTF-8")!!)!!
            XLog.d("sn:" + mD5,10)
            return mD5
        }

        fun getC2LSnV2(paramsMap:LinkedHashMap<String,String>,sk:String):String{
            val toQueryString = toQueryString(paramsMap)
            val wholeStr = "/geocoder/v2/?" + toQueryString + sk
            val mD5 = MD5(URLEncoder.encode(wholeStr!!, "UTF-8")!!)!!
            XLog.d("sn:" + mD5,10)
            return mD5
        }
        @Throws(UnsupportedEncodingException::class)
        fun toQueryString(data: Map<*, *>): String {
            val queryString = StringBuffer()
            for ((key, value) in data) {
                queryString.append(key.toString() + "=")
                queryString.append(URLEncoder.encode(value as String,
                        "UTF-8") + "&")
            }
            if (queryString.length > 0) {
                queryString.deleteCharAt(queryString.length - 1)
            }
            return queryString.toString()
        }

        // 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
        fun MD5(md5: String): String? {
            try {
                val md = java.security.MessageDigest
                        .getInstance("MD5")
                val array = md.digest(md5.toByteArray())
                val sb = StringBuffer()
                for (i in array.indices) {
                    sb.append(Integer.toHexString(array[i].toInt().and(0xFF).or( 0x100))
                            .substring(1, 3))
                }
                return sb.toString()
            } catch (e: java.security.NoSuchAlgorithmException) {
            }

            return null
        }

        fun getCTsn(paramsMap: LinkedHashMap<String, String>, sk: String): String {
            val toQueryString = toQueryString(paramsMap)
            val wholeStr = "/geodata/v3/geotable/create" + toQueryString + sk
            val mD5 = MD5(URLEncoder.encode(wholeStr, "UTF-8")!!)!!
            XLog.d("sn:" + mD5,10)
            return mD5
        }
    }

    val GOOGLE_MAP_API = "http://maps.googleapis.com"
    val BAIDU_MAP_API = "http://api.map.baidu.com"
    val GAODE_API = "http://restapi.amap.com"
    enum class BaiduCOORDTYPE(val value:String){
        BAIDU("bd09ll"), GCJ("gcj02ll"),WGS("wgs84ll")
    }

    interface GeoApi {

        @GET("maps/api/geocode/json?sensor=true&language=zh")
        fun getLocationByLocation(@Query("latlng") latlng: Location): Observable<GeoLocationBean>

        /**
         * coord_type  输入坐标的坐标系：
         * bd09ll（百度经纬度坐标）、gcj02ll（国测局经纬度坐标）、wgs84ll（wgs84经纬度坐标）
         */
        @GET("/cloudrgc/v1")
        fun getLocationByBaidu(@Query("sn") sn:String,@QueryMap paramsMap: LinkedHashMap<String, String>):Observable<LocationAddressBean>

        @GET("/geocoder/v2/")
        fun getLocationByBaiduV2(@QueryMap map:LinkedHashMap<String,String>):Observable<ResponseBody>


        @POST("/geodata/v3/geotable/create")
        @FormUrlEncoded
        fun createGeotable(@Field("sn") sn: String,@FieldMap paramsMap: LinkedHashMap<String, String>):Observable<GeoTableCreateResult>
        @POST("/geodata/v3/geotable/create")
        @FormUrlEncoded
        fun createGeotableWithNoSn(@FieldMap paramsMap: LinkedHashMap<String, String>):Observable<GeoTableCreateResult>

        /**
         * coordsys 可选值：gps;mapbar;baidu;autonavi
         */
        @GET("/v3/assistant/coordinate/convert")
        fun convertCodeByGaode(@Query("key") key: String,@Query("locations") location: String,@Query("coordsys") coordtype:String,@Query("output") output: String):Observable<GaoDeLoactionConvert>
        @GET("/v3/geocode/regeo")
        fun getLocationByGaoDe(@Query("output") output:String,@Query("location") location: String,@Query("key") key:String):Observable<GaodeLocation>

    }

    data class GeoTableCreateResult(val status:Int,val message:String,val id:String)
    data class BaiduLocation(val lat:Float,val lng:Float):Serializable

    data class LocationAddressBean(val statue:Int,val location:BaiduLocation?,val address_component:BaiduAddress,val formatted_address:String,val pois:BaiduPois?,val custom_pois:CustomPois?,val custom_location_description:String?,val recommended_location_description:Float?):Serializable{
        data class BaiduAddress(val country:String,val province:String,val city:String,val district:String,val street:String,val street_number:String,
                                val admin_area_code:Int,val country_code:Int):Serializable
        data class BaiduPois(val name:String,val id:String,val address:String,val tag:String,val location:Float,val direction:String,val distance:Int):Serializable
        data class CustomPois(val name:String,val address:String,val tag:String,val location:Float,val direction:String,val distance:Int):Serializable
    }
    fun getLocationService(baseUrl:String):GeoApi{
        return Retrofit.Builder().baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.MINUTES)
                        .readTimeout(3, TimeUnit.MINUTES)
                        .writeTimeout(3, TimeUnit.MINUTES)
                        .addInterceptor(HttpLoggingInterceptor())
                        .build())
                .build().create(GeoApi::class.java)
    }

    fun formLocation(locations: String): String {
        if (locations.contains(";")) {
            val split = locations.split(";")
            val sb = StringBuilder()
            split.forEach{
                sb.append(handleLocation(it) + ";")
            }
            if(sb.length > 0)
                sb.replace(sb.length - 1,sb.length-1,"")
            return sb.toString()
        }else{
            return handleLocation(locations)
        }
    }

    private fun handleLocation(locations: String): String {
        val split = locations.split(",")
        if (split.size == 2) {
            return String.format("%.6f", split[0].toFloat()) + "," + String.format("%.6f", split[1].toFloat())
        } else {
            throw IllegalArgumentException("locations format error")
        }
    }

}
data class GaodeLocation(
		val status: String, //1
		val info: String, //OK
		val infocode: String, //10000
		val regeocode: Regeocode
):Serializable

data class Regeocode(
		val formatted_address: String, //北京市朝阳区望京街道方恒国际中心B座方恒国际中心
		val addressComponent: AddressComponent
):Serializable

data class AddressComponent(
		val country: String, //中国
		val province: String, //北京市
		val city: Object?,
		val citycode: String, //010
		val district: String, //朝阳区
		val adcode: String, //110105
		val township: String, //望京街道
		val towncode: String, //110105026000
		val neighborhood: Object,
		val building: Object,
		val streetNumber: StreetNumber,
		val businessAreas: Object
):Serializable

data class Building(
		val name: String, //方恒国际中心B座
		val type: String //商务住宅;楼宇;商务写字楼
):Serializable

data class StreetNumber(
		val street: String, //阜通东大街
		val number: String, //6-2号楼
		val location: String, //116.48129,39.9902869
		val direction: String, //西南
		val distance: String //25.9205
):Serializable

data class BusinessArea(
		val location: String, //116.47089234140496,39.9976009239991
		val name: String, //望京
		val id: String //110105
):Serializable

data class Neighborhood(
		val name: Object, //方恒国际中心
		val type: String //商务住宅;楼宇;商住两用楼宇
):Serializable


data class GaoDeLoactionConvert(
		val status: String, //1
		val info: String, //ok
		val infocode: String, //10000
		val locations: String //116.48758517795239.991754014757
):Serializable
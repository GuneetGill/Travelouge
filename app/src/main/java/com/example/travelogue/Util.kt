package com.example.travelogue
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

object Util {
    fun getCountryName(context: Context, latlng: LatLng): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)
            if (addresses != null) {
                return addresses[0].countryName
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
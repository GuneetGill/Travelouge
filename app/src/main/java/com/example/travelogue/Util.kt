package com.example.travelogue
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

object Util {
    // Geocoding stuff
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


    // Check permissions given in a list
    fun checkPermissions(activity: Activity?, permissions: Array<String>){
        if (Build.VERSION.SDK_INT < 23) return
        var missingPermission : Boolean = false
        for(permission in permissions){
            if( ContextCompat.checkSelfPermission(activity!! , permission) != PackageManager.PERMISSION_GRANTED ){
                missingPermission = true
            }
        }
        if(missingPermission){
            ActivityCompat.requestPermissions( activity!!, permissions, 0)
        }
    }
}
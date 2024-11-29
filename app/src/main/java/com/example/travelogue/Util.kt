package com.example.travelogue
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.location.Geocoder
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.travelogue.Globals.PREF_NAME
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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

    // for biometric login
    fun saveToken(context: Context, userID: Long, isEnabled: Boolean) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(userID.toString(), isEnabled).apply()
    }

    fun isFingerprintEnabled(context: Context, userID: Long): Boolean? {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if (sharedPreferences.contains(userID.toString())) {
            return sharedPreferences.getBoolean(userID.toString(), false)
        }
        return null
    }

    // Function to show the dialog
    fun showEnableFingerprintDialog(context: Context, userID: Long, returnIntent: Intent) {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Enable Fingerprint Login")
            .setMessage("Do you want to use fingerprint authentication for future logins?")
            .setPositiveButton("Yes") { _, _ ->
                // User agrees to enable fingerprint login
                saveToken(context, userID, true)
                Toast.makeText(context, "Fingerprint Authentication Enabled", Toast.LENGTH_SHORT).show()
                context.startActivity(returnIntent)
            }
            .setNegativeButton("No") { _, _ ->
                // User declines
                saveToken(context, userID, false)
                context.startActivity(returnIntent)
            }
            .create()

        dialog.show()
    }

    // get userId
    fun getUserId(context: Context): Long {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1L)
    }

    // get bitmap from vector drawable
    // Function to create a BitmapDescriptor from a resource
    fun bitmapDescriptorFromVector(resources: Resources, resourceId: Int): BitmapDescriptor {
        val vectorDrawable = resources.getDrawable(resourceId, null)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = android.graphics.Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
package com.example.travelogue
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

    // for biometric login
    fun saveToken(context: Context, userID: Long) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("user_token", userID.toString()).apply()
        sharedPreferences.edit().putBoolean("fingerprint", true).apply()
    }

    fun isFingerprintEnabled(context: Context): Boolean? {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("fingerprint")) {
            return sharedPreferences.getBoolean("fingerprint", false)
        }
        return null
    }

    fun getToken(context: Context): Long? {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_token", null)?.toLong()
    }

    // Function to show the dialog
    fun showEnableFingerprintDialog(context: Context, userID: Long) {
        val intent = Intent(context, MainActivity::class.java)
        val dialog = AlertDialog.Builder(context)
            .setTitle("Enable Fingerprint Login")
            .setMessage("Do you want to use fingerprint authentication for future logins?")
            .setPositiveButton("Yes") { _, _ ->
                // User agrees to enable fingerprint login
                saveToken(context, userID)
                Toast.makeText(context, "Fingerprint Authentication Enabled", Toast.LENGTH_SHORT).show()
                context.startActivity(intent)
            }
            .setNegativeButton("No") { _, _ ->
                // User declines
                //saveFingerprintPreference(userID)
                context.startActivity(intent)
            }
            .create()

        dialog.show()
    }
}
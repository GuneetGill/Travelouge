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
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.travelogue.Globals.PREF_NAME
import com.example.travelogue.ui.doc.DocumentsActivity
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import java.util.Locale
import java.util.concurrent.Executor

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

    fun getBiometricPrompt(context: Context, activity: FragmentActivity, successIntent: Intent, userId: Long?, userPwd: String?): BiometricPrompt {

        lateinit var executor: Executor
        lateinit var biometricPrompt: BiometricPrompt
        val REQUEST_CODE = 1

        executor = ContextCompat.getMainExecutor(context)
        biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(context,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                    if (errorCode == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
                        // Prompts the user to create credentials that your app accepts.
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG
                            )
                        }
                        activity.startActivityForResult(enrollIntent, REQUEST_CODE)
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(context,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
                    // save userId in sharedPrefs
                    //share user id across the app if needed
                    if (userId != null) {
                        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.clear()
                        editor.putLong("user_id", userId)
                        editor.putString("user_password", userPwd)
                        editor.commit()
                    }
                    // go to success activity
                    context.startActivity(successIntent)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(context, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })
        return biometricPrompt
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
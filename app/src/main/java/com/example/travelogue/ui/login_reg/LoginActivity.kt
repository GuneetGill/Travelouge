package com.example.travelogue.ui.login_reg

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.travelogue.Globals.PREF_NAME
import com.example.travelogue.MainActivity
import com.example.travelogue.R
import com.example.travelogue.Util
import com.example.travelogue.db_user.UserDao
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.db_user.UserRepository
import com.example.travelogue.db_user.UserViewModel
import com.example.travelogue.db_user.UserViewModelFactory
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEmail: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var regButton: Button
    private lateinit var biometricLogin: Button

    private lateinit var database: UserDatabase
    private lateinit var databaseDao: UserDao
    private lateinit var repository: UserRepository
    private lateinit var viewModelFactory: UserViewModelFactory
    private lateinit var userViewModel: UserViewModel

    // biometric login stuff
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)


        usernameEmail = findViewById(R.id.user_name_email)
        password = findViewById(R.id.user_pw)
        loginButton = findViewById(R.id.login_button)
        regButton = findViewById(R.id.register_button)
        biometricLogin = findViewById(R.id.biometricLogin)

        //db initialize
        database = UserDatabase.getInstance(this)
        databaseDao = database.userDao
        repository = UserRepository(databaseDao)
        viewModelFactory = UserViewModelFactory(repository)
        userViewModel =
            ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)

        //display users in log cat
        userViewModel.allUsersLiveData.observe(this) { users ->
            if (users.isNotEmpty()) {
                users.forEach { user ->
                    Log.d("LoginActivity", "User : $user")
                }
            } else {
                Log.d("LoginActivity", "No users found in the database.")
            }
        }


        loginButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val usernameOrEmail = usernameEmail.text.toString()
            val userPassword = password.text.toString()
            var isUserFound = false
            var userId = -1L
            userViewModel.allUsersLiveData.observe(this) { users ->
                if (users.isNotEmpty()) {
                    users.forEach { user ->
                        if ((user.userEmail == usernameOrEmail || user.userName == usernameOrEmail) && user.userPassword == userPassword) {
                            isUserFound = true
                            userId = user.userId
                        }
                    }
                }
            }

            if(isUserFound) {
                //share user id across the app
                val sharedPreferences: SharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.clear()
                editor.putLong("user_id", userId)
                Log.d("myuserid", "User : $userId")
                editor.commit()
                // ask if user wants to enable fingerprint auth
                if (Util.isFingerprintEnabled(this) == null && Util.getToken(this) != userId) {
                    val intent = Intent(this, MainActivity::class.java)
                    Util.showEnableFingerprintDialog(this, userId, intent)
                }
                else {
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Wrong", Toast.LENGTH_SHORT).show()
            }

        }

        regButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Biometric login
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                    if (errorCode == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
                        // Prompts the user to create credentials that your app accepts.
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG)
                        }
                        startActivityForResult(enrollIntent, REQUEST_CODE)
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
                    // go to home
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        // disable and grey out biometric auth button if not enabled
        if (Util.isFingerprintEnabled(this) == null) {
            biometricLogin.isEnabled = false
            biometricLogin.alpha = 0.5f
        }

        usernameEmail?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // grey out login with finger print button if no username or email entered
                if (s.isNullOrEmpty()) {
                    biometricLogin.isEnabled = false
                    biometricLogin.alpha = 0.5f
                }
                else {
                    biometricLogin.isEnabled = true
                    biometricLogin.alpha = 1f
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        biometricLogin.setOnClickListener {
            // check that username/email field is filled out
            val usernameOrEmail = usernameEmail.text.toString()
            var userId = -1L
            userViewModel.allUsersLiveData.observe(this) { users ->
                if (users.isNotEmpty()) {
                    users.forEach { user ->
                        if ((user.userEmail == usernameOrEmail || user.userName == usernameOrEmail)) {
                            userId = user.userId
                        }
                    }
                }
            }
            if (userId == -1L) {
                Toast.makeText(applicationContext, "Invalid Username or Email.",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            // check that correct user is entered and has fingerprint enabled
            if (Util.getToken(this) == userId) {
                biometricPrompt.authenticate(promptInfo)
            }
            else {
                Toast.makeText(applicationContext, "This user does not have fingerprint enabled. Please login with password.",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
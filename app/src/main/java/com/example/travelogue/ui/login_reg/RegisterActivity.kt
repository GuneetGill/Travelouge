package com.example.travelogue.ui.login_reg

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

import androidx.lifecycle.ViewModelProvider
import com.example.travelogue.R

import com.example.travelogue.db_user.User
import com.example.travelogue.db_user.UserDao
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.db_user.UserRepository
import com.example.travelogue.db_user.UserViewModel
import com.example.travelogue.db_user.UserViewModelFactory
import com.google.firebase.auth.FirebaseAuth


class RegisterActivity : AppCompatActivity() {

    private lateinit var regFirstName : EditText
    private lateinit var regLastName : EditText
    private lateinit var regUserName : EditText
    private lateinit var regEmail : EditText
    private lateinit var regPassword : EditText
    private lateinit var regButton : Button
    private lateinit var regBackButton : Button

    private lateinit var database: UserDatabase
    private lateinit var databaseDao: UserDao
    private lateinit var repository: UserRepository
    private lateinit var viewModelFactory: UserViewModelFactory
    private lateinit var userViewModel: UserViewModel

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force Light Mode to see things better
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        regFirstName = findViewById(R.id.reg_first_name)
        regLastName = findViewById(R.id.reg_last_name)
        regUserName = findViewById(R.id.reg_user_name)
        regEmail = findViewById(R.id.reg_user_email)
        regPassword = findViewById(R.id.reg_user_pw)
        regButton = findViewById(R.id.reg_button)
        regBackButton = findViewById(R.id.reg_back_button)

        //db initialize
        database = UserDatabase.getInstance(this)
        databaseDao = database.userDao
        repository = UserRepository(databaseDao)
        viewModelFactory = UserViewModelFactory(repository)
        userViewModel =
            ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)

        // user object creation
        val user = User()

        regButton.setOnClickListener {
            val email = regEmail.text.toString().trim()
            val password = regPassword.text.toString().trim()

            // Input validation
            if (email.isEmpty()) {
                regEmail.error = "Email is required"
                regEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                regEmail.error = "Please enter a valid email"
                regEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6) {
                regPassword.error = "Password must be at least 6 characters"
                regPassword.requestFocus()
                return@setOnClickListener
            }

            // Register user with Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //add in db
                        user.userFirstName = regFirstName.text.toString()
                        user.userLastName = regLastName.text.toString()
                        user.userName = regUserName.text.toString()
                        user.userEmail = regEmail.text.toString()
                        user.userPassword = regPassword.text.toString()
                        userViewModel.insertUser(user)

                        // User successfully registered
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                        finish() // Close the registration screen
                    } else {
                        // Registration failed
                        Toast.makeText(
                            this,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        regBackButton.setOnClickListener{
            finish()
        }
    }
}
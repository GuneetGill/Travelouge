package com.example.travelogue.ui.login_reg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.travelogue.MainActivity
import com.example.travelogue.R
import com.example.travelogue.db_user.UserDao
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.db_user.UserRepository
import com.example.travelogue.db_user.UserViewModel
import com.example.travelogue.db_user.UserViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEmail : EditText
    private lateinit var password : EditText
    private lateinit var loginButton : Button
    private lateinit var regButton : Button

    private lateinit var database: UserDatabase
    private lateinit var databaseDao: UserDao
    private lateinit var repository: UserRepository
    private lateinit var viewModelFactory: UserViewModelFactory
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)


        usernameEmail = findViewById(R.id.user_name_email)
        password = findViewById(R.id.user_pw)
        loginButton = findViewById(R.id.login_button)
        regButton = findViewById(R.id.register_button)

        //db initialize
        database = UserDatabase.getInstance(this)
        databaseDao = database.userDao
        repository = UserRepository(databaseDao)
        viewModelFactory = UserViewModelFactory(repository)
        userViewModel =
            ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)

        //display users in log cat
        userViewModel.allTravelsLiveData.observe(this) { users ->
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
            startActivity(intent)
        }

        regButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}
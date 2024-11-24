package com.example.travelogue.ui.login_reg

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.ViewModelProvider
import com.example.travelogue.R

import com.example.travelogue.db_user.User
import com.example.travelogue.db_user.UserDao
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.db_user.UserRepository
import com.example.travelogue.db_user.UserViewModel
import com.example.travelogue.db_user.UserViewModelFactory

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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

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
            user.userFirstName = regFirstName.text.toString()
            user.userLastName = regLastName.text.toString()
            user.userName = regUserName.text.toString()
            user.userEmail = regEmail.text.toString()
            user.userPassword = regPassword.text.toString()
            userViewModel.insertUser(user)
            finish()
        }

        regBackButton.setOnClickListener{
            finish()
        }
    }
}
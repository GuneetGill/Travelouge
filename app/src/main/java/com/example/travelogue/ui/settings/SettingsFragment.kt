package com.example.travelogue.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import com.example.travelogue.R
import com.example.travelogue.databinding.FragmentSettingsBinding
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.ui.doc.DocumentsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.travelogue.Globals.PREF_NAME
import com.example.travelogue.MainActivity
import com.example.travelogue.Util

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val sharedPrefFile = "com.example.travelogue.PREFERENCE_FILE_KEY"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


        // Load user details
        loadUserDetails(sharedPreferences)

        // Set up Add Destination Button
        val addDestinationButton: Button = binding.buttonAddDestination
        addDestinationButton.setOnClickListener {
            showAddDestinationDialog()
        }

        // Set up Documents Button
        val documentsButton: Button = binding.buttonDocuments
//        documentsButton.setOnClickListener {
//            val intent = Intent(requireContext(), DocumentsActivity::class.java)
//            startActivity(intent)
//        }

        documentsButton.setOnClickListener {
            showPasswordDialog()
        }


        // Load currency list from strings.xml and set up Spinner
        val currencyList = resources.getStringArray(R.array.currency_list).toList()
        val currencyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencyList)
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCurrency.adapter = currencyAdapter

        // Load previously saved currency (if any) and set selection
        val savedCurrency = sharedPreferences.getString("preferred_currency", "")
        val position = currencyList.indexOf(savedCurrency)
        if (position >= 0) {
            binding.spinnerCurrency.setSelection(position)
        }

        // Set up Save Currency Button
        val saveCurrencyButton: Button = binding.buttonSaveCurrency
        saveCurrencyButton.setOnClickListener {
            val selectedCurrency = binding.spinnerCurrency.selectedItem.toString()
            if (selectedCurrency.isNotEmpty()) {
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("preferred_currency", selectedCurrency)
                editor.apply()
                Toast.makeText(context, "Currency saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please select a valid currency.", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up the Change button click listener
        binding.buttonChange.setOnClickListener {
            showChangeNameDialog()
        }

        // on click for enable fingerprint login
        binding.enableFingerPrintLogin.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            Util.showEnableFingerprintDialog(requireContext(), sharedPreferences.getLong("user_id", -1L), intent)
        }

        return root
    }

    private fun loadUserDetails(sharedPreferences: SharedPreferences) {
        // Get the logged-in user ID from SharedPreferences
        val userId = sharedPreferences.getLong("user_id", -1L)

        if (userId != -1L) {
            Log.d("SettingsFragment", "Retrieved user_id: $userId")
            // Fetch user details from the database in a coroutine
            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) {
                    val database = UserDatabase.getInstance(requireContext())
                    database.userDao.getUserById(userId)
                }
                user?.let {
                    // Update the UI with user details
                    binding.FLname.text = "${it.userFirstName} ${it.userLastName}"
                    binding.userName.text = it.userName
                } ?: run {
                    Toast.makeText(context, "User details not found.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "No logged-in user.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddDestinationDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_destination, null)
        val editTextCountry = dialogView.findViewById<EditText>(R.id.editText_country)
        val editTextCity = dialogView.findViewById<EditText>(R.id.editText_city)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Destination")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val country = editTextCountry.text.toString().trim()
                val city = editTextCity.text.toString().trim()
                if (country.isNotEmpty() && city.isNotEmpty()) {
                    addDestinationToBucketList("$city, $country")
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun addDestinationToBucketList(destination: String) {
        val checkBox = CheckBox(context)
        checkBox.text = destination

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val bucketListContainer: LinearLayout = binding.linearLayoutBucketList
                bucketListContainer.removeView(checkBox)
                Toast.makeText(context, "Congratulations on your new achievement!", Toast.LENGTH_SHORT).show()
            }
        }

        val bucketListContainer: LinearLayout = binding.linearLayoutBucketList
        bucketListContainer.addView(checkBox)
    }

    private fun showChangeNameDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_settings, null)
        val editTextFirstName = dialogView.findViewById<EditText>(R.id.editText_first_name)
        val editTextLastName = dialogView.findViewById<EditText>(R.id.editText_last_name)
        val editTextUsername = dialogView.findViewById<EditText>(R.id.editText_username)

        // Pre-fill current values in the dialog
        editTextFirstName.setText(binding.FLname.text.toString().split(" ")[0])
        editTextLastName.setText(binding.FLname.text.toString().split(" ")[1])
        editTextUsername.setText(binding.userName.text.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Change Name and Username")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val firstName = editTextFirstName.text.toString().trim()
                val lastName = editTextLastName.text.toString().trim()
                val username = editTextUsername.text.toString().trim()

                if (firstName.isNotEmpty() && lastName.isNotEmpty() && username.isNotEmpty()) {
                    val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    val userId = sharedPreferences.getLong("user_id", -1L)

                    if (userId != -1L) {
                        // Update the database
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                val database = UserDatabase.getInstance(requireContext())
                                val userDao = database.userDao
                                userDao.updateUserDetails(userId, firstName, lastName, username)
                            }

                            // Update UI on the main thread
                            binding.FLname.text = "$firstName $lastName"
                            binding.userName.text = username
                            Toast.makeText(context, "Name and username updated!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Error: Unable to retrieve user ID.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun showPasswordDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_password, null)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.password_edit_text)

        AlertDialog.Builder(requireContext())
            .setTitle("Enter Password")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val enteredPassword = passwordEditText.text.toString()
                validatePassword(enteredPassword)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun validatePassword(password: String) {
        // Retrieve the stored password (or set a predefined password for simplicity)
        val sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val storedPassword = sharedPreferences.getString("user_password", null)

        if (password == storedPassword) {
            // Password is correct, grant access
            val intent = Intent(requireContext(), DocumentsActivity::class.java)
            startActivity(intent)
        } else {
            // Password is incorrect, show error message
            Toast.makeText(context, "Wrong password. Access denied!", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

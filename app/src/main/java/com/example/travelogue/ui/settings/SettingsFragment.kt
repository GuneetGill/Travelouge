package com.example.travelogue.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.travelogue.R
import com.example.travelogue.databinding.FragmentSettingsBinding
import com.example.travelogue.DocumentsActivity

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
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        // Set up Add Destination Button
        val addDestinationButton: Button = binding.buttonAddDestination
        addDestinationButton.setOnClickListener {
            showAddDestinationDialog()
        }

        // Set up Documents Button
        val documentsButton: Button = binding.buttonDocuments
        documentsButton.setOnClickListener {
            val intent = Intent(requireContext(), DocumentsActivity::class.java)
            startActivity(intent)
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

        return root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// HomeFragment.kt
package com.example.travelogue.ui.home

import com.example.travelogue.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.travelogue.databinding.FragmentHomeBinding
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.table_country.CountryDao
import com.example.travelogue.table_country.CountryRepository
import com.example.travelogue.table_country.CountryViewModel
import com.example.travelogue.table_country.CountryViewModelFactory
import com.example.travelogue.ui.home.CountryFragment


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var countryAdapter: ArrayAdapter<String>

    private lateinit var database: UserDatabase
    private lateinit var databaseDao: CountryDao
    private lateinit var repository: CountryRepository
    private lateinit var viewModelFactory: CountryViewModelFactory
    private lateinit var countryViewModel: CountryViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Initialize for DB interaction
        database = UserDatabase.getInstance(requireActivity())
        databaseDao = database.countryDao
        repository = CountryRepository(databaseDao)
        viewModelFactory = CountryViewModelFactory(repository)
        countryViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(CountryViewModel::class.java)

        // Observe the LiveData from the ViewModel
        countryViewModel.allCountriesLiveData.observe(viewLifecycleOwner) { countries ->
            val countryNames = countries.map { it.countryName }
            countryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, countryNames)
            binding.countryListView.adapter = countryAdapter

            // Set item click listener for navigation to CountryFragment
            binding.countryListView.setOnItemClickListener { _, _, position, _ ->
                val selectedCountry = countries[position]

                // Define the navigation action and navigate to CountryFragment
                val bundle = Bundle().apply {
                    putString("countryName", selectedCountry.countryName)
                }
                findNavController().navigate(R.id.countryFragment, bundle)

            }
        }

        binding.addCountry.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addCountryFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

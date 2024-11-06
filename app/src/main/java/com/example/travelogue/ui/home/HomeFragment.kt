// HomeFragment.kt
package com.example.travelogue.ui.home

import com.example.travelogue.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.travelogue.databinding.FragmentHomeBinding
import com.example.travelogue.ui.home.CountryFragment


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var countryAdapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Observe the LiveData from the ViewModel
        viewModel.countriesLiveData.observe(viewLifecycleOwner) { countries ->
            val countryNames = countries.map { it.name }
            countryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, countryNames)
            binding.countryListView.adapter = countryAdapter

            // Set item click listener for navigation to CountryFragment
            binding.countryListView.setOnItemClickListener { _, _, position, _ ->
                val selectedCountry = countries[position]

                // Define the navigation action and navigate to CountryFragment
                val bundle = Bundle().apply {
                    putString("countryName", selectedCountry.name)
                }
                findNavController().navigate(R.id.countryFragment, bundle)

            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

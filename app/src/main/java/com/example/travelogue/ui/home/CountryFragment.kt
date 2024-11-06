// CountryFragment.kt
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
import com.example.travelogue.databinding.FragmentCountryBinding

class CountryFragment : Fragment() {

    private var _binding: FragmentCountryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var journalAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCountryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        // Retrieve country name from arguments
        val countryName = arguments?.getString("countryName")

        // Observe countriesLiveData and filter journals for the selected country
        viewModel.countriesLiveData.observe(viewLifecycleOwner) { countries ->
            val selectedCountry = countries.find { it.name == countryName }
            val journalTitles = selectedCountry?.journals?.map { it.title } ?: listOf()

            // Set up journal adapter
            journalAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, journalTitles)
            binding.journalListView.adapter = journalAdapter
            binding.addJournalButton.setOnClickListener {
                findNavController().navigate(R.id.action_countryFragment_to_addJournalFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// CountryFragment.kt
package com.example.travelogue.ui.home
import com.example.travelogue.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.travelogue.databinding.FragmentCountryBinding
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.table_country.CountryDao
import com.example.travelogue.table_country.CountryRepository
import com.example.travelogue.table_country.CountryViewModel
import com.example.travelogue.table_country.CountryViewModelFactory
import com.example.travelogue.table_journal.Journal
import com.example.travelogue.table_journal.JournalDao
import com.example.travelogue.table_journal.JournalRepository
import com.example.travelogue.table_journal.JournalViewModel
import com.example.travelogue.table_journal.JournalViewModelFactory

class CountryFragment : Fragment() {

    private var _binding: FragmentCountryBinding? = null
    private val binding get() = _binding!!

    private lateinit var journalAdapter: ArrayAdapter<String>

    private lateinit var database: UserDatabase
    private lateinit var databaseDao: JournalDao
    private lateinit var repository: JournalRepository
    private lateinit var viewModelFactory: JournalViewModelFactory
    private lateinit var journalViewModel: JournalViewModel

    private lateinit var arrayList: ArrayList<Journal>
    private lateinit var arrayAdapter: JournalListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCountryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize for DB interaction
        database = UserDatabase.getInstance(requireActivity())
        databaseDao = database.journalDao
        repository = JournalRepository(databaseDao)
        viewModelFactory = JournalViewModelFactory(repository)
        journalViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(JournalViewModel::class.java)

        // Retrieve country name & ID from arguments and set title
        val countryName = arguments?.getString("countryName")
        val countryID = arguments?.getLong("countryID")
        println("debug: countryID = ${countryID}, countryName = ${countryName}")
        val countryTitle = view.findViewById<TextView>(R.id.countryTitleTextView)
        countryTitle.text = countryName

        // list adapter
        // Set up journal adapter
        arrayList = ArrayList()
        arrayAdapter = JournalListAdapter(requireActivity(), arrayList)
        binding.journalListView.adapter = arrayAdapter

        // Observe countriesLiveData and filter journals for the selected country
        journalViewModel.getJournalsByCountry(countryID!!).observe(viewLifecycleOwner) { journals ->
            println("debug: journals.size = ${journals.size}")
            arrayAdapter.replace(journals)
            arrayAdapter.notifyDataSetChanged()
        }

        // On click for add journal
        binding.addJournalButton.setOnClickListener {
            val bundle = Bundle().apply {
                putLong("countryID", countryID)
                putString("countryName", countryName)
            }
            findNavController().navigate(R.id.action_countryFragment_to_addJournalFragment, bundle)
        }

        // set on click for journal item in listview
        binding.journalListView.setOnItemClickListener { adapterView, view, position, l ->
            // once database is working you would probably pass the journal ID to the fragment so it can retrieve the right journal
            val journal = arrayAdapter.getItem(position)

            // for now, pass journalTitle to viewJournalFragment
            val bundle = Bundle().apply {
                putLong("journalID", journal.journalId)
                putString("title", journal.title)
                putFloat("rating", journal.rating)
                putString("content", journal.content)
                putString("photoUri", journal.photoUri)
            }

            findNavController().navigate(R.id.viewJournalFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.travelogue.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.travelogue.R
import com.example.travelogue.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var arrayList: ArrayList<Map<String, String>>
    private lateinit var arrayAdapter: JournalEntriesListAdapter
    private lateinit var journalEntriesListView: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set up viewmodel
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        //locate elements
        journalEntriesListView = view.findViewById(R.id.Journals)

        // set up list adapter (fake data for now)
        arrayList = ArrayList()
        arrayAdapter = JournalEntriesListAdapter(requireActivity(), arrayList)
        journalEntriesListView.adapter = arrayAdapter

        // Update listview if something is added or removed
        homeViewModel.allJournalEntriesLiveData.observe(requireActivity(), Observer { it ->
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
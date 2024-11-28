package com.example.travelogue.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.travelogue.R
import com.example.travelogue.Util
import com.example.travelogue.databinding.FragmentHomeBinding
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.table_country.CountryDao
import com.example.travelogue.table_country.CountryRepository
import com.example.travelogue.table_country.CountryViewModel
import com.example.travelogue.table_country.CountryViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var database: UserDatabase
    private lateinit var databaseDao: CountryDao
    private lateinit var repository: CountryRepository
    private lateinit var viewModelFactory: CountryViewModelFactory
    private lateinit var countryViewModel: CountryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize for DB interaction
        database = UserDatabase.getInstance(requireActivity())
        databaseDao = database.countryDao
        repository = CountryRepository(databaseDao)
        viewModelFactory = CountryViewModelFactory(repository)
        countryViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(CountryViewModel::class.java)
    }

    data class MarkerInfo(val countryId: Long, val countryName: String)

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        val style = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
        mMap.setMapStyle(style)

        // Observe the LiveData from the ViewModel
        countryViewModel.getCountriesByUserId(Util.getUserId(requireContext())).observe(viewLifecycleOwner) { countries ->
           countries.forEach { country ->
               val marker = googleMap.addMarker(
                   MarkerOptions()
                       .position(LatLng(country.countryLat, country.countryLng))
                       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
               )
               marker?.tag = MarkerInfo(countryId = country.country_id, countryName = country.countryName)
           }
            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations[1], 2f))
        }

        // on click for pin
        mMap.setOnMarkerClickListener { clickedMarker ->
            val markerInfo = clickedMarker.tag as MarkerInfo
            // Define the navigation action and navigate to CountryFragment
            val bundle = Bundle().apply {
                putString("countryName", markerInfo.countryName)
                putLong("countryID", markerInfo.countryId)
            }
            findNavController().navigate(R.id.countryFragment, bundle)
            false
        }
    }
}
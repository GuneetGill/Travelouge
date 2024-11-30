package com.example.travelogue.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.travelogue.R
import com.example.travelogue.Util
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.table_country.CountryDao
import com.example.travelogue.table_country.CountryRepository
import com.example.travelogue.table_country.CountryViewModel
import com.example.travelogue.table_country.CountryViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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
    private var pinLocations: MutableList<LatLng> = mutableListOf()
    private var currentZoomedCountry: Int = 0

    private lateinit var goToCountryBtn: Button

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

        // UI elements
        goToCountryBtn = view.findViewById(R.id.goToCountry)
    }

    data class MarkerInfo(val countryId: Long, val countryName: String)

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        val style = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
        mMap.setMapStyle(style)

        // Observe the LiveData from the ViewModel
        countryViewModel.getCountriesByUserId(Util.getUserId(requireContext())).observe(viewLifecycleOwner) { countries ->
            pinLocations.clear()
           countries.forEach { country ->
               val countryLocation = LatLng(country.countryLat, country.countryLng)
               val marker = googleMap.addMarker(
                   MarkerOptions()
                       .position(countryLocation)
                       .icon(Util.bitmapDescriptorFromVector(resources, R.drawable.baseline_location_pin_24))
               )
               pinLocations.add(countryLocation)
               marker?.tag = MarkerInfo(countryId = country.country_id, countryName = country.countryName)
           }
            currentZoomedCountry = pinLocations.lastIndex

            // enable goToCountryBtn if countries exist
            if (pinLocations.size != 0) {
                goToCountryBtn.isEnabled = true
                goToCountryBtn.alpha = 1f
            }
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

        // enable goToCountryBtn if countries exist
        if (pinLocations.size != 0) {
            goToCountryBtn.isEnabled = true
            goToCountryBtn.alpha = 1f
        }

        // on click for find country button
        goToCountryBtn.setOnClickListener {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pinLocations[currentZoomedCountry], 5f))
            iterateCurrentZoomedCountry()
        }

    }

    // iterate current zoomed country
    private fun iterateCurrentZoomedCountry() {
        currentZoomedCountry = (currentZoomedCountry + 1) % pinLocations.size
    }

}
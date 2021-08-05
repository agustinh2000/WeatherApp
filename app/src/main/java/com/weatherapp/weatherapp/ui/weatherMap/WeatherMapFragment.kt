package com.weatherapp.weatherapp.ui.weatherMap

import android.content.ContentValues.TAG
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import com.weatherapp.weatherapp.R
import com.weatherapp.weatherapp.api.model.weather.WeatherResponse
import com.weatherapp.weatherapp.databinding.FragmentWeatherMapBinding
import com.weatherapp.weatherapp.repository.implementation.WeatherRepository
import com.weatherapp.weatherapp.ui.home.model.Parameter
import com.weatherapp.weatherapp.ui.weatherMap.adapter.WeatherMapAdapter
import com.weatherapp.weatherapp.ui.weatherMap.viewmodel.WeatherMapViewModel
import com.weatherapp.weatherapp.ui.weatherMap.viewmodel.WeatherMapViewModelFactory

class WeatherMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private var _binding: FragmentWeatherMapBinding? = null
    private val binding get() = _binding!!
    private var map: GoogleMap? = null
    private var lastKnownLocation: Location? = null
    private val DEFAULT_ZOOM = 13
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var parametersForGetWeather: Parameter = Parameter()
    private val weatherMapVM by viewModels<WeatherMapViewModel> {
        WeatherMapViewModelFactory(WeatherRepository())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherMapBinding.inflate(inflater, container, false)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        val mMapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().add(R.id.map, mMapFragment).commit()
        mMapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        initHourlyWeatherRecyclerView()
        map = googleMap
        map!!.setOnMapClickListener(this);
        updateLocationUI()
        getDeviceLocation()
        setUpWeather(binding.root)
    }

    private fun initHourlyWeatherRecyclerView(){
        binding.rvHourlyWeather.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    private fun setUpWeather(view: View) {
        weatherMapVM.fetchWeather.observe(viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                val weather = response.body() as WeatherResponse
                binding.rvHourlyWeather.adapter = WeatherMapAdapter(weather.hourlyWeather.take(24).toTypedArray())
                updateUI(weather)
            } else {
                println(response.code())
            }
        })
    }

    private fun updateUI(weather: WeatherResponse) {
        val zone = weather.timeZone.split("/")
        binding.tvLocation.text = zone.last()
        binding.tvTemp.text = "${weather.currentWeather.temperature.toInt()} °C"
        binding.tvDescription.text = weather.currentWeather.weatherDescription[0].detailedDescription.uppercase()
        val icon = weather.currentWeather.weatherDescription[0].icon
        val imageUrl = "https://openweathermap.org/img/w/$icon.png"
        Picasso.get().load(imageUrl).into(binding.ivIconWeather)
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            map?.isMyLocationEnabled = true
            map?.uiSettings?.isMyLocationButtonEnabled = true
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                ), DEFAULT_ZOOM.toFloat()
                            )
                        )
                        parametersForGetWeather.longitude = lastKnownLocation!!.longitude.toFloat()
                        parametersForGetWeather.latitude = lastKnownLocation!!.latitude.toFloat()
                        weatherMapVM.setParameters(parametersForGetWeather)
                    }
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.")
                    Log.e(TAG, "Exception: %s", task.exception)
                    map?.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                    )
                    map?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    override fun onMapClick(point: LatLng) {
        parametersForGetWeather.longitude = point.longitude.toFloat()
        parametersForGetWeather.latitude = point.latitude.toFloat()
        weatherMapVM.setParameters(parametersForGetWeather)
        map!!.clear()
        map!!.addMarker(
            MarkerOptions()
                .position(point)
        )
        map!!.moveCamera(CameraUpdateFactory.newLatLng(point))
    }
}
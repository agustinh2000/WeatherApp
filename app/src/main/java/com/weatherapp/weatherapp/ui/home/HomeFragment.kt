package com.weatherapp.weatherapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import com.weatherapp.weatherapp.api.model.weather.WeatherResponse
import com.weatherapp.weatherapp.databinding.FragmentHomeBinding
import com.weatherapp.weatherapp.repository.implementation.WeatherRepository
import com.weatherapp.weatherapp.ui.home.adapter.WeatherAdapter
import com.weatherapp.weatherapp.ui.home.model.Parameter
import com.weatherapp.weatherapp.ui.home.viewmodel.HomeViewModel
import com.weatherapp.weatherapp.ui.home.viewmodel.HomeViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val PERMISSION_LOCATION_REQUEST_CODE = 1
    }

    private var _binding: FragmentHomeBinding? = null

    private var parametersForGetWeather: Parameter = Parameter()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var lastKnownLocation: Location? = null

    private val homeVM by viewModels<HomeViewModel> {
        HomeViewModelFactory(WeatherRepository())
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        getDeviceLocation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWeeklyWeatherRecyclerView()
        setUpWeather(view)
    }

    private fun initWeeklyWeatherRecyclerView(){
        binding.rvTimeOfTheWeek.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun setUpWeather(view: View) {
        homeVM.fetchWeather.observe(viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                val weather = response.body() as WeatherResponse
                binding.rvTimeOfTheWeek.adapter = WeatherAdapter(weather.weeklyWeather)
                updateUI(weather)
            } else {
                println(response.code())
            }
        })
    }

    private fun updateUI(weather: WeatherResponse) {
        val zone = weather.timeZone.split("/")
        val today = weather.currentWeather.dateTime
        val sunrise = weather.currentWeather.sunrise
        val sunset = weather.currentWeather.sunset
        val humidity = weather.currentWeather.humidity
        val windSpeed = weather.currentWeather.windSpeed
        val pressure = weather.currentWeather.pressure
        binding.tvDate.text =
            SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH).format(Date(today * 1000))
        binding.tvCity.text = zone.last()
        binding.tvTemperature.text = "${weather.currentWeather.temperature} °C"
        binding.tvSunriseTime.text =
            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
        binding.tvSunsetTime.text =
            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
        binding.tvHumidityData.text = "${humidity} %"
        binding.tvWindData.text = "${windSpeed} m/s"
        binding.tvPressureData.text = "${pressure} hPa"
        val icon = weather.currentWeather.weatherDescription[0].icon
        val imageUrl = "https://openweathermap.org/img/w/$icon.png"
        Picasso.get().load(imageUrl).into(binding.ivWeatherIcon)
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        if (hasLocationPermission()) {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnSuccessListener { location ->
                parametersForGetWeather.longitude = location.longitude.toFloat()
                parametersForGetWeather.latitude = location.latitude.toFloat()
                homeVM.setParameters(parametersForGetWeather)
            }
        }
        else{
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This application cannot work without Location Permission.",
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestLocationPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(
            requireContext(),
            "Permission Granted!",
            Toast.LENGTH_SHORT
        ).show()
        getDeviceLocation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
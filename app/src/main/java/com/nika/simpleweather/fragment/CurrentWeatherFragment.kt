package com.nika.simpleweather.fragment

import androidx.fragment.app.Fragment
import com.nika.simpleweather.R

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nika.simpleweather.adapter.WeatherAdapter
import com.nika.simpleweather.constants.Util.Companion.ICON
import com.nika.simpleweather.constants.Util.Companion.PNG
import com.nika.simpleweather.databinding.CurrentWeatherFragmentBinding
import com.nika.simpleweather.mvvm.Resource
import com.nika.simpleweather.mvvm.WeatherRepository
import com.nika.simpleweather.mvvm.WeatherViewModel
import com.nika.simpleweather.mvvm.WeatherViewModelProviderFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CurrentWeatherFragment():Fragment(R.layout.current_weather_fragment) {

    lateinit var viewmodel:WeatherViewModel
    lateinit var rvAdapter: WeatherAdapter
    lateinit var binding:CurrentWeatherFragmentBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weatherRepository=WeatherRepository()
        val viewModelFactory=WeatherViewModelProviderFactory(weatherRepository)
        viewmodel=ViewModelProvider(requireActivity(),viewModelFactory)[WeatherViewModel::class.java]

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())



        viewLifecycleOwner.lifecycleScope.launch {
            delay(500L)
            observeCurentWeatherLiveData()
        }

        setUpRecyclerView()



    }

    private fun setUpRecyclerView() {

        rvAdapter=WeatherAdapter()
        binding.recyclerView.apply {
            adapter= rvAdapter
            layoutManager=LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            observerForecastLivedata()
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= CurrentWeatherFragmentBinding.inflate(inflater)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        getLastKnownLocation()
        requestLocation()
    }

    private fun requestLocation() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){
            getLastKnownLocation()
        }else{
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                viewmodel.getCurrentWeather(latitude,longitude)


                Toast.makeText(requireContext(), "Latitudine: $latitude, Longitudine: $longitude", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "cannot get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observerForecastLivedata(){

        viewmodel.forecastWeather.observe(viewLifecycleOwner, Observer { resource->

            when(resource){
                is Resource.Success ->{
                    val forecastWeather=resource.data
                    forecastWeather?.let {
                        rvAdapter.differ.submitList(forecastWeather.forecast.forecastday.get(0).hour)
                    }
                }
                is Resource.Error -> {

                    Toast.makeText(requireContext(), resource.message , Toast.LENGTH_SHORT).show()

                }
            }

        })
    }

    private  fun observeCurentWeatherLiveData(){
        viewmodel.currentWeather.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    val currentWeatherResponse = resource.data
                    currentWeatherResponse?.let { currentWeather->
                        viewmodel.getForecast(currentWeather.name)
                        binding.tvTempVal.text = currentWeather.main?.temp.toString()
                        binding.tvDescription.text=currentWeather.weather?.get(0)?.description.toString() ?: ""
                        binding.tvMax.text=" Max   ${currentWeather.main?.feelsLike}"
                        binding.tvMin.text=" Min:  ${currentWeather.main?.tempMin}"
                        binding.tvFeelsLike.text="Feels like : ${currentWeather.main.feelsLike}"
                        binding.tvName.text=currentWeather.name
                        val weatherIconUrl = ICON + currentWeather.weather?.get(0)?.icon + PNG
                            Glide.with(requireContext())
                                .load(weatherIconUrl)
                                .error(R.drawable.baseline_error_24)
                                .into(binding.icon)


                    }
                }
                is Resource.Error -> {

                    Toast.makeText(requireContext(), resource.message , Toast.LENGTH_SHORT).show()

                }

            }
        })

    }










}
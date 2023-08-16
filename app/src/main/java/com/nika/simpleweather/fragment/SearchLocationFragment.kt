

package com.nika.simpleweather

import android.app.DatePickerDialog

    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
    import com.nika.simpleweather.adapter.SearchAdapter
    import com.nika.simpleweather.databinding.SearchLocationFragmentBinding
    import com.nika.simpleweather.mvvm.Resource
    import com.nika.simpleweather.mvvm.WeatherRepository
    import com.nika.simpleweather.mvvm.WeatherViewModel
    import com.nika.simpleweather.mvvm.WeatherViewModelProviderFactory
import kotlinx.coroutines.*

import java.text.SimpleDateFormat
    import java.util.*
import java.util.concurrent.Executors
import kotlin.streams.toList

class SearchLocationFragment : Fragment() {


    val asd = MutableLiveData<String>()







     



        private lateinit var binding: SearchLocationFragmentBinding
        private lateinit var viewmodel: WeatherViewModel
        private lateinit var rvAdapter: SearchAdapter
        private var searchJob: Job? = null
        private var choicedCalendar: String? = null
        var city:String?=null

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding = SearchLocationFragmentBinding.bind(view)

            val weatherRepository = WeatherRepository()
            val viewModelFactory = WeatherViewModelProviderFactory(weatherRepository)
            viewmodel = ViewModelProvider(requireActivity(), viewModelFactory)[WeatherViewModel::class.java]

            setUpRecyclerView()
            observeForSearchLiveData()

            val myCalendar = Calendar.getInstance()
            val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                makeSearch(myCalendar)

            }



            asd.observe(viewLifecycleOwner){ valami ->
                print(valami)
            }

            binding.imageView.setOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    datePicker,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }


            binding.etSearch.addTextChangedListener {text ->
                searchJob?.cancel()
                searchJob= MainScope().launch {
                    delay(500L)
                    if (text?.isNotEmpty()!!){
                        city=text.toString()
                    }
                }
            }





        }

        private fun makeToast() {
            Toast.makeText(requireContext(), "$choicedCalendar $city", Toast.LENGTH_SHORT).show()
        }

   private fun makeSearch(myCalendar: Calendar) {
            val myFormat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
            choicedCalendar = sdf.format(myCalendar.time)
            makeToast()

            if (city?.isNotEmpty() == true && choicedCalendar != null) {
                viewmodel.getForSearch(city!!, choicedCalendar!!)
            } else {
                Toast.makeText(requireContext(), "Please enter city and select date", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = SearchLocationFragmentBinding.inflate(inflater, container, false)
            return binding.root
        }

        private fun observeForSearchLiveData() {
            viewmodel.searchedWeather.observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { searchResponse ->
                            rvAdapter.differ.submitList(searchResponse.forecast.forecastday.get(0).hour)
                        }
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.e("?????????", " An error occurred: $message")
                        }
                    }
                }
            })
        }

        private fun setUpRecyclerView() {

            rvAdapter= SearchAdapter()
            binding.rvSearchNews.apply {
                adapter = rvAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }

        }


    }

package com.nika.simpleweather.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nika.simpleweather.databinding.ItemsBinding
import com.nika.simpleweather.repository.current.Hour
import java.util.*

class WeatherAdapter():RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>(){

    private var currentListSize: Int? = null

    inner class WeatherViewHolder( val binding: ItemsBinding) : ViewHolder(binding.root)

    private val differCallBack=object : DiffUtil.ItemCallback<Hour>(){
        override fun areItemsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return  oldItem.time==newItem.time
        }

        override fun areContentsTheSame(oldItem: Hour, newItem: Hour): Boolean {

            return oldItem==newItem
        }
    }
    val differ = AsyncListDiffer(this , differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding=ItemsBinding.inflate(LayoutInflater.from(parent.context) , parent, false)

        return WeatherViewHolder(binding)
    }

    override fun getItemCount(): Int {

        if (currentListSize==null){
        return   differ.currentList.size
            }
        else{
            return currentListSize as Int
        }
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weatherList = differ.currentList
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)


        val filteredList = weatherList.filter { hour ->
            val hourValueFromResponse = hour.time.substring(11, 13).toInt()
            val isSameDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == hour.time.substring(8, 10).toInt()

            isSameDay && hourValueFromResponse >= currentHour && hourValueFromResponse <= 23
        }

        if (filteredList.isNotEmpty() && position < filteredList.size) {
            val weatherHour = filteredList[position]
            val hour = weatherHour.time.substring(11, 16)

            val binding = holder.binding
            holder.itemView.apply {
                binding.tvTemp.text = weatherHour.tempC.toString()
                binding.tvTime.text = hour
                binding.tvChanceToRain.text = weatherHour.chanceOfRain.toString()
            }
        } else {

            return
        }
    }
}


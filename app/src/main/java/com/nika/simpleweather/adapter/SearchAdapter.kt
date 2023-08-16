package com.nika.simpleweather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nika.simpleweather.databinding.ItemsBinding
import com.nika.simpleweather.databinding.SearchedItemsBinding
import com.nika.simpleweather.repository.current.Hour
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SearchAdapter: RecyclerView.Adapter<SearchAdapter.SearchedWeatherViewHolder>(){

    private var currentListSize: Int? = null

    val scope = CoroutineScope(Dispatchers.Main)

    fun x(){
        scope.launch {

        }
    }

    inner class SearchedWeatherViewHolder( val binding: SearchedItemsBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallBack=object : DiffUtil.ItemCallback<Hour>(){
        override fun areItemsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return  oldItem.time==newItem.time
        }

        override fun areContentsTheSame(oldItem: Hour, newItem: Hour): Boolean {

            return oldItem==newItem
        }
    }
    val differ = AsyncListDiffer(this , differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedWeatherViewHolder {
        val binding=SearchedItemsBinding.inflate(LayoutInflater.from(parent.context) , parent, false)

        return SearchedWeatherViewHolder(binding)
    }



    override fun getItemCount(): Int {

            return   differ.currentList.size

    }


    override fun onBindViewHolder(holder: SearchedWeatherViewHolder, position: Int) {
        val weather=differ.currentList[position]
        val binding=holder.binding
        val time = weather.time.substring(11, 16)
        holder.itemView.apply {
            binding.tvTime.text=time
            binding.tvTemp.text=weather.tempC.toString()
            binding.tvFeelLike.text="Feels Like : ${weather.feelslikeC}"
            binding.tvChanceToRain.text=weather.chanceOfRain.toString()
            binding.tvDescrption.text=weather.condition.text

        }
    }

}

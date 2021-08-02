package com.weatherapp.weatherapp.ui.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weatherapp.weatherapp.ui.helpers.BaseViewHolder

abstract class GenericAdapter<T>(private val dataList: Array<T>) :
    RecyclerView.Adapter<BaseViewHolder<T>>() {

    abstract fun setViewHolder(parent: ViewGroup): BaseViewHolder<T>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return setViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.render(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size
}
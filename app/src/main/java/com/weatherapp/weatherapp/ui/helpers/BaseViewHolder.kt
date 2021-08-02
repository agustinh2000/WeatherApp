package com.weatherapp.weatherapp.ui.helpers

import android.view.View
import androidx.recyclerview.widget.RecyclerView


abstract class BaseViewHolder<T>(containerView: View) : RecyclerView.ViewHolder(containerView) {
    abstract fun render(item: T)
}
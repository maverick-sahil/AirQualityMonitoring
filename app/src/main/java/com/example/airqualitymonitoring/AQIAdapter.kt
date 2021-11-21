package com.example.airqualitymonitoring

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class AQIAdapter : ListAdapter<AirQuality, AQIAdapter.ItemViewHolder>(OngoingMeetingsDiffCallback()) {

    var onItemClick: ((AirQuality) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AQIAdapter.ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_aqi, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AQIAdapter.ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: AirQuality) = with(itemView) {
            val textCity = this.findViewById<TextView>(R.id.textCity)
            val textAqi = this.findViewById<TextView>(R.id.textAqi)
            val textLastUpdated = this.findViewById<TextView>(R.id.textLastUpdated)
            val constraintParent = this.findViewById<ConstraintLayout>(R.id.constraintParent)
            textCity.text = item.city
            textAqi.text = String.format("%.2f", item.aqi)
            textLastUpdated.text = DateUtils.getRelativeTimeSpanString(item.lastUpdated.time)
            constraintParent.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    when (item.aqi.roundToInt()) {
                        in 0..50 -> R.color.good
                        in 51..100 -> R.color.satisfactory
                        in 101..200 -> R.color.moderate
                        in 201..300 -> R.color.poor
                        in 301..400 -> R.color.very_poor
                        else -> R.color.severe
                    }
                )
            )

            setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    class OngoingMeetingsDiffCallback : DiffUtil.ItemCallback<AirQuality>() {
        override fun areItemsTheSame(oldItem: AirQuality, newItem: AirQuality): Boolean {
            return oldItem.city == newItem.city &&
                oldItem.aqi == newItem.aqi &&
                oldItem.lastUpdated == newItem.lastUpdated
        }

        override fun areContentsTheSame(oldItem: AirQuality, newItem: AirQuality): Boolean {
            return oldItem == newItem
        }
    }
}
package com.example.miles.data

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.miles.R
import java.util.*

class ItemListAdapter(private val onActionIntent: (Intent) -> Unit) :
    ListAdapter<Item, ItemListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate( R.layout.layout_history_card_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            DiffCallback
        }
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = current.timestamp
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val formattedTimestamp = "$day.$month.$year"
        val hours = current.duration/3600
        val minutes = (current.duration % 3600) / 60
        val secondOfDuration = current.duration % 60
        val duration : String = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,secondOfDuration)
        holder.textView.text = formattedTimestamp
        holder.textView1.text = "${current.calories.toString()} Cals"
        holder.textView2.text = "${current.Distance.toString()} Mtr"
        holder.textView3.text = current.id.toString()
        holder.imageView.setImageResource(
            when(current.activity){
                "cy" -> {R.drawable.ic_baseline_directions_bike_24}
                "wa" -> {R.drawable.ic_baseline_directions_walk_24}
                else -> {R.drawable.ic_baseline_directions_run_24}
            }
        )
        holder.button.setOnClickListener {
            val activitySummary =
                "Date:${formattedTimestamp}"+
                        "\nTime:${hour}.${minute}.${second}"+
                        "\nCalories Burned:${current.calories} cals"+
                        "\nDistance:${current.Distance} mtr" +
                        "\nActivity:${
                            when (current.activity) {
                                "cy" -> "cycling"
                                "wa" -> "walking"
                                else -> "running"
                            }
                        }" +
                        "\nAverage Speed:${current.speed}" +
                        "\nDuration:${duration}"

            val intent = Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, "Detials Of Activity")
                .putExtra(Intent.EXTRA_TEXT, activitySummary)
            onActionIntent(intent)
        }
    }

    class ItemViewHolder(private var itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val textView : TextView = itemView.findViewById(R.id.textView2)
        val textView1 : TextView = itemView.findViewById(R.id.textView4)
        val textView2 : TextView = itemView.findViewById(R.id.textView3)
        val textView3 : TextView = itemView.findViewById(R.id.textView8)
        val imageView : ImageView = itemView.findViewById(R.id.imageView)
        val button : ImageButton = itemView.findViewById(R.id.imageButton)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
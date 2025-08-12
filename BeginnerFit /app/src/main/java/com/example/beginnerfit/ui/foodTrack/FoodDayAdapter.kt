package com.example.beginnerfit.ui.foodTrack

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.beginnerfit.MyApplication
import com.example.beginnerfit.R
import com.example.beginnerfit.databinding.ItemFoodDayBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodDayAdapter(
    private var days: List<LocalDate>,
    private val onDayClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<FoodDayAdapter.DayViewHolder>() {

    inner class DayViewHolder(private val binding: ItemFoodDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(holder: DayViewHolder, date: LocalDate) {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
            binding.tvDate.text = formatter.format(date)

            val today = LocalDate.now()

            val context = holder.itemView.context

            val color = ContextCompat.getColor(context ,R.color.PastTextColor)

            setArrowColor(color)

            when {
                date.isBefore(today) -> {
                    applyTextStyle(binding.tvDate, R.style.FoodItemDateTextStylePast)
                    applyTextStyle(binding.tvSubtitle, R.style.FoodItemTitleTextStylePast)
                    binding.root.setBackgroundResource(R.drawable.workout_set_card_past)
                    binding.ivFoodIcon.alpha = 1f
                    setArrowColor(
                        ContextCompat.getColor(
                            context,
                            R.color.PastTextColor
                        )
                    )
                    binding.root.setOnClickListener {
                        onDayClick(date)
                    }

                }

                date.isEqual(today) -> {
                    applyTextStyle(binding.tvDate, R.style.FoodItemDateTextStyleToday)
                    applyTextStyle(binding.tvSubtitle, R.style.FoodItemTitleTextStyleToday)
                    binding.ivFoodIcon.alpha = 1f
                    setArrowColor(
                        ContextCompat.getColor(
                            context,
                            R.color.TodayTextColor
                        )
                    )
                    binding.root.setBackgroundResource(R.drawable.selected_food_card)
                    binding.root.setOnClickListener {
                        onDayClick(date)
                    }

                }

                date.isAfter(today) -> {
                    applyTextStyle(binding.tvDate, R.style.FoodItemDateTextStyleFuture)
                    applyTextStyle(binding.tvSubtitle, R.style.FoodItemTitleTextStyleFuture)
                    binding.ivFoodIcon.alpha = 0.3f
                    setArrowColor(
                        ContextCompat.getColor(
                            context,
                            R.color.FutureTextColor
                        )
                    )
                    binding.root.setBackgroundResource(R.drawable.workout_set_card)

                    binding.root.setOnClickListener {
                        Toast.makeText(binding.root.context,R.string.stat_tuned, Toast.LENGTH_SHORT).show()
                    }
                }
            }



        }

        private fun setArrowColor(color: Int) {
            val drawable = ContextCompat.getDrawable(
                MyApplication.getContext(),
                R.drawable.outline_chevron_right_24
            )

            if (drawable != null) {
                DrawableCompat.setTint(drawable, color)
            }
            binding.rightArrow.setImageDrawable(drawable)
        }

    }

    private fun applyTextStyle(textView: TextView, @StyleRes styleResId: Int) {
        textView.setTextAppearance(styleResId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemFoodDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(holder,days[position])
    }

    override fun getItemCount(): Int = days.size

    fun updateDays(newDays: List<LocalDate>) {
        days = newDays
        notifyDataSetChanged()
    }
}

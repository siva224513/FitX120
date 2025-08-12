package com.example.beginnerfit.ui.workoutTrack

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
import com.example.beginnerfit.databinding.WorkoutListItemBinding
import com.example.beginnerfit.model.WorkoutDay
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WorkoutListAdapter(
    private var week: List<WorkoutDay>,
    private val onDayClick: (WorkoutDay) -> Unit
) : RecyclerView.Adapter<WorkoutListAdapter.WorkoutViewHolder>() {

    private var selectedDate: LocalDate = LocalDate.now()

    inner class WorkoutViewHolder(val binding: WorkoutListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {



        fun bind(holder: WorkoutViewHolder, day: WorkoutDay) {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")



            binding.tvDayName.text = day.dayName
            binding.tvWorkoutTitle.text = day.title
            binding.tvWorkoutDate.text = formatter.format(day.date)
            binding.tvDayNumber.text = day.dayNumber.toString()


            val context = holder.itemView.context



            val  color = ContextCompat.getColor(context, R.color.PastTextColor)

            setArrowColor(color)

            when {
                day.date.isBefore(selectedDate) -> {
                    applyTextStyle(binding.tvDayNumber, R.style.WorkListItemDayNumberTextPast)
                    applyTextStyle(binding.tvDayName, R.style.WorkListItemDayTextPast)
                    applyTextStyle(binding.tvWorkoutTitle, R.style.WorkListItemWorkoutTextPast)
                    applyTextStyle(binding.tvWorkoutDate, R.style.WorkListItemDateTextPast)
                    binding.root.setBackgroundResource(R.drawable.workout_set_card_past)
                    binding.ivAvatar.alpha = 1f
                    setArrowColor(ContextCompat.getColor(context, R.color.PastTextColor))
                    binding.root.setOnClickListener {
                        val workoutDay = week[adapterPosition]
                        onDayClick(workoutDay)
                    }

                }

                day.date.isEqual(selectedDate) -> {
                    applyTextStyle(binding.tvDayNumber, R.style.WorkListItemDayNumberTextToday)
                    applyTextStyle(binding.tvDayName, R.style.WorkListItemDayTextToday)
                    applyTextStyle(binding.tvWorkoutTitle, R.style.WorkListItemWorkoutTextToday)
                    applyTextStyle(binding.tvWorkoutDate, R.style.WorkListItemDateTextToday)
                    binding.root.setBackgroundResource(R.drawable.workout_set_card_today)
                    binding.ivAvatar.alpha = 1f
                    setArrowColor(ContextCompat.getColor(context, R.color.TodayTextColor))
                    binding.root.setOnClickListener {
                        val workoutDay = week[adapterPosition]
                        onDayClick(workoutDay)
                    }


                }

                day.date.isAfter(selectedDate) -> {
                    applyTextStyle(binding.tvDayNumber, R.style.WorkListItemDayNumberTextFuture)
                    applyTextStyle(binding.tvDayName, R.style.WorkListItemDayTextFuture)
                    applyTextStyle(binding.tvWorkoutTitle, R.style.WorkListItemWorkoutTextFuture)
                    applyTextStyle(binding.tvWorkoutDate, R.style.WorkListItemDateTextFuture)
                    binding.root.setBackgroundResource(R.drawable.workout_set_card)
                    binding.ivAvatar.alpha = 0.3f
                    setArrowColor(ContextCompat.getColor(context, R.color.FutureTextColor))
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = WorkoutListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(holder,week[position])

    }

    override fun getItemCount(): Int = week.size

    fun updateDays(days: List<WorkoutDay>, selectedDate: LocalDate) {
        week = days
        this.selectedDate = selectedDate
        notifyDataSetChanged()
    }
}
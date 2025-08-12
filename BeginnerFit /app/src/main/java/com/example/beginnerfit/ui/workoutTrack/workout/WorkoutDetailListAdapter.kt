package com.example.beginnerfit.ui.workoutTrack.workout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.beginnerfit.R
import com.example.beginnerfit.databinding.ItemWorkoutDetailBinding
import com.example.beginnerfit.model.WorkOutDetail

class WorkoutDetailListAdapter(
    private var workouts: MutableList<WorkOutDetail>,
    private var onclick: (WorkOutDetail) -> Unit
) : RecyclerView.Adapter<WorkoutDetailListAdapter.WorkoutDetailListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutDetailListViewHolder {
        val binding =
            ItemWorkoutDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutDetailListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutDetailListViewHolder, position: Int) {
        holder.bind( workouts[position])
    }

    override fun getItemCount(): Int = workouts.size

    inner class WorkoutDetailListViewHolder(private val binding: ItemWorkoutDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind( workout: WorkOutDetail) {
            val context = binding.root.context

            binding.tvWorkoutTitle.text = workout.name

            binding.tvSetsReps.text = context.getString(
                R.string.sets_reps_format,
                workout.sets,
                context.getString(R.string.sets),
                workout.reps,
                context.getString(R.string.reps)
            )




            if (workout.isCompleted == 1) {
                binding.ivStatus.visibility = View.VISIBLE
                binding.ivStatus.setImageResource(R.drawable.check_complete_circle)
                binding.ivStatus.setColorFilter(ContextCompat.getColor(context, R.color.green))
            }
            //          else {
//                binding.ivStatus.setImageResource(R.drawable.check_incomplete_circle)
//                binding.ivStatus.setColorFilter(ContextCompat.getColor(context, R.color.gray))
//            }

            binding.root.setOnClickListener {
                onclick(workout)
            }
        }
    }


}

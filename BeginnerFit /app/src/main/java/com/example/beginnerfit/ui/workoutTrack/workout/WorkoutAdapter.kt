package com.example.beginnerfit.ui.workoutTrack.workout


import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.beginnerfit.R
import com.example.beginnerfit.databinding.ItemWorkoutProgressBinding

data class WorkoutSet(
    val set: Int,
    var kg: Double,
    var reps: Int,
    var weightUsed: Double = 0.0,
    var completedReps: Int = 0
)

class WorkoutAdapter(
    private val setList: List<WorkoutSet>
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    private var editable: Boolean = true

    inner class WorkoutViewHolder(private val binding: ItemWorkoutProgressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, data: WorkoutSet) {
            binding.apply {

                val context = root.context

                kgEditText.filters = arrayOf(InputFilter.LengthFilter(5))
                repsEditText.filters = arrayOf(InputFilter.LengthFilter(3))
                setLabel.text = root.context.getString(R.string.set_label_format, position + 1)
                targetKgTextView.text = data.kg.toString()
                targetRepsTextView.text = data.reps.toString()

                kgEditText.setText(
                    if (data.weightUsed >= 0.0) data.weightUsed.toString() else ""
                )

                repsEditText.setText(
                    if (data.completedReps >= 0) data.completedReps.toString() else ""
                )

                repsEditText.isEnabled = editable
                kgEditText.isEnabled = editable

                kgEditText.doAfterTextChanged {
                    val text = kgEditText.text.toString().trim()
                    if (text.isNotEmpty()) {
                        val value = text.toDoubleOrNull()
                        if (value == null || value < 0 || value>300) {
                            kgErrorTextView.text = context.getString(R.string.error_invalid_weight_kg)
                            kgErrorTextView.visibility = View.VISIBLE
                            data.weightUsed = 0.0
                        } else {
                            kgErrorTextView.visibility = View.GONE
                            data.weightUsed = value
                        }
                    }
                }

                repsEditText.doAfterTextChanged {
                    val text = repsEditText.text.toString().trim()
                    if (text.isNotEmpty()) {
                        val value = text.toIntOrNull()
                        if (value == null || value < 0|| value >30) {
                            repsErrorTextView.text = context.getString(R.string.error_invalid_reps)
                            repsErrorTextView.visibility = View.VISIBLE
                            data.completedReps = 0
                        } else {
                            repsErrorTextView.visibility = View.GONE
                            data.completedReps = value
                        }
                    }
                }

            }
        }

    }


    fun setEditable(enabled: Boolean) {
        editable = enabled
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding =
            ItemWorkoutProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(position, setList[position])
    }

    override fun getItemCount(): Int = setList.size
}

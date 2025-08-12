import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.beginnerfit.MyApplication
import com.example.beginnerfit.R
import com.example.beginnerfit.databinding.CalendarItemBinding
import java.time.LocalDate


class CalendarAdapter(
    private var dates: List<LocalDate?>,
    private var selectedDate: LocalDate?,
    private val onDateSelected: (LocalDate) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val today: LocalDate = LocalDate.now()

    inner class CalendarViewHolder(val binding: CalendarItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CalendarItemBinding.inflate(inflater, parent, false)
        return CalendarViewHolder(binding)
    }

    override fun getItemCount(): Int = dates.size

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = dates[position]

        holder.binding.apply {
            if (date != null) {

                dayNumber.text = date.dayOfMonth.toString()
                dayNumber.visibility = View.VISIBLE

                root.setBackgroundResource(R.drawable.unselected_card_bg)

                val context = holder.itemView.context
                val textColor = ContextCompat.getColor(context, R.color.PastTextColor)



                dayNumber.setTextColor(textColor)

                if (date == today) {
                    root.setBackgroundResource(R.drawable.selected_card_bg)
                }

                if (date == selectedDate) {
                    root.setBackgroundResource(R.drawable.today_card_bg)
                    dayNumber.setTextColor( ContextCompat.getColor(context, R.color.TodayTextColor))
                }


                if (date.isAfter(today)) {
                    dayNumber.setTextColor(ContextCompat.getColor(context, R.color.FutureTextColor))
                    root.setOnClickListener(null)
                } else {
                    root.setOnClickListener {
                        selectedDate = date
                        notifyDataSetChanged()
                        onDateSelected(date)
                    }
                }

            } else {

                dayNumber.text = ""
                dayNumber.visibility = View.INVISIBLE
                root.setBackgroundResource(android.R.color.transparent)
                root.setOnClickListener(null)
            }
        }
    }


    fun updateDates(newDates: List<LocalDate?>) {
        dates = newDates
        notifyDataSetChanged()
    }
}

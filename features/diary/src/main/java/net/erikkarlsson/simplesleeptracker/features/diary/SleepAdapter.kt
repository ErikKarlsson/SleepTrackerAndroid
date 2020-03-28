package net.erikkarlsson.simplesleeptracker.features.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.erikkarlsson.simplesleeptracker.core.util.formatDateDisplayName
import net.erikkarlsson.simplesleeptracker.core.util.formatHHMM
import net.erikkarlsson.simplesleeptracker.core.util.formatHoursMinutes2
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.features.diary.databinding.ItemSleepBinding

class SleepAdapter(private val itemClick: (Sleep) -> Unit) : PagedListAdapter<Sleep, SleepAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSleepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindSleep(getItem(position))
    }

    class ViewHolder(private val binding: ItemSleepBinding, private val itemClick: (Sleep) -> Unit)
        : RecyclerView.ViewHolder(binding.root) {

        fun bindSleep(sleep: Sleep?) {
            val hours = sleep?.hours ?: 0f

            val imageResId = when {
                hours >= 7 && hours <= 9 -> R.drawable.ic_good_sleep
                hours < 6 || hours > 10 -> R.drawable.ic_bad_sleep
                else -> R.drawable.ic_so_so_sleep
            }

            binding.iconImage.setImageResource(imageResId)
            binding.hoursText.text = sleep?.hours?.formatHoursMinutes2
            binding.dateText.text = sleep?.toDate?.formatDateDisplayName
            binding.timeText.text = sleep?.fromDate?.formatHHMM
            binding.root.setOnClickListener {
                sleep?.let(itemClick)
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Sleep>() {
            override fun areItemsTheSame(oldItem: Sleep, newItem: Sleep): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Sleep, newItem: Sleep): Boolean =
                    oldItem == newItem
        }
    }

}

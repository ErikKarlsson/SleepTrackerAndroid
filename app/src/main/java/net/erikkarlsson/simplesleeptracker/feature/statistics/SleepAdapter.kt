package net.erikkarlsson.simplesleeptracker.feature.statistics

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_sleep.view.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.util.formatDateDisplayName
import net.erikkarlsson.simplesleeptracker.util.formatHHMM
import net.erikkarlsson.simplesleeptracker.util.formatHoursMinutes

class SleepAdapter(private val itemClick: (Sleep) -> Unit) : PagedListAdapter<Sleep, SleepAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sleep, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindSleep(getItem(position))
    }

    class ViewHolder(v: View, private val itemClick: (Sleep) -> Unit)
        : RecyclerView.ViewHolder(v) {

        fun bindSleep(sleep: Sleep?) {
            itemView.hoursText.text = sleep?.hours?.formatHoursMinutes
            itemView.dateText.text = sleep?.fromDate?.formatDateDisplayName
            itemView.timeText.text = sleep?.fromDate?.formatHHMM
            itemView.setOnClickListener {
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
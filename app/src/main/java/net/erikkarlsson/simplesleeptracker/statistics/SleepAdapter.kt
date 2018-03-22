package net.erikkarlsson.simplesleeptracker.statistics

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.common.collect.ImmutableList
import kotlinx.android.synthetic.main.item_sleep.view.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.util.formatDateDisplayName
import net.erikkarlsson.simplesleeptracker.util.formatHHMM
import net.erikkarlsson.simplesleeptracker.util.formatHoursMinutes

class SleepAdapter(private val itemClick: (Sleep) -> Unit) : RecyclerView.Adapter<SleepAdapter.ViewHolder>() {

    private var sleepList: ImmutableList<Sleep> = ImmutableList.of()

    override fun getItemCount(): Int = sleepList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sleep, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindSleep(sleepList[position])
    }

    fun swapData(sleepList: ImmutableList<Sleep>, diffResult: DiffUtil.DiffResult) {
        this.sleepList = sleepList
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(v: View, private val itemClick: (Sleep) -> Unit)
        : RecyclerView.ViewHolder(v) {

        fun bindSleep(sleep: Sleep) {
            itemView.hoursText.text = sleep.hours.formatHoursMinutes
            itemView.dateText.text = sleep.fromDate.formatDateDisplayName
            itemView.timeText.text = sleep.fromDate.formatHHMM
            itemView.setOnClickListener { itemClick(sleep) }
        }
    }

    class DiffCallback(private val oldSleepList: ImmutableList<Sleep>, private val newSleepList: ImmutableList<Sleep>)
        : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldSleepList.size

        override fun getNewListSize(): Int = newSleepList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldSleepList[oldItemPosition].id == newSleepList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldSleepList[oldItemPosition] == newSleepList[newItemPosition]
        }

    }
}
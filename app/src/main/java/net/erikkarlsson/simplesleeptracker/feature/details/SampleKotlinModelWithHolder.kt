package net.erikkarlsson.simplesleeptracker.feature.details

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.base.KotlinHolder

@EpoxyModelClass(layout = R.layout.view_holder_page_header)
abstract class SampleKotlinModelWithHolder : EpoxyModelWithHolder<Holder>() {

    @EpoxyAttribute
    lateinit var title: String

    override fun bind(holder: Holder) {
        holder.titleView.text = title
    }

}

class Holder : KotlinHolder() {
    val titleView by bind<TextView>(R.id.title)
}

package net.erikkarlsson.simplesleeptracker.features.diary.recycler

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import net.erikkarlsson.simplesleeptracker.core.util.px
import net.erikkarlsson.simplesleeptracker.features.diary.R
import javax.inject.Inject

private const val LEFT_PADDING_DP = 72

class SimpleDividerItemDecoration @Inject constructor(context: Context) : RecyclerView.ItemDecoration() {
    private val divider: Drawable

    init {
        divider = checkNotNull(ContextCompat.getDrawable(context, R.drawable.line_divider))
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = LEFT_PADDING_DP.px
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + divider.intrinsicHeight

            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }
}

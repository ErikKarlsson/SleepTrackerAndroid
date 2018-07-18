package net.erikkarlsson.simplesleeptracker.feature.diary

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import net.erikkarlsson.simplesleeptracker.R
import javax.inject.Inject

class SimpleDividerItemDecoration @Inject constructor(context: Context) : RecyclerView.ItemDecoration() {
    private val divider: Drawable

    init {
        divider = checkNotNull(ContextCompat.getDrawable(context, R.drawable.line_divider))
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        val left = parent.paddingLeft
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
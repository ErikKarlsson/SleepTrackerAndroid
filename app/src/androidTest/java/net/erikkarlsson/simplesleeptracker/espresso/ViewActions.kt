package net.erikkarlsson.simplesleeptracker.espresso

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.recyclerview.widget.RecyclerView

object ViewActions {

    fun typeInEditText(@IdRes editTextResId: Int, text: String) {
        onView(withId(editTextResId))
            .perform(closeSoftKeyboard(),
                    typeText(text),
                    closeSoftKeyboard())
    }

    fun clickViewWithId(@IdRes resId: Int) {
        onView(withId(resId)).perform(click())
    }

    fun clickViewWithText(withText: String) {
        onView(withText(withText)).perform(click())
    }

    fun clickRecyclerViewItemWithText(@IdRes recyclerViewResId: Int, text: String) {
        onView(withId(recyclerViewResId))
            .perform(RecyclerViewActions.actionOnItem<androidx.recyclerview.widget.RecyclerView.ViewHolder>(
                    hasDescendant(withText(text)), click()))
    }

    fun clickRecyclerViewItemAtPosition(@IdRes recyclerViewResId: Int, position: Int) {
        onView(withId(recyclerViewResId))
            .perform(RecyclerViewActions.actionOnItemAtPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(
                    position, click()))
    }

}

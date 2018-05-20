package net.erikkarlsson.simplesleeptracker.espresso

import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText

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
            .perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(text)), click()))
    }

    fun clickRecyclerViewItemAtPosition(@IdRes recyclerViewResId: Int, position: Int) {
        onView(withId(recyclerViewResId))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    position, click()))
    }

}

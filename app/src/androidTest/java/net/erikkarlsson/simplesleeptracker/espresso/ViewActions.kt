package net.erikkarlsson.simplesleeptracker.espresso

import android.support.annotation.IdRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.RecyclerView

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

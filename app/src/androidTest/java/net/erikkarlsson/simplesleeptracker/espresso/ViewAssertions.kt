package net.erikkarlsson.simplesleeptracker.espresso

import android.support.annotation.IdRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.view.View
import org.hamcrest.Matchers.not

object ViewAssertions {

    fun shouldDisplayView(@IdRes resId: Int) {
        onView(withId(resId)).check(matches(isDisplayed()))
    }

    fun shouldNotDisplayView(@IdRes resId: Int) {
        onView(withId(resId)).check(matches(not<View>(isDisplayed())))
    }

    fun shouldDisplayErrorText(@IdRes editTextResId: Int, text: String) {
        onView(withId(editTextResId))
            .check(matches(hasErrorText(text)))
    }
}

package net.erikkarlsson.simplesleeptracker.runner

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import net.erikkarlsson.simplesleeptracker.TestApp

class TestRunner : AndroidJUnitRunner() {
    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
    }
}

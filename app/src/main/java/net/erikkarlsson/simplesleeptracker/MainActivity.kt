package net.erikkarlsson.simplesleeptracker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.crashlytics.android.Crashlytics
import dagger.android.AndroidInjection
import io.fabric.sdk.android.Fabric
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import javax.inject.Inject

const val REQUEST_CODE_SIGN_IN = 1

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        Fabric.with(this, Crashlytics())

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.app_name)

        val host: NavHostFragment = supportFragmentManager
                .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController

        bottomNavigation.setupWithNavController(navController)
    }
}

class MainViewModel @Inject constructor(mainComponent: MainComponent) :
        ElmViewModel<MainState, MainMsg, MainCmd>(mainComponent)

package net.erikkarlsson.simplesleeptracker

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.crashlytics.android.Crashlytics
import dagger.android.AndroidInjection
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.feature.appwidget.AlarmBroadcastReciever
import javax.inject.Inject



const val REQUEST_CODE_SIGN_IN = 1

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var navController: NavController

    private lateinit var receiver: AlarmBroadcastReciever

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        Fabric.with(this, Crashlytics())
//        receiver = AlarmBroadcastReciever()

//        val intentFilter = IntentFilter()
//        intentFilter.addAction("android.app.action.NEXT_ALARM_CLOCK_CHANGED")
        /*
        intentFilter.addAction("com.sec.android.app.clockpackage.ClockPackage.ALARM_ALERT")
        intentFilter.addAction("com.sec.android.app.clockpackage.ClockPackage.ALARM_DISMISS")
        intentFilter.addAction("com.sec.android.app.clockpackage.alarm.ALARM_DISMISS")
        intentFilter.addAction("com.sec.android.app.clockpackage.ALARM_DISMISS")
        intentFilter.addAction("com.sec.android.app.clockpackage.ALARM_ALERT")
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        */
//        registerReceiver(receiver, intentFilter);



        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.app_name)

        val host: NavHostFragment = supportFragmentManager
                .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

        navController = host.navController

        setupWithNavController(bottomNavigation, navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onNavDestinationSelected(item, navController)
        return super.onOptionsItemSelected(item)
    }
}

class MainViewModel @Inject constructor(mainComponent: MainComponent) :
        ElmViewModel<MainState, MainMsg, MainCmd>(mainComponent)

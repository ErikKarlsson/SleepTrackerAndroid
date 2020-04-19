package net.erikkarlsson.simplesleeptracker

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.NavigationUI.setupWithNavController
import dagger.android.AndroidInjection
import net.erikkarlsson.simplesleeptracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController

    private lateinit var toolbar: Toolbar

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.app_name)

        val host: NavHostFragment = supportFragmentManager
                .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

        navController = host.navController

        setupWithNavController(binding.bottomNavigation, navController)
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

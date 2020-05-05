package net.erikkarlsson.simplesleeptracker

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.NavigationUI.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.data.draft.*
import net.erikkarlsson.simplesleeptracker.databinding.ActivityMainBinding
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController

    private lateinit var toolbar: Toolbar

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var draftContentDao: DraftContentDao

    @Inject
    lateinit var listingDraftDao: ListingDraftDao


    @Inject
    lateinit var imageDao: DraftImageDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.app_name)

        val host: NavHostFragment = supportFragmentManager
                .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

        navController = host.navController

        setupWithNavController(binding.bottomNavigation, navController)

        lifecycleScope.launch {
            createListingDraft()
        }
    }

    private suspend fun createListingDraft() {
        val listingDraft = ListingDraftEntity(templateName = "Lego template", dateCreated = "2020-02-20")

        val listingDraftId = listingDraftDao.insertListingDraft(listingDraft)

        val draftContent = DraftContentEntity(listingDraftId = listingDraftId, title = "Lego", description = "Batman")

        val draftContentId = draftContentDao.insertDraftContent(draftContent)

        imageDao.insertDraftImage(DraftImageEntity(draftContentId = draftContentId, url = "http://tradera.com/image.jpg"))
        imageDao.insertDraftImage(DraftImageEntity(draftContentId = draftContentId, url = "http://tradera.com/image2.jpg"))
        imageDao.insertDraftImage(DraftImageEntity(draftContentId = draftContentId, url = "http://tradera.com/image3.jpg"))

        val listingDraftResult = listingDraftDao.getListingDraft(listingDraftId)

        Timber.d("asdf")
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

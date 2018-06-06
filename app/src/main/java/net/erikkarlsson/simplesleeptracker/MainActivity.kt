package net.erikkarlsson.simplesleeptracker

import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.feature.main.MainCmd
import net.erikkarlsson.simplesleeptracker.feature.main.MainComponent
import net.erikkarlsson.simplesleeptracker.feature.main.MainMsg
import net.erikkarlsson.simplesleeptracker.feature.main.MainState
import net.erikkarlsson.simplesleeptracker.feature.main.SignInCancelled
import net.erikkarlsson.simplesleeptracker.feature.main.SignInFailed
import net.erikkarlsson.simplesleeptracker.feature.main.SignInSuccess
import timber.log.Timber
import javax.inject.Inject

const val REQUEST_CODE_SIGN_IN = 1

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.app_name)

        val host: NavHostFragment = supportFragmentManager
                .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController

        bottomNavigation.setupWithNavController(navController)

        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account == null) {
            signIn()
        } else {
            onSignInSuccess(account)
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            onSignInSuccess(account)
        } catch (e: ApiException) {
            when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
                    Timber.w("Sign in cancelled:failed code=" + e.statusCode)
                    onSignInCancelled()
                }
                GoogleSignInStatusCodes.SIGN_IN_FAILED -> {
                    Timber.w("Sign in failed:failed code=" + e.statusCode)
                    onSignInFailed()
                }
                else -> Timber.w("signInResult:failed code=" + e.statusCode)
            }
        }
    }

    private fun onSignInSuccess(account: GoogleSignInAccount) {
        viewModel.dispatch(SignInSuccess)
    }

    private fun onSignInCancelled() {
        viewModel.dispatch(SignInCancelled)
    }

    private fun onSignInFailed() {
        viewModel.dispatch(SignInFailed)
    }
}

class MainViewModel @Inject constructor(mainComponent: MainComponent) :
        ElmViewModel<MainState, MainMsg, MainCmd>(mainComponent)

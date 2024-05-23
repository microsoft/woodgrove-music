package com.woodgrove.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.microsoft.identity.nativeauth.statemachine.results.GetAccountResult
import com.microsoft.identity.nativeauth.statemachine.results.SignOutResult
import com.woodgrove.android.R
import com.woodgrove.android.databinding.ActivityHomeBinding
import com.woodgrove.android.ui.landing.LandingActivity
import com.woodgrove.android.utils.AuthClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initialiseTabs()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    private fun initialiseTabs() {
        val discoverFragment = DiscoverFragment()
        val feedFragment = FeedFragment()
        val myMusicFragment = MyMusicFragment()

        setFragment(discoverFragment, getString(R.string.title_discover))

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                com.woodgrove.android.R.id.discover -> setFragment(discoverFragment, it.title.toString())
                R.id.feed -> setFragment(feedFragment, it.title.toString())
                R.id.my_music -> setFragment(myMusicFragment, it.title.toString())
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                performLogout()
                true
            }
            R.id.profile -> {
                // Do nothing for now
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setFragment(fragment: Fragment, title: String) {
        supportActionBar?.title = title
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.content_fragment, fragment)
            commit()
        }
    }

    private fun performLogout() {
        val authClient = AuthClient.getAuthClient()
        CoroutineScope(Dispatchers.Main).launch {
            val accountState = authClient.getCurrentAccount()
            if (accountState is GetAccountResult.AccountFound) {
                val signOutResult = accountState.resultValue.signOut()
                if (signOutResult is SignOutResult.Complete) {
                    navigateToLanding()
                } else {
                    Toast.makeText(this@HomeActivity, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToLanding() {
        startActivity(LandingActivity.getStartIntent(this))
        finish()
    }
}
package com.woodgrove.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.woodgrove.android.R
import com.woodgrove.android.databinding.ActivityHomeBinding

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

    override fun onStart() {
        super.onStart()
        initializeButtonListeners()
    }

    private fun initialiseTabs() {
        val discoverFragment = DiscoverFragment()
        val feedFragment = FeedFragment()
        val myMusicFragment = MyMusicFragment()

        setFragment(discoverFragment, getString(R.string.title_discover))

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.discover -> setFragment(discoverFragment, it.title.toString())
                R.id.feed -> setFragment(feedFragment, it.title.toString())
                R.id.my_music -> setFragment(myMusicFragment, it.title.toString())
            }
            true
        }
    }

    private fun initializeButtonListeners() {

    }

    private fun setFragment(fragment: Fragment, title: String) {
        supportActionBar?.title = title
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.content_fragment, fragment)
            commit()
        }
    }
}
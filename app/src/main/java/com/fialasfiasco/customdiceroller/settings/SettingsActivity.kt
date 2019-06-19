package com.fialasfiasco.customdiceroller.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.fialasfiasco.customdiceroller.R
import kotlinx.android.synthetic.main.settings_activity.*

private const val TITLE_TAG = "settingsActivityTitle"

class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setSupportActionBar(mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.settings,
                    HeaderFragment()
                )
                .commit()
        } else {
            supportActionBar?.title = savedInstanceState.getCharSequence(TITLE_TAG)
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                supportActionBar?.title = getString(R.string.title_activity_settings)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, supportActionBar?.title)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        onBackPressed()
        return true
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {

        // Replace the existing Fragment with the new Fragment
        when(pref.fragment) {
            getString(R.string.header_preference_fragment) -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.settings,
                        HeaderFragment()
                    )
                    .addToBackStack(null)
                    .commit()
                supportActionBar?.title = pref.title
            }

            getString(R.string.shake_preference_fragment) -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.settings,
                        ShakeFragment()
                    )
                    .addToBackStack(null)
                    .commit()
                supportActionBar?.title = pref.title
            }

            getString(R.string.sound_preference_fragment) -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.settings,
                        SoundFragment()
                    )
                    .addToBackStack(null)
                    .commit()
                supportActionBar?.title = pref.title
            }
        }

        return true
    }
}

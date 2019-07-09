package com.fialasfiasco.customdiceroller

import android.content.ActivityNotFoundException
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.data.SectionsPagerAdapter
import com.fialasfiasco.customdiceroller.helper.AppLaunchResponder
import com.fialasfiasco.customdiceroller.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view_pager.adapter = SectionsPagerAdapter(this, supportFragmentManager)
        tabs.setupWithViewPager(view_pager)
        setSupportActionBar(mainToolbar)
        ViewModelProviders.of(this).get(PageViewModel::class.java).setContext(this)

        AppLaunchResponder(this).appLaunched()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.clearHistory -> {
                ViewModelProviders.of(this).get(PageViewModel::class.java).clearHistory()
                val text = "History Cleared"
                val duration = Toast.LENGTH_SHORT
                Toast.makeText(this, text, duration).show()
                true
            }
            R.id.settings_item -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingsIntent)
                true
            }
            R.id.aboutItem -> {
                val aboutIntent = Intent(this, AboutActivity::class.java)
                startActivity(aboutIntent)
                true
            }
            R.id.rateThisApp ->
            {
                val rateIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID))
                try {
                    startActivity(rateIntent)
                }
                catch (error : ActivityNotFoundException)
                {
                    val errorDialog = AlertDialog.Builder(this)
                    errorDialog.setTitle("Error occurred while opening Play Store")
                    errorDialog.show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart()
    {
        // Update all of the saved settings into the PageViewModel
        val pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val diePool = preferences.getStringSet(
            getString(R.string.dice_pool_key),
            resources.getStringArray(R.array.dice_pool_entries).toSet()
        )

        pageViewModel.initDiePoolFromStrings(diePool)

        pageViewModel.setShakeEnabled(preferences.getBoolean(
            getString(R.string.shake_enabled_key),
            resources.getBoolean(R.bool.shake_enabled_default)
        ))

        val savedShakeSensitivity = preferences.getInt(
            getString(R.string.shake_sensitivity_key),
            resources.getInteger(R.integer.shake_sensitivity_default)
        ).toFloat()
        pageViewModel.setShakeSensitivity(12f - savedShakeSensitivity / 10f)

        val savedShakeDuration = preferences.getInt(
            getString(R.string.shake_duration_key),
            resources.getInteger(R.integer.shake_duration_default)
        )
        pageViewModel.setShakeDuration(500 + savedShakeDuration * 5)

        val savedHoldDuration = preferences.getInt(
            getString(R.string.hold_duration_key),
            resources.getInteger(R.integer.hold_duration_default)
        )
        pageViewModel.setHoldDuration(500 + savedHoldDuration * 5)

        pageViewModel.setSortType(preferences.getString(
            getString(R.string.sort_type_key),
            getString(R.string.sort_type_default)
        )!!.toInt())

        pageViewModel.setShowAverageRollResult(preferences.getBoolean(
            getString(R.string.show_dice_roll_average_enable_key),
            resources.getBoolean(R.bool.show_dice_roll_average_enable_default)
        ))

        pageViewModel.setSoundEnabled(preferences.getBoolean(
            getString(R.string.sound_enabled_key),
            resources.getBoolean(R.bool.sound_enabled_default)
        ))

        val intVolume = preferences.getInt(
            getString(R.string.sound_volume_key),
            resources.getInteger(R.integer.sound_volume_default)
        )
        pageViewModel.setVolume(intVolume.toFloat().div(100.0f))

        pageViewModel.setEditEnabled(preferences.getBoolean(
            getString(R.string.dice_edit_enabled_key),
            resources.getBoolean(R.bool.dice_edit_enabled_default)
        ))

        pageViewModel.setItemsInRowSimple(preferences.getString(
            getString(R.string.items_per_row_simple_key),
            resources.getInteger(R.integer.items_per_row_simple_default).toString()
        )!!.toInt())

        pageViewModel.setItemsInRowAggregate(preferences.getString(
            getString(R.string.items_per_row_custom_key),
            resources.getInteger(R.integer.items_per_row_custom_default).toString()
        )!!.toInt())

        super.onStart()
    }
}

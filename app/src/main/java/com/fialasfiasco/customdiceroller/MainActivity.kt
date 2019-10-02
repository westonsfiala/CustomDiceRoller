package com.fialasfiasco.customdiceroller

import android.content.ActivityNotFoundException
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.fialasfiasco.customdiceroller.data.*
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.data.SectionsPagerAdapter
import com.fialasfiasco.customdiceroller.dice.Roll
import com.fialasfiasco.customdiceroller.helper.AppLaunchResponder
import com.fialasfiasco.customdiceroller.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SectionsPagerAdapter.tabUpdateListener {

    private lateinit var pageViewModel : PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_pager.adapter = SectionsPagerAdapter(this, supportFragmentManager, this)
        view_pager.currentItem = SIMPLE_ROLL_TAB_INDEX
        tabs.setupWithViewPager(view_pager)

        setSupportActionBar(mainToolbar)

        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java)

        setupObservers()

        AppLaunchResponder(this).appLaunched()
    }

    override fun tabUpdated() {

        val subTitle = when(view_pager.currentItem) {

            HISTORY_TAB_INDEX -> getString(R.string.history_subtitle)
            SIMPLE_ROLL_TAB_INDEX -> getString(R.string.simple_roller_subtitle)
            CUSTOM_ROLL_TAB_INDEX -> getString(R.string.custom_roller_subtitle)
            SAVED_ROLLS_TAB_INDEX -> getString(R.string.saved_rolls_subtitle)
            else -> ""
        }

        mainToolbar.subtitle = subTitle
    }

    private fun setupObservers() {
        // Notify about new items and then scroll to the top.
        pageViewModel.rollToEdit.observe(this, Observer<Roll> {roll ->
            if(roll != null) {
                view_pager.currentItem = CUSTOM_ROLL_TAB_INDEX
            }
        })


        pageViewModel.diePool.observe(this, Observer<Set<String>> {dieStrings ->
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)

            val prefEditor = preferences.edit()
            prefEditor.putStringSet(getString(R.string.dice_pool_key), dieStrings)
            prefEditor.apply()
        })

        pageViewModel.savedRollPool.observe(this, Observer<Set<String>> {rollStrings ->
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)

            val prefEditor = preferences.edit()
            prefEditor.putStringSet(getString(R.string.saved_roll_pool_key), rollStrings)
            prefEditor.apply()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val shakeItem = menu?.findItem(R.id.shakeMenuItem)
        shakeItem?.isChecked = pageViewModel.getShakeEnabled()

        val soundItem = menu?.findItem(R.id.soundMenuItem)
        soundItem?.isChecked = pageViewModel.getSoundEnabled()

        val critSoundsItem = menu?.findItem(R.id.critSoundItem)
        critSoundsItem?.isChecked = pageViewModel.getCritSoundEnabled()

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.clearHistory -> {
                val confirmMenu = PopupMenu(this, mainToolbar, Gravity.END)

                confirmMenu.menu.add("Clear History")

                confirmMenu.setOnMenuItemClickListener {
                    pageViewModel.clearHistory()
                    val text = "History Cleared"
                    val duration = Toast.LENGTH_SHORT
                    Toast.makeText(this, text, duration).show()
                    true
                }
                confirmMenu.show()
                true
            }
            R.id.shakeMenuItem -> {
                // Update all of the saved settings into the PageViewModel
                val preferences = PreferenceManager.getDefaultSharedPreferences(this)

                val newShakeEnable = !item.isChecked

                item.isChecked = newShakeEnable
                preferences.edit().putBoolean(getString(R.string.shake_enabled_key), newShakeEnable).apply()
                pageViewModel.setShakeEnabled(newShakeEnable)
                true
            }
            R.id.soundMenuItem -> {
                // Update all of the saved settings into the PageViewModel
                val preferences = PreferenceManager.getDefaultSharedPreferences(this)

                val newSoundEnable = !item.isChecked

                item.isChecked = newSoundEnable
                preferences.edit().putBoolean(getString(R.string.shake_sound_enabled_key), newSoundEnable).apply()
                pageViewModel.setSoundEnabled(newSoundEnable)
                true
            }
            R.id.critSoundItem -> {
                // Update all of the saved settings into the PageViewModel
                val preferences = PreferenceManager.getDefaultSharedPreferences(this)

                val newCritSoundEnable = !item.isChecked

                item.isChecked = newCritSoundEnable
                preferences.edit().putBoolean(getString(R.string.critical_roll_sound_key), newCritSoundEnable).apply()
                pageViewModel.setCritSoundEnabled(newCritSoundEnable)
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
                    Toast.makeText(this, "Error occurred while opening Play Store", Toast.LENGTH_LONG).show()
                }
                true
            }
            R.id.feedback ->
            {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.type = "text/plain"
                intent.data = Uri.parse("mailto:")
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@fialasfiasco.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback / Request")

                try {
                    //start email intent
                    startActivity(intent)
                }
                catch (e: Exception){
                    //if any thing goes wrong for example no email client application or any exception
                    //get and show exception message
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart()
    {
        // Update all of the saved settings into the PageViewModel
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val diePool = preferences.getStringSet(
            getString(R.string.dice_pool_key),
            setOf()
        )

        val errorRecoveryDiePool = preferences.getStringSet(
            getString(R.string.dice_pool_key),
            setOf()
        )

        if(diePool == null || diePool.isEmpty())
        {
            pageViewModel.resetDiePool()
        }
        else
        {
            val somethingFailed = pageViewModel.initDiePoolFromStrings(diePool)
            retroactiveAddFate(preferences, pageViewModel)

            if(somethingFailed) {
                val editor = preferences.edit()

                editor.putStringSet(getString(R.string.error_dice_pool_key), diePool)

                editor.apply()
            }
        }

        if(errorRecoveryDiePool != null && errorRecoveryDiePool.isNotEmpty()) {
            val somethingFailed = pageViewModel.attemptRecoveryOfDiePoolFromStrings(errorRecoveryDiePool)

            if(!somethingFailed) {
                val editor = preferences.edit()

                editor.putStringSet(getString(R.string.error_dice_pool_key), setOf())

                editor.apply()
            }
        }

        val savedRollPool = preferences.getStringSet(
            getString(R.string.saved_roll_pool_key),
            setOf()
        )

        val errorRecoverySavedRollPool = preferences.getStringSet(
            getString(R.string.error_saved_roll_pool_key),
            setOf()
        )

        if(savedRollPool == null || savedRollPool.isEmpty())
        {
            pageViewModel.resetSavedRollPool()
        }
        else
        {
            val somethingFailed = pageViewModel.initSavedRollPoolFromStrings(savedRollPool)

            if(somethingFailed) {
                val editor = preferences.edit()

                editor.putStringSet(getString(R.string.error_saved_roll_pool_key), savedRollPool)

                editor.apply()
            }
        }

        if(errorRecoverySavedRollPool != null && errorRecoverySavedRollPool.isNotEmpty()) {
            val somethingFailed = pageViewModel.attemptRecoveryOfSavedRollPoolFromStrings(errorRecoverySavedRollPool)

            if(!somethingFailed) {
                val editor = preferences.edit()

                editor.putStringSet(getString(R.string.error_saved_roll_pool_key), setOf())

                editor.apply()
            }
        }


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
        val shakeDurationSeconds = 0.5f + savedShakeDuration / 100f
        pageViewModel.setShakeDuration(shakeDurationSeconds)

        val savedHoldDuration = preferences.getInt(
            getString(R.string.hold_duration_key),
            resources.getInteger(R.integer.hold_duration_default)
        )
        val holdDurationSeconds = 0.5f + savedHoldDuration / 100f
        pageViewModel.setHoldDuration(holdDurationSeconds)

        pageViewModel.setSortType(preferences.getString(
            getString(R.string.sort_type_key),
            getString(R.string.sort_type_default)
        )!!.toInt())

        pageViewModel.setShowAverageRollResult(preferences.getBoolean(
            getString(R.string.show_dice_roll_average_enable_key),
            resources.getBoolean(R.bool.show_dice_roll_average_enable_default)
        ))

        pageViewModel.setRollPropertiesEnabled(preferences.getBoolean(
            getString(R.string.roll_properties_enable_key),
            resources.getBoolean(R.bool.roll_properties_enable_default)
        ))

        pageViewModel.setSoundEnabled(preferences.getBoolean(
            getString(R.string.shake_sound_enabled_key),
            resources.getBoolean(R.bool.sound_enabled_default)
        ))

        val intVolume = preferences.getInt(
            getString(R.string.sound_volume_key),
            resources.getInteger(R.integer.sound_volume_default)
        )
        pageViewModel.setVolume(intVolume.toFloat().div(100.0f))

        pageViewModel.setCritSoundEnabled(preferences.getBoolean(
            getString(R.string.critical_roll_sound_key),
            resources.getBoolean(R.bool.critical_roll_sound_default)
        ))

        pageViewModel.setEditEnabled(preferences.getBoolean(
            getString(R.string.dice_edit_enabled_key),
            resources.getBoolean(R.bool.dice_edit_enabled_default)
        ))

        pageViewModel.setItemsInRow(preferences.getString(
            getString(R.string.items_per_row_preference_key),
            resources.getInteger(R.integer.items_per_row_default).toString()
        )!!.toInt())

        if(preferences.getBoolean(
            getString(R.string.keep_screen_on_key),
            resources.getBoolean(R.bool.keep_screen_on_default)))
        {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        super.onStart()
    }

    // Make sure that the fate die is added to installs that existed before it was introduced.
    private fun retroactiveAddFate(preferences : SharedPreferences, pageViewModel: PageViewModel)
    {
        val possibleFateEncounter = preferences.getBoolean(getString(R.string.possible_fate_die_encounter_key), false)

        if(!possibleFateEncounter)
        {
            pageViewModel.addDieToPool(pageViewModel.fateDie)
            // We have now seen this before
            val editor = preferences.edit()
            editor.putBoolean(getString(R.string.possible_fate_die_encounter_key), true)
            editor.apply()
        }
    }
}

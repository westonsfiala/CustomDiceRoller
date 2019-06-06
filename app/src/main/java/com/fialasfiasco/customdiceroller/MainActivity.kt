package com.fialasfiasco.customdiceroller

import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.fialasfiasco.customdiceroller.ui.main.PageViewModel
import com.fialasfiasco.customdiceroller.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var shakeSensitivity = 4f
    private var shakeToRoll = false
    private var shakeDuration = 500
    private var holdDuration = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view_pager.adapter = SectionsPagerAdapter(this, supportFragmentManager)
        tabs.setupWithViewPager(view_pager)
        setSupportActionBar(settingsToolbar)
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
                val settingsIntent = Intent(this, Settings::class.java)
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
                startActivity(rateIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        setupPreferences()
    }

    private fun setupPreferences()
    {
        shakeToRoll = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            .getBoolean(getString(R.string.shake_preference_key), DEFAULT_SHAKE_ENABLED)

        shakeSensitivity = 10f - getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            .getInt(getString(R.string.shake_sensitivity_key), DEFAULT_SHAKE_SENSITIVITY)

        shakeDuration = 500 + getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            .getInt(getString(R.string.shake_duration_key), DEFAULT_SHAKE_DURATION) * 100

        holdDuration = 500 + getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            .getInt(getString(R.string.hold_duration_key), DEFAULT_HOLD_DURATION) * 100
    }

    fun isShakeToRoll() : Boolean
    {
        return shakeToRoll
    }

    fun shakeSensitivity() : Float
    {
        return shakeSensitivity
    }

    fun shakeDuration() : Int
    {
        return shakeDuration
    }

    fun holdDuration() : Int
    {
        return holdDuration
    }


}

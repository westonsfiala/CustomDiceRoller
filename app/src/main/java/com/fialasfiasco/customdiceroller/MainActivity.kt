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
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.data.SectionsPagerAdapter
import com.fialasfiasco.customdiceroller.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view_pager.adapter =
            SectionsPagerAdapter(this, supportFragmentManager)
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
}

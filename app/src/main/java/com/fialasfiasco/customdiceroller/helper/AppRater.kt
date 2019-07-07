package com.fialasfiasco.customdiceroller.helper

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.fialasfiasco.customdiceroller.BuildConfig
import com.fialasfiasco.customdiceroller.R


object AppRater {

    private const val DAYS_UNTIL_PROMPT = 3//Min number of days
    private const val LAUNCHES_UNTIL_PROMPT = 10//Min number of launches

    fun appLaunched(context: Context) {
        val prefs = context.getSharedPreferences("app_rater_pref", 0)
        if (prefs.getBoolean("dont_show_again_key", false)) {
            return
        }

        val editor = prefs.edit()

        // Increment launch counter
        val launchCount = prefs.getInt("launch_count_key", 0) + 1
        editor.putInt("launch_count_key", launchCount)

        // Get date of first launch
        var dateFirstLaunch: Long? = prefs.getLong("date_first_launch_key", 0)
        if (dateFirstLaunch == 0.toLong()) {
            dateFirstLaunch = System.currentTimeMillis()
            editor.putLong("date_first_launch_key", dateFirstLaunch)
        }

        // Wait at least n days before opening
        if (launchCount >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= dateFirstLaunch!! + DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000) {
                showRateDialog(context)
                editor.putBoolean("dont_show_again_key", true)
            }
        }

        editor.apply()
    }

    private fun showRateDialog(context: Context) {

        val rateDialog = AlertDialog.Builder(context)

        rateDialog.setTitle(R.string.rate_this_app)
        rateDialog.setMessage(R.string.rate_this_app_dialog_text)
        rateDialog.setPositiveButton(R.string.rate_this_app) { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID))
            context.startActivity(intent)
        }
        rateDialog.setNegativeButton(R.string.no_thanks) { _, _ -> }

        rateDialog.show()
    }
}
package com.fialasfiasco.customdiceroller.helper

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.fialasfiasco.customdiceroller.BuildConfig
import com.fialasfiasco.customdiceroller.R

private const val DAYS_UNTIL_PROMPT = 3//Min number of days
private const val LAUNCHES_UNTIL_PROMPT = 10//Min number of launches

class AppLaunchResponder(private val context: Context) {

    private val prefs = context.getSharedPreferences("app_rater_pref", 0)

    fun appLaunched() {
        if (getStopShowing()) {
            return
        }

        val timesOpened = getTimesAppHasOpenedAndIncrement()
        if(timesOpened == 1)
        {
            showFirstOpenDialog()
        }

        val daysSinceFirstOpen = getNumberOfDaysSinceFirstOpen()

        // Wait at least n days before opening
        if (timesOpened >= LAUNCHES_UNTIL_PROMPT && daysSinceFirstOpen >= DAYS_UNTIL_PROMPT) {
            showRateDialog()
            setStopShowing()
        }
    }

    private fun showFirstOpenDialog()
    {
        val firstOpenDialog = AlertDialog.Builder(context)

        firstOpenDialog.setTitle(R.string.first_open_dialog_title)
        firstOpenDialog.setMessage(R.string.first_open_dialog_text)
        firstOpenDialog.setPositiveButton("OK") { _, _ -> }

        firstOpenDialog.show()
    }

    private fun showRateDialog()
    {
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

    private fun getTimesAppHasOpenedAndIncrement() : Int
    {
        val launchCount = prefs.getInt("launch_count_key", 0) + 1

        val editor = prefs.edit()
        editor.putInt("launch_count_key", launchCount)
        editor.apply()

        return launchCount
    }

    private fun getNumberOfDaysSinceFirstOpen() : Int
    {
        // Get date of first launch
        var dateFirstLaunch: Long? = prefs.getLong("date_first_launch_key", 0)
        if (dateFirstLaunch == 0.toLong()) {
            val editor = prefs.edit()
            dateFirstLaunch = System.currentTimeMillis()
            editor.putLong("date_first_launch_key", dateFirstLaunch)
            editor.apply()
        }

        val elapsedTime = System.currentTimeMillis() - dateFirstLaunch!!
        val daysElapsed = elapsedTime / (24 * 60 * 60 * 1000)

        return daysElapsed.toInt()
    }

    private fun getStopShowing() : Boolean
    {
        return prefs.getBoolean("dont_show_again_key", false)
    }

    private fun setStopShowing()
    {
        val editor = prefs.edit()
        editor.putBoolean("dont_show_again_key", true)
        editor.apply()
    }
}
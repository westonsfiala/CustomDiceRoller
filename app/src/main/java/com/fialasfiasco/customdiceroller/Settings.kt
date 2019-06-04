package com.fialasfiasco.customdiceroller

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_settings.*

const val DEFAULT_SHAKE_ENABLED = true
const val DEFAULT_SHAKE_SENSITIVITY = 4
const val DEFAULT_SHAKE_DURATION = 4
const val DEFAULT_HOLD_DURATION = 4

class Settings : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(settingsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        preferences = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        setupShakeSetting()
        setupShakeSensitivity()
        setupShakeDuration()
        setupHoldDuration()
        setShakeSubSettingsEnabled()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupShakeSetting()
    {
        val shakeSet = preferences.getBoolean(getString(R.string.shake_preference_key),DEFAULT_SHAKE_ENABLED)

        shakeToRollSwitch.isChecked = shakeSet

        shakeToRollSwitch.setOnClickListener {
            val editor = preferences.edit()
            editor.putBoolean(getString(R.string.shake_preference_key), shakeToRollSwitch.isChecked)
            editor.apply()

            setShakeSubSettingsEnabled()
        }
    }

    private fun setupShakeSensitivity()
    {
        val shakeSensitivity = preferences.getInt(getString(R.string.shake_sensitivity_key), DEFAULT_SHAKE_SENSITIVITY)

        // Weird hack so that the progress gets updated. Had to find it online.
        // https://stackoverflow.com/questions/9792888/android-seekbar-set-progress-value
        shakeSensitivityBar.max = 0
        shakeSensitivityBar.max = 8
        shakeSensitivityBar.setOnSeekBarChangeListener(this)
        shakeSensitivityBar.progress = shakeSensitivity
    }

    private fun setupShakeDuration()
    {
        val shakeDuration = preferences.getInt(getString(R.string.shake_duration_key), DEFAULT_SHAKE_DURATION)

        // Weird hack so that the progress gets updated. Had to find it online.
        // https://stackoverflow.com/questions/9792888/android-seekbar-set-progress-value
        shakeDurationBar.max = 0
        shakeDurationBar.max = 8
        shakeDurationBar.setOnSeekBarChangeListener(this)
        shakeDurationBar.progress = shakeDuration
    }

    private fun setupHoldDuration()
    {
        val holdDuration = preferences.getInt(getString(R.string.hold_duration_key), DEFAULT_HOLD_DURATION)

        // Weird hack so that the progress gets updated. Had to find it online.
        // https://stackoverflow.com/questions/9792888/android-seekbar-set-progress-value
        holdDurationBar.max = 0
        holdDurationBar.max = 8
        holdDurationBar.setOnSeekBarChangeListener(this)
        holdDurationBar.progress = holdDuration
    }

    private fun setShakeSubSettingsEnabled()
    {
        val shakeEnabled = shakeToRollSwitch.isChecked

        findViewById<TextView>(R.id.shakeSensitivityText).isEnabled = shakeEnabled
        findViewById<TextView>(R.id.shakeDurationText).isEnabled = shakeEnabled
        findViewById<TextView>(R.id.holdDurationText).isEnabled = shakeEnabled

        findViewById<SeekBar>(R.id.shakeSensitivityBar).isEnabled = shakeEnabled
        findViewById<SeekBar>(R.id.shakeDurationBar).isEnabled = shakeEnabled
        findViewById<SeekBar>(R.id.holdDurationBar).isEnabled = shakeEnabled
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                   fromUser: Boolean) {

        when
        {
            seekBar.id == shakeSensitivityBar.id -> {
                val editor = preferences.edit()
                editor.putInt(getString(R.string.shake_sensitivity_key), progress)
                editor.apply()
            }
            seekBar.id == shakeDurationBar.id -> {
                val editor = preferences.edit()
                editor.putInt(getString(R.string.shake_duration_key), progress)
                editor.apply()
            }
            seekBar.id == holdDurationBar.id -> {
                val editor = preferences.edit()
                editor.putInt(getString(R.string.hold_duration_key), progress)
                editor.apply()
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }
}

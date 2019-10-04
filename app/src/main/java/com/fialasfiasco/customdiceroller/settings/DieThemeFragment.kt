package com.fialasfiasco.customdiceroller.settings

import android.os.Bundle
import androidx.preference.*
import com.fialasfiasco.customdiceroller.R

class DieThemeFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val basicDieTheme = DropDownPreference(context)
        basicDieTheme.key = getString(R.string.die_theme_key)
        basicDieTheme.title = getString(R.string.basic_die_theme_title)
        basicDieTheme.setEntries(R.array.basic_die_themes)
        basicDieTheme.setEntryValues(R.array.basic_die_themes)
        basicDieTheme.setDefaultValue(getString(R.string.die_themes_default))
        basicDieTheme.setIcon(R.drawable.ic_palette)
        screen.addPreference(basicDieTheme)

        val metallicDieTheme = DropDownPreference(context)
        metallicDieTheme.key = getString(R.string.die_theme_key)
        metallicDieTheme.title = getString(R.string.metallic_die_theme_title)
        metallicDieTheme.setEntries(R.array.metallic_die_themes)
        metallicDieTheme.setEntryValues(R.array.metallic_die_themes)
        metallicDieTheme.setDefaultValue(getString(R.string.die_themes_default))
        metallicDieTheme.setIcon(R.drawable.ic_gold_stack)
        screen.addPreference(metallicDieTheme)

        val iceCreamDieTheme = DropDownPreference(context)
        iceCreamDieTheme.key = getString(R.string.die_theme_key)
        iceCreamDieTheme.title = getString(R.string.ice_cream_die_theme_title)
        iceCreamDieTheme.setEntries(R.array.ice_cream_die_themes)
        iceCreamDieTheme.setEntryValues(R.array.ice_cream_die_themes)
        iceCreamDieTheme.setDefaultValue(getString(R.string.die_themes_default))
        iceCreamDieTheme.setIcon(R.drawable.ic_ice_cream_cone)
        screen.addPreference(iceCreamDieTheme)

        preferenceScreen = screen
    }
}
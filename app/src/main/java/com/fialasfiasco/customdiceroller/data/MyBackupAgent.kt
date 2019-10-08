package com.fialasfiasco.customdiceroller.data

import android.app.backup.BackupAgentHelper
import android.app.backup.SharedPreferencesBackupHelper

class MyBackupAgent : BackupAgentHelper() {

    // Allocate a helper and install it.
    override fun onCreate() {
        val helper = SharedPreferencesBackupHelper(this, getDefaultSharedPreferencesName())
        addHelper(MY_PREFS_BACKUP_KEY, helper)
    }

    companion object {
        // An arbitrary string used within the BackupAgentHelper implementation to
        // identify the SharedPreferenceBackupHelper's data.
        internal val MY_PREFS_BACKUP_KEY = "myprefs"
    }

    private fun getDefaultSharedPreferencesName(): String {
        return packageName + "_preferences"
    }
}
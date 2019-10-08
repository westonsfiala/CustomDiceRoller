package com.fialasfiasco.customdiceroller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setSupportActionBar(aboutToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        versionText.text = String.format("Version %s", BuildConfig.VERSION_NAME)

        setupRecycler()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupRecycler()
    {
        tipRecyclerView.layoutManager = LinearLayoutManager(this)
        tipRecyclerView.adapter = AboutRecyclerViewAdapter(this)
    }
}

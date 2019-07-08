package com.fialasfiasco.customdiceroller

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.holder_about_tip.view.*

/**
 * [RecyclerView.Adapter] that can display a [TipHolder]
 */
class AboutRecyclerViewAdapter(private val context: Context) : RecyclerView.Adapter<AboutRecyclerViewAdapter.TipHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_about_tip, parent, false)
        return TipHolder(view)
    }

    override fun onBindViewHolder(holder: TipHolder, position: Int) {
        holder.mTipView.text = context.resources.getStringArray(R.array.tips)[position]
    }

    override fun getItemCount(): Int = context.resources.getStringArray(R.array.tips).size

    inner class TipHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mTipView: TextView = view.tipTextView
    }
}

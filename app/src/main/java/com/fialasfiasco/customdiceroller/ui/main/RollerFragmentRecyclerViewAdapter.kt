package com.fialasfiasco.customdiceroller.ui.main

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.fialasfiasco.customdiceroller.R
import kotlinx.android.synthetic.main.die_view_layout.view.*

/**
 * [RecyclerView.Adapter] that can display a [DieViewHolder]
 */
class RollerFragmentRecyclerViewAdapter(private val pageViewModel: PageViewModel) : RecyclerView.Adapter<RollerFragmentRecyclerViewAdapter.DieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.die_view_layout, parent, false)
        return DieViewHolder(view)
    }

    override fun onBindViewHolder(holder: DieViewHolder, position: Int) {
        val simpleDie = pageViewModel.getSimpleDie(position)
        holder.mDieDisplay.setImageResource(simpleDie.
        holder.mDisplayText.text = stamp.title
    }

    override fun getItemCount(): Int = pageViewModel.numHistoryStamps()

    inner class DieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mDieDisplay: ImageView = view.dieDisplay
        val mDisplayText: TextView = view.displayText
    }
}

package com.fialasfiasco.customdiceroller.simple_roller

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.data.SimpleDie
import kotlinx.android.synthetic.main.die_view_layout.view.*

/**
 * [RecyclerView.Adapter] that can display a [DieViewHolder]
 */
class SimpleRollRecyclerViewAdapter(private val pageViewModel: PageViewModel, private val listener: OnSimpleDieViewInteractionListener) :
    RecyclerView.Adapter<SimpleRollRecyclerViewAdapter.DieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.die_view_layout, parent, false)
        return DieViewHolder(view)
    }

    override fun onBindViewHolder(holder: DieViewHolder, position: Int) {
        val simpleDie = pageViewModel.getSimpleDie(position)
        holder.mDieDisplay.setImageResource(simpleDie.mImageID)
        val simpleDieID = simpleDie.mDie
        holder.mDisplayText.text = "d$simpleDieID"

        holder.mLayout.setOnClickListener {
            listener.onDieClicked(simpleDie)
        }

        holder.mLayout.setOnLongClickListener {
            listener.onDieLongClick(simpleDie)
            true
        }
    }

    interface OnSimpleDieViewInteractionListener
    {
        fun onDieClicked(simpleDie: SimpleDie)
        fun onDieLongClick(simpleDie: SimpleDie)
    }

    override fun getItemCount(): Int = pageViewModel.getSimpleDiceSize()

    inner class DieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mDieDisplay: ImageView = view.dieDisplay
        val mDisplayText: TextView = view.displayText
        val mLayout: ConstraintLayout = view.dieViewLayout
    }
}

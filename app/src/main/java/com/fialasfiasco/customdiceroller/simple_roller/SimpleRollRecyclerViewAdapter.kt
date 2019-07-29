package com.fialasfiasco.customdiceroller.simple_roller

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.dice.Die
import com.fialasfiasco.customdiceroller.data.PageViewModel
import kotlinx.android.synthetic.main.holder_simple_die.view.*

/**
 * [RecyclerView.Adapter] that can display a [DieViewHolder]
 */
class SimpleRollRecyclerViewAdapter(private val pageViewModel: PageViewModel, private val listener: OnSimpleDieViewInteractionListener) :
    RecyclerView.Adapter<SimpleRollRecyclerViewAdapter.DieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_simple_die, parent, false)
        return DieViewHolder(view)
    }

    override fun onBindViewHolder(holder: DieViewHolder, position: Int) {
        val die = pageViewModel.getInnerDie(position)
        holder.mDieDisplay.setImageResource(die.getImageID())
        holder.mDisplayText.text = die.getDisplayName()

        holder.mLayout.setOnClickListener {
            listener.onDieClicked(die)
        }

        holder.mLayout.setOnLongClickListener {
            listener.onDieLongClick(die)
            true
        }
    }

    interface OnSimpleDieViewInteractionListener
    {
        fun onDieClicked(die: Die)
        fun onDieLongClick(die: Die)
    }

    override fun getItemCount(): Int = pageViewModel.getInnerDiceSize()

    inner class DieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mDieDisplay: ImageView = view.dieDisplay
        val mDisplayText: TextView = view.dieDisplayText
        val mLayout: ConstraintLayout = view.dieViewLayout
    }
}

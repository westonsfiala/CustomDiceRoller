package com.fialasfiasco.customdiceroller.saved_roller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.AggregateRoll
import com.fialasfiasco.customdiceroller.data.PageViewModel
import kotlinx.android.synthetic.main.holder_simple_die.view.*

/**
 * [RecyclerView.Adapter] that can display a [SavedRollViewHolder]
 */
class SavedRollerRecyclerViewAdapter(private val pageViewModel: PageViewModel, private val listener: OnSavedRollViewInteractionListener) :
    RecyclerView.Adapter<SavedRollerRecyclerViewAdapter.SavedRollViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRollViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_simple_die, parent, false)
        return SavedRollViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedRollViewHolder, position: Int) {
        val die = pageViewModel.getSavedRoll(position)
        holder.mDieDisplay.setImageResource(die.getImageID())
        holder.mDisplayText.text = die.getName()

        holder.mLayout.setOnClickListener {
            listener.onRollClicked(die)
        }

        holder.mLayout.setOnLongClickListener {
            listener.onRollLongClick(die)
            true
        }
    }

    interface OnSavedRollViewInteractionListener
    {
        fun onRollClicked(roll: AggregateRoll)
        fun onRollLongClick(roll: AggregateRoll)
    }

    override fun getItemCount(): Int = pageViewModel.getSavedRollSize()

    inner class SavedRollViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mDieDisplay: ImageView = view.dieDisplay
        val mDisplayText: TextView = view.dieDisplayText
        val mLayout: ConstraintLayout = view.dieViewLayout
    }
}

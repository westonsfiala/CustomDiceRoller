package com.fialasfiasco.customdiceroller.saved_roller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.dice.Roll
import com.fialasfiasco.customdiceroller.data.PageViewModel
import kotlinx.android.synthetic.main.holder_saved_roll.view.*
import kotlinx.android.synthetic.main.holder_simple_die.view.*

/**
 * [RecyclerView.Adapter] that can display a [SavedRollViewHolder]
 */
class SavedRollerRecyclerViewAdapter(private val pageViewModel: PageViewModel, private val listener: OnSavedRollViewInteractionListener) :
    RecyclerView.Adapter<SavedRollerRecyclerViewAdapter.SavedRollViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRollViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_saved_roll, parent, false)
        return SavedRollViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedRollViewHolder, position: Int) {
        val die = pageViewModel.getSavedRoll(position)

        holder.mRollNameText.text = die.getDisplayName()
        holder.mRollDetailsText.text = die.getDetailedRollName()

        holder.mClickableLayout.setOnClickListener {
            listener.onRollClicked(die)
        }

        holder.mInfoButton.setOnClickListener {
            listener.onRollInfoClicked(die)
        }
    }

    interface OnSavedRollViewInteractionListener
    {
        fun onRollClicked(roll: Roll)
        fun onRollInfoClicked(roll: Roll)
    }

    override fun getItemCount(): Int = pageViewModel.getSavedRollSize()

    inner class SavedRollViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mRollNameText: TextView = view.rollNameText
        val mRollDetailsText: TextView = view.rollDetailsText
        val mClickableLayout: ConstraintLayout = view.clickableLayout
        val mInfoButton: ImageButton = view.infoButton
    }
}

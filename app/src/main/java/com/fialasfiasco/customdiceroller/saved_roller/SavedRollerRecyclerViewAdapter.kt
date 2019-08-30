package com.fialasfiasco.customdiceroller.saved_roller

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
class SavedRollerRecyclerViewAdapter(private val context: Context,
                                     private val pageViewModel: PageViewModel,
                                     private val listener: OnSavedRollViewInteractionListener) :
    RecyclerView.Adapter<SavedRollerRecyclerViewAdapter.SavedRollViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRollViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_saved_roll, parent, false)
        return SavedRollViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedRollViewHolder, position: Int) {
        val roll = pageViewModel.getSavedRoll(position)

        setupDisplayText(holder, roll)
        setupClickableLayout(holder, roll)
        setupInfoButton(holder, roll)
    }

    private fun setupDisplayText(holder: SavedRollViewHolder, roll: Roll) {
        holder.mRollNameText.text = roll.getDisplayName()
        holder.mRollDetailsText.text = roll.getDetailedRollName()
    }

    private fun setupClickableLayout(holder: SavedRollViewHolder, roll: Roll) {
        holder.mClickableLayout.setOnClickListener {
            listener.onRollClicked(roll)
        }
    }

    private fun setupInfoButton(holder: SavedRollViewHolder, roll: Roll) {
        holder.mInfoButton.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.mInfoButton)

            popupMenu.menu?.add(Menu.NONE, R.string.remove, Menu.NONE, context.getString(R.string.remove))
            //popupMenu.menu?.add(Menu.NONE, R.string.edit, Menu.NONE, context.getString(R.string.edit))

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.string.remove -> listener.onRemoveRollClicked(roll)
                    R.string.edit -> listener.onEditRollClicked(roll)
                }
                true
            }

            popupMenu.show()
        }
    }

    interface OnSavedRollViewInteractionListener {
        fun onRollClicked(roll: Roll)
        fun onRemoveRollClicked(roll: Roll)
        fun onEditRollClicked(roll: Roll)
    }

    override fun getItemCount(): Int = pageViewModel.getSavedRollSize()

    inner class SavedRollViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mRollNameText: TextView = view.rollNameText
        val mRollDetailsText: TextView = view.rollDetailsText
        val mClickableLayout: ConstraintLayout = view.clickableLayout
        val mInfoButton: ImageButton = view.infoButton
    }
}

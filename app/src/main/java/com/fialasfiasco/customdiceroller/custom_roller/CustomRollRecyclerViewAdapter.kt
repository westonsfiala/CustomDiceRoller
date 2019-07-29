package com.fialasfiasco.customdiceroller.custom_roller

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import kotlinx.android.synthetic.main.fragment_up_down_buttons.view.*
import kotlinx.android.synthetic.main.holder_custom_die.view.*
import kotlinx.android.synthetic.main.holder_simple_die.view.*

/**
 * [RecyclerView.Adapter] that can display a [CustomDieViewHolder]
 */
class CustomRollRecyclerViewAdapter(private val pageViewModel: PageViewModel,
                                    private val listener: CustomRollInterfaceListener)
    :
    RecyclerView.Adapter<CustomRollRecyclerViewAdapter.CustomDieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomDieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_custom_die, parent, false)

        return CustomDieViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomDieViewHolder, position: Int) {
        val aggregateDie = pageViewModel.getInnerDie(position)
        holder.mImage.setImageResource(aggregateDie.getImageID())
        holder.mText.text = aggregateDie.getDisplayName()

        holder.mUpButton.setOnClickListener {
            pageViewModel.incrementCustomDieCount(position)
            updateDieCount(holder, position)
        }

        holder.mUpButton.setOnLongClickListener {
            pageViewModel.largeIncrementCustomDieCount(position)
            updateDieCount(holder, position)
            true
        }

        holder.mDownButton.setOnClickListener {
            pageViewModel.decrementCustomDieCount(position)
            updateDieCount(holder, position)
        }

        holder.mDownButton.setOnLongClickListener {
            pageViewModel.largeDecrementCustomDieCount(position)
            updateDieCount(holder, position)
            true
        }

        updateDieCount(holder,position)

        holder.mModText.setOnClickListener {
            listener.onDisplayTextClicked(holder, position)
        }
    }

    private fun updateDieCount(holder: CustomDieViewHolder, position: Int)
    {
        holder.mModText.text = pageViewModel.getCustomDieCount(position).toString()
    }

    override fun getItemCount(): Int = pageViewModel.getInnerDiceSize()

    inner class CustomDieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mImage: ImageView = view.simpleDieInclude.dieDisplay
        val mText: TextView = view.simpleDieInclude.dieDisplayText
        val mUpButton: ImageButton = view.upDownButtonsInclude.upButton
        val mDownButton: ImageButton = view.upDownButtonsInclude.downButton
        val mModText: TextView = view.upDownButtonsInclude.upDownDisplayText

        init {
            view.simpleDieInclude.isClickable = false
        }
    }

    interface CustomRollInterfaceListener
    {
        fun onDisplayTextClicked(holder: CustomDieViewHolder, position: Int)
    }
}

package com.fialasfiasco.customdiceroller.custom_roller

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.data.ThemedDieImageGetter
import com.fialasfiasco.customdiceroller.dice.DieLoadError
import com.fialasfiasco.customdiceroller.dice.RollProperties
import com.fialasfiasco.customdiceroller.helper.*
import kotlinx.android.synthetic.main.layout_up_down_buttons.view.*
import kotlinx.android.synthetic.main.holder_custom_die.view.*
import kotlinx.android.synthetic.main.holder_simple_die.view.*

/**
 * [RecyclerView.Adapter] that can display a [CustomDieViewHolder]
 */
class CustomRollRecyclerViewAdapter(private val context: Context,
                                    private val layoutInflater: LayoutInflater,
                                    private val pageViewModel: PageViewModel,
                                    private val listener: CustomRollInterfaceListener)
    :
    RecyclerView.Adapter<CustomRollRecyclerViewAdapter.CustomDieViewHolder>() {

    private val numDiceUpDownButtonsID = 0
    private val modifierUpDownButtonsID = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomDieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_custom_die, parent, false)

        return CustomDieViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomDieViewHolder, position: Int) {
        setupDieDisplayHolder(holder,position)
        setupNumDiceHolder(holder,position)
        setupModifierHolder(holder,position)
        setupPropertiesBar(holder,position)
        setupMoveUpDownHolder(holder,position)
        setupRemoveDieHolder(holder,position)
    }

    private fun setupDieDisplayHolder(holder: CustomDieViewHolder, position: Int) {
        val die = pageViewModel.getCustomDieAt(position)
        holder.mImage.setImageDrawable(ThemedDieImageGetter(context, pageViewModel).getDieDrawable(die.getImageID()))
        holder.mText.text = die.getDisplayName()

        holder.mSimpleDieDisplay.setOnClickListener {
            EditDialogs(context, layoutInflater).createNameDialog(
                "Name & Category of roll",
                "Choose something memorable.",
                die.getDisplayName(),
                object : EditDialogs.NameDialogListener {
                    override fun respondToOK(name: String) {

                        try {
                            val newDie = die.clone(name)

                            if(pageViewModel.hasDieInCustomRoll(newDie)) {
                                Toast.makeText(context, "Unable to rename die", Toast.LENGTH_LONG).show()
                            } else {
                                if(pageViewModel.overrideDieInCustomRollAt(newDie, position)) {
                                    notifyDataSetChanged()
                                }
                            }
                        } catch ( error : DieLoadError) {
                            Toast.makeText(context, "Unable to rename die", Toast.LENGTH_LONG).show()
                        }

                    }
                })
        }
    }

    private fun setupNumDiceHolder(holder: CustomDieViewHolder, position: Int) {
        UpDownButtonsHelper(context, layoutInflater,
            holder.mNumDiceUpButton, holder.mNumDiceDownButton, holder.mNumDiceDisplayText,
            numDiceUpDownButtonsID, UpDownButtonsItemListener(position))
    }

    private fun setupModifierHolder(holder: CustomDieViewHolder, position: Int) {
        UpDownButtonsHelper(context, layoutInflater,
            holder.mModifierUpButton, holder.mModifierDownButton, holder.mModifierDisplayText,
            modifierUpDownButtonsID, UpDownButtonsItemListener(position))
    }

    private fun setupPropertiesBar(holder: CustomDieViewHolder, position: Int) {
        if(pageViewModel.getRollPropertiesEnabled()) {
            holder.mAddPropertyButton.visibility = Button.VISIBLE
            holder.mCurrentPropertiesButton.visibility = Button.VISIBLE

            RollPropertyHelper(context, layoutInflater, holder.mAddPropertyButton, holder.mCurrentPropertiesButton, position, RollPropertyItemListener())
        } else {
            holder.mAddPropertyButton.visibility = Button.GONE
            holder.mCurrentPropertiesButton.visibility = Button.GONE
        }
    }

    private fun setupMoveUpDownHolder(holder: CustomDieViewHolder, position: Int) {
        holder.mMoveDieUpButton.setOnClickListener {
            if(pageViewModel.moveCustomDieUp(position)) {
                notifyDataSetChanged()
            }
        }

        holder.mMoveDieDownButton.setOnClickListener {
            if(pageViewModel.moveCustomDieDown(position)) {
                notifyDataSetChanged()
            }
        }
    }

    private fun setupRemoveDieHolder(holder: CustomDieViewHolder, position: Int) {
        holder.mRemoveDieButton.setOnClickListener {
            pageViewModel.removeCustomDieFromPool(pageViewModel.getCustomDieAt(position))
            notifyDataSetChanged()
            listener.onNumberDiceInRollChange()
        }
    }

    override fun getItemCount(): Int = pageViewModel.getNumberCustomRollItems()

    inner class CustomDieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mImage: ImageView = view.simpleDieInclude.dieDisplay
        val mText: TextView = view.simpleDieInclude.dieDisplayText
        val mSimpleDieDisplay: View = view.simpleDieInclude
        val mNumDiceUpButton: ImageButton = view.diceUpDownButtonsInclude.upButton
        val mNumDiceDownButton: ImageButton = view.diceUpDownButtonsInclude.downButton
        val mNumDiceDisplayText: TextView = view.diceUpDownButtonsInclude.upDownDisplayText
        val mModifierUpButton: ImageButton = view.modifierUpDownButtonsInclude.upButton
        val mModifierDownButton: ImageButton = view.modifierUpDownButtonsInclude.downButton
        val mModifierDisplayText: TextView = view.modifierUpDownButtonsInclude.upDownDisplayText
        val mAddPropertyButton: Button = view.addCustomPropertyButton
        val mCurrentPropertiesButton: Button = view.currentCustomPropertiesButton
        val mMoveDieUpButton: ImageButton = view.moveDieUpButton
        val mMoveDieDownButton: ImageButton = view.moveDieDownButton
        val mRemoveDieButton: ImageButton = view.removeDieButton
    }

    inner class RollPropertyItemListener : RollPropertyHelper.PropertyChangeListener
    {
        override fun advantageDisadvantageChanged(id: Int, mode: Int) {
            pageViewModel.setAdvantageDisadvantageCustomDie(id, mode)
        }

        override fun dropHighChanged(id: Int, dropValue: Int) {
            pageViewModel.setCustomDieDropHigh(id, dropValue)
        }

        override fun dropLowChanged(id: Int, dropValue: Int) {
            pageViewModel.setCustomDieDropLow(id, dropValue)
        }

        override fun keepHighChanged(id: Int, keepValue: Int) {
            pageViewModel.setCustomDieKeepHigh(id, keepValue)
        }

        override fun keepLowChanged(id: Int, keepValue: Int) {
            pageViewModel.setCustomDieKeepLow(id, keepValue)
        }

        override fun reRollSet(id: Int, threshold: Int) {
            pageViewModel.setCustomDieReRoll(id, threshold)
        }

        override fun reRollCleared(id: Int) {
            pageViewModel.clearCustomDieReRoll(id)
        }

        override fun minimumDieValueSet(id: Int, threshold: Int) {
            pageViewModel.setCustomDieMinimumDieValue(id, threshold)
        }

        override fun minimumDieValueCleared(id: Int) {
            pageViewModel.clearCustomDieMinimumDieValue(id)
        }

        override fun explodeChanged(id: Int, explode: Boolean) {
            pageViewModel.setCustomDieExplode(id, explode)
        }

        override fun getCurrentProperties(id: Int): RollProperties {
            return pageViewModel.getCustomDieRollProperties(id)
        }
    }

    inner class UpDownButtonsItemListener(private val position: Int) : UpDownButtonsHelper.UpDownButtonsListener
    {
        override fun downButtonClick(id: Int) {
            when (id)
            {
                numDiceUpDownButtonsID -> pageViewModel.decrementCustomDieCount(position)
                modifierUpDownButtonsID -> pageViewModel.decrementCustomDieModifier(position)
            }
        }

        override fun downButtonLongClick(id: Int) {
            when (id)
            {
                numDiceUpDownButtonsID -> pageViewModel.largeDecrementCustomDieCount(position)
                modifierUpDownButtonsID -> pageViewModel.largeDecrementCustomDieModifier(position)
            }
        }

        override fun upButtonClick(id: Int) {
            when (id)
            {
                numDiceUpDownButtonsID -> pageViewModel.incrementCustomDieCount(position)
                modifierUpDownButtonsID -> pageViewModel.incrementCustomDieModifier(position)
            }
        }

        override fun upButtonLongClick(id: Int) {
            when (id)
            {
                numDiceUpDownButtonsID -> pageViewModel.largeIncrementCustomDieCount(position)
                modifierUpDownButtonsID -> pageViewModel.largeIncrementCustomDieModifier(position)
            }
        }

        override fun setExactValue(id: Int, value: Int) {
            when (id)
            {
                numDiceUpDownButtonsID -> pageViewModel.setCustomDieCountExact(position, value)
                modifierUpDownButtonsID -> pageViewModel.setCustomDieModifierExact(position, value)
            }
        }

        override fun getExactValue(id: Int): Int {
            return when (id)
            {
                numDiceUpDownButtonsID -> pageViewModel.getCustomDieDieCount(position)
                modifierUpDownButtonsID -> pageViewModel.getCustomDieModifier(position)
                else -> 0
            }
        }

        override fun getDisplayText(id: Int): CharSequence {
            return when (id)
            {
                numDiceUpDownButtonsID -> getNumDiceString(pageViewModel.getCustomDieDieCount(position))
                modifierUpDownButtonsID -> getModifierString(pageViewModel.getCustomDieModifier(position))
                else -> context.getString(R.string.temp)
            }
        }
    }

    interface CustomRollInterfaceListener
    {
        fun onNumberDiceInRollChange()
    }
}

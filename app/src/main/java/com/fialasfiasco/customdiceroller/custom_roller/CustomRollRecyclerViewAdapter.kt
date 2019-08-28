package com.fialasfiasco.customdiceroller.custom_roller

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.dice.RollProperties
import com.fialasfiasco.customdiceroller.dice.rollAdvantageValue
import com.fialasfiasco.customdiceroller.dice.rollDisadvantageValue
import com.fialasfiasco.customdiceroller.dice.rollNaturalValue
import com.fialasfiasco.customdiceroller.helper.EditDialogs
import com.fialasfiasco.customdiceroller.helper.getDropDiceString
import com.fialasfiasco.customdiceroller.helper.getModifierString
import com.fialasfiasco.customdiceroller.helper.getNumDiceString
import kotlinx.android.synthetic.main.fragment_up_down_buttons.view.*
import kotlinx.android.synthetic.main.holder_custom_die.view.*
import kotlinx.android.synthetic.main.holder_simple_die.view.*

/**
 * [RecyclerView.Adapter] that can display a [CustomDieViewHolder]
 */
class CustomRollRecyclerViewAdapter(private val context: Context,
                                    private val pageViewModel: PageViewModel,
                                    private val listener: CustomRollInterfaceListener)
    :
    RecyclerView.Adapter<CustomRollRecyclerViewAdapter.CustomDieViewHolder>() {

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
        holder.mImage.setImageResource(die.getImageID())
        holder.mText.text = die.getDisplayName()
    }

    private fun setupNumDiceHolder(holder: CustomDieViewHolder, position: Int) {
        holder.mNumDiceUpButton.setOnClickListener {
            pageViewModel.incrementCustomDieCount(position)
            updateDieCountText(holder, position)
        }

        holder.mNumDiceUpButton.setOnLongClickListener {
            pageViewModel.largeIncrementCustomDieCount(position)
            updateDieCountText(holder, position)
            true
        }

        holder.mNumDiceDownButton.setOnClickListener {
            pageViewModel.decrementCustomDieCount(position)
            updateDieCountText(holder, position)
        }

        holder.mNumDiceDownButton.setOnLongClickListener {
            pageViewModel.largeDecrementCustomDieCount(position)
            updateDieCountText(holder, position)
            true
        }

        updateDieCountText(holder,position)

        holder.mNumDiceDisplayText.setOnClickListener {
            listener.onNumDiceDisplayTextClicked(holder, position)
        }
    }

    private fun setupModifierHolder(holder: CustomDieViewHolder, position: Int) {
        holder.mModifierUpButton.setOnClickListener {
            pageViewModel.incrementCustomDieModifier(position)
            updateModifierText(holder, position)
        }

        holder.mModifierUpButton.setOnLongClickListener {
            pageViewModel.largeIncrementCustomDieModifier(position)
            updateModifierText(holder, position)
            true
        }

        holder.mModifierDownButton.setOnClickListener {
            pageViewModel.decrementCustomDieModifier(position)
            updateModifierText(holder, position)
        }

        holder.mModifierDownButton.setOnLongClickListener {
            pageViewModel.largeDecrementCustomDieModifier(position)
            updateModifierText(holder, position)
            true
        }

        updateModifierText(holder,position)

        holder.mModifierDisplayText.setOnClickListener {
            listener.onModifierDisplayTextClicked(holder, position)
        }
    }

    private fun setupPropertiesBar(holder: CustomDieViewHolder, position: Int) {
        if(pageViewModel.getRollPropertiesEnabled()) {
            holder.mAddPropertyButton.visibility = Button.VISIBLE
            holder.mCurrentPropertiesButton.visibility = Button.VISIBLE

            setupAddPropertyButton(holder, position)
            setupCurrentPropertiesButton(holder,position)
        } else {
            holder.mAddPropertyButton.visibility = Button.GONE
            holder.mCurrentPropertiesButton.visibility = Button.GONE
        }
    }

    private fun setupAddPropertyButton(holder: CustomDieViewHolder, position: Int) {
        holder.mAddPropertyButton.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.mAddPropertyButton)

            popupMenu.menu?.add(Menu.NONE, R.string.advantage, Menu.NONE, context.getString(R.string.advantage))
            popupMenu.menu?.add(Menu.NONE, R.string.disadvantage, Menu.NONE, context.getString(R.string.disadvantage))
            popupMenu.menu?.add(Menu.NONE, R.string.drop_highest, Menu.NONE, context.getString(R.string.drop_highest))
            popupMenu.menu?.add(Menu.NONE, R.string.drop_lowest, Menu.NONE, context.getString(R.string.drop_lowest))

            popupMenu.setOnMenuItemClickListener {
                //Toast.makeText(context, it.title, Toast.LENGTH_SHORT).show()

                when(it.itemId) {
                    R.string.advantage -> pageViewModel.setAdvantageDisadvantageCustomDie(position,rollAdvantageValue)
                    R.string.disadvantage -> pageViewModel.setAdvantageDisadvantageCustomDie(position,rollDisadvantageValue)
                    R.string.drop_highest -> listener.onDropHighButtonClicked(holder, position)
                    R.string.drop_lowest -> listener.onDropLowButtonClicked(holder, position)
                }

                updateCurrentPropertiesButton(holder, position)

                true
            }

            popupMenu.show()
        }
    }

    private fun setupCurrentPropertiesButton(holder: CustomDieViewHolder, position: Int) {
        holder.mCurrentPropertiesButton.setOnClickListener {
            var hasItems = false
            val popupMenu = PopupMenu(context, holder.mCurrentPropertiesButton)

            val defaultProps = RollProperties()
            val rollProps = pageViewModel.getCustomDieRollProperties(position)

            if(rollProps.mAdvantageDisadvantage == rollAdvantageValue) {
                popupMenu.menu?.add(Menu.NONE, R.string.advantage, Menu.NONE, context.getString(R.string.advantage))
                hasItems = true
            } else if (rollProps.mAdvantageDisadvantage == rollDisadvantageValue) {
                popupMenu.menu?.add(Menu.NONE, R.string.disadvantage, Menu.NONE, context.getString(R.string.disadvantage))
                hasItems = true
            }

            if(rollProps.mDropHighLow != defaultProps.mDropHighLow) {
                popupMenu.menu?.add(Menu.NONE, R.string.drop_high_low, Menu.NONE, getDropDiceString(rollProps.mDropHighLow))
                hasItems = true
            }

            if(hasItems)
            {
                holder.mCurrentPropertiesButton.setText(R.string.tap_to_remove)

                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.string.advantage -> pageViewModel.setAdvantageDisadvantageCustomDie(position,rollNaturalValue)
                        R.string.disadvantage -> pageViewModel.setAdvantageDisadvantageCustomDie(position,rollNaturalValue)
                        R.string.drop_high_low -> pageViewModel.setCustomDieDropHighLow(position, 0)
                    }

                    true
                }
            }
            else {
                popupMenu.menu?.add(Menu.NONE, Menu.NONE, Menu.NONE, "No Properties")
            }

            popupMenu.setOnDismissListener {
                updateCurrentPropertiesButton(holder, position)
            }

            popupMenu.show()
        }
        updateCurrentPropertiesButton(holder, position)
    }

    fun updateCurrentPropertiesButton(holder: CustomDieViewHolder, position: Int) {
        var numProperties = 0

        val defaultProps = RollProperties()
        val rollProps = pageViewModel.getCustomDieRollProperties(position)

        if(rollProps.mAdvantageDisadvantage != defaultProps.mAdvantageDisadvantage) {
            numProperties += 1
        }

        if(rollProps.mDropHighLow != defaultProps.mDropHighLow) {
            numProperties += 1
        }

        holder.mCurrentPropertiesButton.text = if(numProperties == 0) {
            "No Properties"
        } else {
            "$numProperties Properties"
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

    private fun updateDieCountText(holder: CustomDieViewHolder, position: Int)
    {
        holder.mNumDiceDisplayText.text = getNumDiceString(pageViewModel.getCustomDieDieCount(position))
    }

    private fun updateModifierText(holder: CustomDieViewHolder, position: Int)
    {
        holder.mModifierDisplayText.text = getModifierString(pageViewModel.getCustomDieModifier(position))
    }

    override fun getItemCount(): Int = pageViewModel.getNumberCustomRollItems()

    inner class CustomDieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mImage: ImageView = view.simpleDieInclude.dieDisplay
        val mText: TextView = view.simpleDieInclude.dieDisplayText
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

        init {
            view.simpleDieInclude.isClickable = false
        }
    }

    interface CustomRollInterfaceListener
    {
        fun onNumDiceDisplayTextClicked(holder: CustomDieViewHolder, position: Int)
        fun onModifierDisplayTextClicked(holder: CustomDieViewHolder, position: Int)
        fun onDropHighButtonClicked(holder: CustomDieViewHolder, position: Int)
        fun onDropLowButtonClicked(holder: CustomDieViewHolder, position: Int)
        fun onNumberDiceInRollChange()
    }
}

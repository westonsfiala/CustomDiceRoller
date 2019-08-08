package com.fialasfiasco.customdiceroller.custom_roller

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.dice.rollAdvantageValue
import com.fialasfiasco.customdiceroller.dice.rollDisadvantageValue
import com.fialasfiasco.customdiceroller.dice.rollNaturalValue
import com.fialasfiasco.customdiceroller.helper.getModifierString
import com.fialasfiasco.customdiceroller.helper.getNumDiceString
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
        setupDieDisplayHolder(holder,position)
        setupNumDiceHolder(holder,position)
        setupModifierHolder(holder,position)
        setupAdvantageDisadvantageHolder(holder,position)
        setupDropHighLowHolder(holder,position)
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

    private fun setupAdvantageDisadvantageHolder(holder: CustomDieViewHolder, position: Int) {
        if(pageViewModel.getAdvantageDisadvantageEnabled()) {
            holder.mRadioGroup.visibility = View.VISIBLE

            when(pageViewModel.getAdvantageDisadvantageCustomDie(position))
            {
                rollDisadvantageValue -> holder.mDisadvantageButton.isChecked = true
                rollNaturalValue -> holder.mNaturalButton.isChecked = true
                rollAdvantageValue -> holder.mAdvantageButton.isChecked = true
            }

            holder.mNaturalButton.setOnClickListener {
                pageViewModel.setAdvantageDisadvantageCustomDie(position, rollNaturalValue)
            }

            holder.mAdvantageButton.setOnClickListener {
                pageViewModel.setAdvantageDisadvantageCustomDie(position, rollAdvantageValue)
            }

            holder.mDisadvantageButton.setOnClickListener {
                pageViewModel.setAdvantageDisadvantageCustomDie(position, rollDisadvantageValue)
            }
        } else {
            holder.mRadioGroup.visibility = View.GONE
        }
    }

    private fun setupDropHighLowHolder(holder: CustomDieViewHolder, position: Int) {
        if(pageViewModel.getDropHighLowEnabled()) {
            holder.mDropDiceButton.visibility = View.VISIBLE

            holder.mDropDiceButton.setOnClickListener {
                listener.onDropDiceButtonClicked(holder, position)
            }
        } else {
            holder.mDropDiceButton.visibility = View.GONE
        }
    }

    private fun setupMoveUpDownHolder(holder: CustomDieViewHolder, position: Int) {
        holder.mMoveDieUpButton.setOnClickListener {

        }

        holder.mMoveDieDownButton.setOnClickListener {

        }
    }

    private fun setupRemoveDieHolder(holder: CustomDieViewHolder, position: Int) {
        holder.mRemoveDieButton.setOnClickListener {
            pageViewModel.removeCustomDieFromPool(pageViewModel.getCustomDieAt(position))
            notifyDataSetChanged()
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
        val mRadioGroup: RadioGroup = view.radioGroup
        val mNaturalButton: RadioButton = view.naturalRadioButton
        val mAdvantageButton: RadioButton = view.advantageRadioButton
        val mDisadvantageButton: RadioButton = view.disadvantageRadioButton
        val mDropDiceButton: Button = view.dropDiceButton
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
        fun onDropDiceButtonClicked(holder: CustomDieViewHolder, position: Int)
    }
}

package com.fialasfiasco.customdiceroller.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.widget.Button
import android.widget.PopupMenu
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.MIN_BOUNDING_VALUE
import com.fialasfiasco.customdiceroller.data.MAX_BOUNDING_VALUE
import com.fialasfiasco.customdiceroller.dice.RollProperties
import com.fialasfiasco.customdiceroller.dice.rollAdvantageValue
import com.fialasfiasco.customdiceroller.dice.rollDisadvantageValue
import com.fialasfiasco.customdiceroller.dice.rollNaturalValue
import kotlin.math.max

class RollPropertyHelper(private val context: Context,
                         private val layoutInflater: LayoutInflater,
                         private val addPropertyButton: Button,
                         private val currentPropertiesButton: Button,
                         private val id : Int,
                         private val listener: PropertyChangeListener) {

    init {
        setupAddPropertyButton()
        setupCurrentPropertiesButton()
    }

    private fun setupAddPropertyButton() {
        addPropertyButton.setOnClickListener {
            val popupMenu = PopupMenu(context, addPropertyButton)

            popupMenu.menu?.add(Menu.NONE, R.string.advantage, Menu.NONE, context.getString(R.string.advantage))
            popupMenu.menu?.add(Menu.NONE, R.string.disadvantage, Menu.NONE, context.getString(R.string.disadvantage))
            popupMenu.menu?.add(Menu.NONE, R.string.drop_highest, Menu.NONE, context.getString(R.string.drop_highest))
            popupMenu.menu?.add(Menu.NONE, R.string.drop_lowest, Menu.NONE, context.getString(R.string.drop_lowest))
            popupMenu.menu?.add(Menu.NONE, R.string.keep_highest, Menu.NONE, context.getString(R.string.keep_highest))
            popupMenu.menu?.add(Menu.NONE, R.string.keep_lowest, Menu.NONE, context.getString(R.string.keep_lowest))
            popupMenu.menu?.add(Menu.NONE, R.string.re_roll_dice, Menu.NONE, context.getString(R.string.re_roll_dice))
            popupMenu.menu?.add(Menu.NONE, R.string.minimum_die_value, Menu.NONE, context.getString(R.string.minimum_die_value))
            popupMenu.menu?.add(Menu.NONE, R.string.explode, Menu.NONE, context.getString(R.string.explode))

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.string.advantage -> listener.advantageDisadvantageChanged(id, rollAdvantageValue)
                    R.string.disadvantage -> listener.advantageDisadvantageChanged(id, rollDisadvantageValue)
                    R.string.drop_highest -> setDropHigh()
                    R.string.drop_lowest -> setDropLow()
                    R.string.keep_highest -> setKeepHigh()
                    R.string.keep_lowest -> setKeepLow()
                    R.string.re_roll_dice -> setReRoll()
                    R.string.minimum_die_value -> setMinimumDieValue()
                    R.string.explode -> listener.explodeChanged(id, true)
                }

                updateCurrentPropertiesButton()

                true
            }

            popupMenu.show()
        }
    }

    private fun setupCurrentPropertiesButton() {
        currentPropertiesButton.setOnClickListener {
            var hasItems = false
            val popupMenu = PopupMenu(context, currentPropertiesButton)

            val defaultProps = RollProperties()
            val rollProps = listener.getCurrentProperties(id)

            if(rollProps.mAdvantageDisadvantage == rollAdvantageValue) {
                popupMenu.menu?.add(Menu.NONE, R.string.advantage, Menu.NONE, context.getString(R.string.advantage))
                hasItems = true
            } else if (rollProps.mAdvantageDisadvantage == rollDisadvantageValue) {
                popupMenu.menu?.add(Menu.NONE, R.string.disadvantage, Menu.NONE, context.getString(R.string.disadvantage))
                hasItems = true
            }

            if(rollProps.mDropHigh != defaultProps.mDropHigh) {
                popupMenu.menu?.add(Menu.NONE, R.string.drop_highest, Menu.NONE, getDropHighString(rollProps.mDropHigh))
                hasItems = true
            }

            if(rollProps.mDropLow != defaultProps.mDropLow) {
                popupMenu.menu?.add(Menu.NONE, R.string.drop_lowest, Menu.NONE, getDropLowString(rollProps.mDropLow))
                hasItems = true
            }

            if(rollProps.mKeepHigh != defaultProps.mKeepHigh) {
                popupMenu.menu?.add(Menu.NONE, R.string.keep_highest, Menu.NONE, getKeepHighString(rollProps.mKeepHigh))
                hasItems = true
            }

            if(rollProps.mKeepLow != defaultProps.mKeepLow) {
                popupMenu.menu?.add(Menu.NONE, R.string.keep_lowest, Menu.NONE, getKeepLowString(rollProps.mKeepLow))
                hasItems = true
            }

            if(rollProps.mUseReRoll != defaultProps.mUseReRoll && rollProps.mReRoll != defaultProps.mReRoll) {
                popupMenu.menu?.add(Menu.NONE, R.string.re_roll_dice, Menu.NONE, getReRollString(rollProps.mReRoll))
                hasItems = true
            }

            if(rollProps.mUseMinimumRoll != defaultProps.mUseMinimumRoll && rollProps.mMinimumRoll != defaultProps.mMinimumRoll) {
                popupMenu.menu?.add(Menu.NONE, R.string.minimum_die_value, Menu.NONE, getMinimumDieValueString(rollProps.mMinimumRoll))
                hasItems = true
            }

            if(rollProps.mExplode != defaultProps.mExplode) {
                popupMenu.menu?.add(Menu.NONE, R.string.explode, Menu.NONE, context.getString(R.string.explode))
                hasItems = true
            }

            popupMenu.setOnDismissListener {
                updateCurrentPropertiesButton()
            }

            if(hasItems)
            {
                currentPropertiesButton.setText(R.string.tap_to_remove)

                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.string.advantage -> listener.advantageDisadvantageChanged(id, rollNaturalValue)
                        R.string.disadvantage -> listener.advantageDisadvantageChanged(id, rollNaturalValue)
                        R.string.drop_highest -> listener.dropHighChanged(id, 0)
                        R.string.drop_lowest -> listener.dropLowChanged(id, 0)
                        R.string.keep_highest -> listener.keepHighChanged(id, 0)
                        R.string.keep_lowest -> listener.keepLowChanged(id, 0)
                        R.string.re_roll_dice -> listener.reRollCleared(id)
                        R.string.minimum_die_value -> listener.minimumDieValueCleared(id)
                        R.string.explode -> listener.explodeChanged(id, false)
                    }

                    true
                }

                popupMenu.show()
            }

        }
        updateCurrentPropertiesButton()
    }

    private fun setDropHigh() {
        EditDialogs(context, layoutInflater).createNumberDialog(
            context.getString(R.string.drop_highest),
            "How many dice to drop from the roll?",
            0,
            MAX_BOUNDING_VALUE,
            max(0,listener.getCurrentProperties(id).mDropHigh),
            object : EditDialogs.NumberDialogListener {
                override fun respondToOK(number: Int) {
                    listener.dropHighChanged(id, number)
                    updateCurrentPropertiesButton()
                }
            })
    }

    private fun setDropLow() {
        EditDialogs(context, layoutInflater).createNumberDialog(
            context.getString(R.string.drop_lowest),
            "How many dice to drop from the roll?",
            0,
            MAX_BOUNDING_VALUE,
            max(0,listener.getCurrentProperties(id).mDropLow),
            object : EditDialogs.NumberDialogListener {
                override fun respondToOK(number: Int) {
                    listener.dropLowChanged(id, number)
                    updateCurrentPropertiesButton()
                }
            })
    }

    private fun setKeepHigh() {
        EditDialogs(context, layoutInflater).createNumberDialog(
            context.getString(R.string.keep_highest),
            "How many highest dice to keep in the roll?",
            0,
            MAX_BOUNDING_VALUE,
            max(0,listener.getCurrentProperties(id).mKeepHigh),
            object : EditDialogs.NumberDialogListener {
                override fun respondToOK(number: Int) {
                    listener.keepHighChanged(id, number)
                    updateCurrentPropertiesButton()
                }
            })
    }

    private fun setKeepLow() {
        EditDialogs(context, layoutInflater).createNumberDialog(
            context.getString(R.string.keep_lowest),
            "How many lowest dice to keep in the roll?",
            0,
            MAX_BOUNDING_VALUE,
            max(0,listener.getCurrentProperties(id).mKeepLow),
            object : EditDialogs.NumberDialogListener {
                override fun respondToOK(number: Int) {
                    listener.keepLowChanged(id, number)
                    updateCurrentPropertiesButton()
                }
            })
    }

    private fun setReRoll() {
        EditDialogs(context, layoutInflater).createNumberDialog(
            context.getString(R.string.re_roll_dice),
            "Re-Roll any die with value less than or equal to what you set here (once).",
            MIN_BOUNDING_VALUE,
            MAX_BOUNDING_VALUE,
            max(0,listener.getCurrentProperties(id).mReRoll),
            object : EditDialogs.NumberDialogListener {
                override fun respondToOK(number: Int) {
                    listener.reRollSet(id, number)
                    updateCurrentPropertiesButton()
                }
            })
    }

    private fun setMinimumDieValue() {
        EditDialogs(context, layoutInflater).createNumberDialog(
            context.getString(R.string.minimum_die_value),
            "The minimum value that a die can roll is what you set here.",
            MIN_BOUNDING_VALUE,
            MAX_BOUNDING_VALUE,
            max(0,listener.getCurrentProperties(id).mMinimumRoll),
            object : EditDialogs.NumberDialogListener {
                override fun respondToOK(number: Int) {
                    listener.minimumDieValueSet(id, number)
                    updateCurrentPropertiesButton()
                }
            })
    }

    private fun updateCurrentPropertiesButton() {
        var numProperties = 0

        val defaultProps = RollProperties()
        val rollProps = listener.getCurrentProperties(id)

        if(rollProps.mAdvantageDisadvantage != defaultProps.mAdvantageDisadvantage) {
            numProperties += 1
        }

        if(rollProps.mDropHigh != defaultProps.mDropHigh) {
            numProperties += 1
        }

        if(rollProps.mDropLow != defaultProps.mDropLow) {
            numProperties += 1
        }

        if(rollProps.mKeepHigh != defaultProps.mKeepHigh) {
            numProperties += 1
        }

        if(rollProps.mKeepLow != defaultProps.mKeepLow) {
            numProperties += 1
        }

        if(rollProps.mUseReRoll != defaultProps.mUseReRoll && rollProps.mReRoll != defaultProps.mReRoll) {
            numProperties += 1
        }

        if(rollProps.mUseMinimumRoll != defaultProps.mUseMinimumRoll && rollProps.mMinimumRoll != defaultProps.mMinimumRoll) {
            numProperties += 1
        }

        if(rollProps.mExplode != defaultProps.mExplode) {
            numProperties += 1
        }

        currentPropertiesButton.text = if(numProperties == 0) {
            "No Properties"
        } else {
            "$numProperties Properties"
        }
    }

    interface PropertyChangeListener {
        fun advantageDisadvantageChanged(id: Int, mode: Int)
        fun dropHighChanged(id: Int, dropValue: Int)
        fun dropLowChanged(id: Int, dropValue: Int)
        fun keepHighChanged(id: Int, keepValue: Int)
        fun keepLowChanged(id: Int, keepValue: Int)
        fun reRollSet(id: Int, threshold: Int)
        fun reRollCleared(id: Int)
        fun minimumDieValueSet(id: Int, threshold: Int)
        fun minimumDieValueCleared(id: Int)
        fun explodeChanged(id: Int, explode: Boolean)
        fun getCurrentProperties(id: Int) : RollProperties
    }
}
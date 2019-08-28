package com.fialasfiasco.customdiceroller.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.widget.Button
import android.widget.PopupMenu
import com.fialasfiasco.customdiceroller.R
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

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.string.advantage -> listener.advantageDisadvantageChanged(id, rollAdvantageValue)
                    R.string.disadvantage -> listener.advantageDisadvantageChanged(id, rollDisadvantageValue)
                    R.string.drop_highest -> setDropHigh()
                    R.string.drop_lowest -> setDropLow()
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

            if(rollProps.mDropHighLow != defaultProps.mDropHighLow) {
                popupMenu.menu?.add(Menu.NONE, R.string.drop_high_low, Menu.NONE, getDropDiceString(rollProps.mDropHighLow))
                hasItems = true
            }

            if(hasItems)
            {
                currentPropertiesButton.setText(R.string.tap_to_remove)

                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.string.advantage -> listener.advantageDisadvantageChanged(id, rollNaturalValue)
                        R.string.disadvantage -> listener.advantageDisadvantageChanged(id, rollNaturalValue)
                        R.string.drop_high_low -> listener.dropHighLowChanged(id, 0)
                    }

                    true
                }
            }
            else {
                popupMenu.menu?.add(Menu.NONE, Menu.NONE, Menu.NONE, "No Properties")
            }

            popupMenu.setOnDismissListener {
                updateCurrentPropertiesButton()
            }

            popupMenu.show()
        }
        updateCurrentPropertiesButton()
    }

    private fun setDropHigh() {
        EditDialogs(context, layoutInflater).createNumberDialog(
            "How many highest to drop?",
            "",
            0,
            MAX_BOUNDING_VALUE,
            max(0,-listener.getCurrentProperties(id).mDropHighLow),
            object : EditDialogs.NumberDialogListener {
                override fun respondToOK(outputValue: Int) {
                    listener.dropHighLowChanged(id, -outputValue)
                    updateCurrentPropertiesButton()
                }
            })
    }

    private fun setDropLow() {
        EditDialogs(context, layoutInflater).createNumberDialog(
            "How many lowest to drop?",
            "",
            0,
            MAX_BOUNDING_VALUE,
            max(0,listener.getCurrentProperties(id).mDropHighLow),
            object : EditDialogs.NumberDialogListener {
                override fun respondToOK(outputValue: Int) {
                    listener.dropHighLowChanged(id, outputValue)
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

        if(rollProps.mDropHighLow != defaultProps.mDropHighLow) {
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
        fun dropHighLowChanged(id: Int, dropValue: Int)
        fun getCurrentProperties(id: Int) : RollProperties
    }
}
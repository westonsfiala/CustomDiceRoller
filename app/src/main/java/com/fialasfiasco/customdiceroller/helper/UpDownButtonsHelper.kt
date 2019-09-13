package com.fialasfiasco.customdiceroller.helper

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import com.fialasfiasco.customdiceroller.data.*

class UpDownButtonsHelper(private val context: Context,
                          private val layoutInflater: LayoutInflater,
                          private val upButton: ImageButton,
                          private val downButton: ImageButton,
                          private val upDownDisplayText: TextView,
                          private val id: Int,
                          private val listener: UpDownButtonsListener
) {

    init {
        setupUpButton()
        setupDownButton()
        setupDisplayText()
        updateDisplayText()
    }

    private fun setupUpButton() {
        upButton.setOnClickListener  {
            listener.upButtonClick(id)
            updateDisplayText()
        }
        upButton.setOnLongClickListener {
            listener.upButtonLongClick(id)
            updateDisplayText()
            true
        }
    }

    private fun setupDownButton() {
        downButton.setOnClickListener  {
            listener.downButtonClick(id)
            updateDisplayText()
        }
        downButton.setOnLongClickListener {
            listener.downButtonLongClick(id)
            updateDisplayText()
            true
        }
    }

    private fun setupDisplayText() {
        upDownDisplayText.setOnClickListener {
            EditDialogs(context, layoutInflater).createNumberDialog(
                "Set Exact Value",
                "",
                MIN_BOUNDING_VALUE,
                MAX_BOUNDING_VALUE,
                listener.getExactValue(id),
                object : EditDialogs.NumberDialogListener {
                    override fun respondToOK(number: Int) {
                        listener.setExactValue(id, number)
                        updateDisplayText()
                    }
                })
        }
    }

    private fun updateDisplayText() {
        upDownDisplayText.text = listener.getDisplayText(id)
    }

    interface UpDownButtonsListener {
        fun upButtonClick(id: Int)
        fun upButtonLongClick(id: Int)

        fun downButtonClick(id: Int)
        fun downButtonLongClick(id: Int)

        fun setExactValue(id: Int, value: Int)

        fun getExactValue(id: Int) : Int

        fun getDisplayText(id: Int) : CharSequence
    }

}

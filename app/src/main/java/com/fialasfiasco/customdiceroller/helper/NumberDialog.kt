package com.fialasfiasco.customdiceroller.helper

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.fialasfiasco.customdiceroller.R
import java.lang.NumberFormatException

class NumberDialog(private val context: Context?,
                   private val layoutInflater: LayoutInflater)
{

    fun createDialog(title: String, lowerBound: Int, upperBound: Int, startingValue: Int, listener: NumberDialogListener)
    {
        val builder = AlertDialog.Builder(context)

        val numberView = layoutInflater.inflate(R.layout.dialog_number_edit, null)
        builder.setView(numberView)

        val editLine = numberView.findViewById<EditText>(R.id.numberEditId)
        editLine.setText(startingValue.toString())
        editLine.selectAll()
        editLine.requestFocusFromTouch()

        builder.setTitle(title)
        builder.setMessage("Constrained between $lowerBound and $upperBound")

        builder.setPositiveButton("OK") {_, _ ->
            try {
                listener.respondToOK(editLine.text.toString().toInt())
            } catch (error: NumberFormatException) {
                Toast.makeText(context, "Issue creating die", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { _, _ -> }

        val dialog = builder.create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    interface NumberDialogListener
    {
        fun respondToOK(outputValue: Int)
    }
}
package com.fialasfiasco.customdiceroller.helper

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.fialasfiasco.customdiceroller.R
import java.lang.NumberFormatException

class CustomDieEditDialog(private val context: Context?,
                          private val layoutInflater: LayoutInflater)
{

    fun createDialog(title: String, lowerBound: Int, upperBound: Int, listener: CustomDieEditListener)
    {
        val builder = AlertDialog.Builder(context)

        val numberView = layoutInflater.inflate(R.layout.dialog_custom_die_edit, null)
        builder.setView(numberView)


        val nameEditLine = numberView.findViewById<EditText>(R.id.nameEditId)
        nameEditLine.setText(context?.getString(R.string.temp))
        nameEditLine.selectAll()
        nameEditLine.requestFocusFromTouch()

        val minEditLine = numberView.findViewById<EditText>(R.id.minEditId)
        minEditLine.setText("0")

        val maxEditLine = numberView.findViewById<EditText>(R.id.maxEditId)
        maxEditLine.setText("0")

        builder.setTitle(title)
        builder.setMessage("Min and Max constrained between $lowerBound and $upperBound\n" +
        "Shorter names display better")

        builder.setPositiveButton("OK") {_, _ ->
            try {
                val name = nameEditLine.text.toString()
                val min = minEditLine.text.toString().toInt()
                val max = maxEditLine.text.toString().toInt()
                listener.respondToOK(name, min, max)
            } catch (error: NumberFormatException) {
                Toast.makeText(context, "Issue creating custom die", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { _, _ -> }

        val dialog = builder.create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    interface CustomDieEditListener
    {
        fun respondToOK(name : String, min : Int, max : Int)
    }
}
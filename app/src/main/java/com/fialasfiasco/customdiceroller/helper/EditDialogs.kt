package com.fialasfiasco.customdiceroller.helper

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.fialasfiasco.customdiceroller.R
import java.lang.NumberFormatException

class EditDialogs(private val context: Context?,
                  private val layoutInflater: LayoutInflater)
{
    fun createNameMinMaxDialog(title: String, lowerBound: Int, upperBound: Int, listener: NameMinMaxDialogListener)
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
                Toast.makeText(context, "Issue with parsing numbers", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { _, _ -> }

        val dialog = builder.create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    interface NameMinMaxDialogListener
    {
        fun respondToOK(name : String, min : Int, max : Int)
    }

    fun createNumberDialog(title: String, lowerBound: Int, upperBound: Int, startingValue: Int, listener: NumberDialogListener)
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
                Toast.makeText(context, "Issue with parsing numbers", Toast.LENGTH_SHORT).show()
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

    fun createNameDialog(title: String, listener: NameDialogListener)
    {
        val builder = AlertDialog.Builder(context)

        val savedRollView = layoutInflater.inflate(R.layout.dialog_name_edit, null)
        builder.setView(savedRollView)

        val nameEditLine = savedRollView.findViewById<EditText>(R.id.nameEditId)
        nameEditLine.setText(context?.getString(R.string.temp))
        nameEditLine.selectAll()
        nameEditLine.requestFocusFromTouch()

        builder.setTitle(title)
        builder.setMessage("Shorter names display better")

        builder.setPositiveButton("OK") {_, _ ->
            val name = nameEditLine.text.toString()
            listener.respondToOK(name)
        }

        builder.setNegativeButton("Cancel") { _, _ -> }

        val dialog = builder.create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    interface NameDialogListener
    {
        fun respondToOK(name : String)
    }
}
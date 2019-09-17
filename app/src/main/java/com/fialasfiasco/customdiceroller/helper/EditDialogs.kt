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
    private val constrainedBetweenString = "Constrained between %d and %d\n"

    fun createNameCategoryDialog(title: String, message: String, defaultName: String, defaultCategory: String, listener: NameCategoryDialogListener)
    {
        val builder = AlertDialog.Builder(context)

        val nameCategoryView = layoutInflater.inflate(R.layout.dialog_name_category_edit, null)
        builder.setView(nameCategoryView)

        val nameEditLine = nameCategoryView.findViewById<EditText>(R.id.nameEditId)
        nameEditLine.setText(defaultName)
        nameEditLine.selectAll()
        nameEditLine.requestFocusFromTouch()

        val categoryEditLine = nameCategoryView.findViewById<EditText>(R.id.categoryEditId)
        categoryEditLine.setText(defaultCategory)

        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton("OK") {_, _ ->
            val name = nameEditLine.text.toString()
            val category = categoryEditLine.text.toString()
            listener.respondToOK(name, category)
        }

        builder.setNegativeButton("Cancel") { _, _ -> }

        val dialog = builder.create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    interface NameCategoryDialogListener
    {
        fun respondToOK(name : String, category : String)
    }

    fun createNumberDialog(title: String, message: String, lowerBound: Int, upperBound: Int, defaultValue: Int, listener: NumberDialogListener)
    {
        val builder = AlertDialog.Builder(context)

        val numberView = layoutInflater.inflate(R.layout.dialog_number_edit, null)
        builder.setView(numberView)

        val numberEditLine = numberView.findViewById<EditText>(R.id.numberEditId)
        numberEditLine.setText(defaultValue.toString())
        numberEditLine.selectAll()
        numberEditLine.requestFocusFromTouch()

        builder.setTitle(title)
        builder.setMessage(String.format(constrainedBetweenString,lowerBound, upperBound) +
                message)

        builder.setPositiveButton("OK") {_, _ ->
            try {
                listener.respondToOK(numberEditLine.text.toString().toInt())
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
        fun respondToOK(number: Int)
    }

    fun createNameDieDialog(title: String, message: String, lowerBound: Int, upperBound: Int, defaultName: String, defaultValue: Int, listener: NameDieDialogListener)
    {
        val builder = AlertDialog.Builder(context)

        val nameNumberView = layoutInflater.inflate(R.layout.dialog_name_number_edit, null)
        builder.setView(nameNumberView)

        val nameEditLine = nameNumberView.findViewById<EditText>(R.id.nameEditId)
        nameEditLine.setText(defaultName)
        nameEditLine.selectAll()
        nameEditLine.requestFocusFromTouch()

        val numberEditLine = nameNumberView.findViewById<EditText>(R.id.valueEditId)
        numberEditLine.setText(defaultValue.toString())

        builder.setTitle(title)
        builder.setMessage(String.format(constrainedBetweenString,lowerBound, upperBound) +
                message)

        builder.setPositiveButton("OK") {_, _ ->
            try {
                listener.respondToOK(nameEditLine.text.toString(), numberEditLine.text.toString().toInt())
            } catch (error: NumberFormatException) {
                Toast.makeText(context, "Issue with parsing numbers", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { _, _ -> }

        val dialog = builder.create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    interface NameDieDialogListener
    {
        fun respondToOK(name: String, number: Int)
    }

    fun createNameMinMaxDialog(title: String, message: String, lowerBound: Int, upperBound: Int,
                               defaultName: String, defaultMin: Int, defaultMax: Int, listener: NameMinMaxDialogListener)
    {
        val builder = AlertDialog.Builder(context)

        val numberView = layoutInflater.inflate(R.layout.dialog_name_min_max_edit, null)
        builder.setView(numberView)

        val nameEditLine = numberView.findViewById<EditText>(R.id.nameEditId)
        nameEditLine.setText(defaultName)
        nameEditLine.selectAll()
        nameEditLine.requestFocusFromTouch()

        val minEditLine = numberView.findViewById<EditText>(R.id.minEditId)
        minEditLine.setText(defaultMin.toString())

        val maxEditLine = numberView.findViewById<EditText>(R.id.maxEditId)
        maxEditLine.setText(defaultMax.toString())

        builder.setTitle(title)
        builder.setMessage(String.format(constrainedBetweenString,lowerBound, upperBound) + message)

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

    fun createNameFacesDialog(title: String, message: String, lowerBound: Int, upperBound: Int,
                              defaultName: String, defaultNumbers: List<Int>, listener: NameFacesDialogListener)
    {
        val builder = AlertDialog.Builder(context)

        val nameImbalancedView = layoutInflater.inflate(R.layout.dialog_name_imbalanced_edit, null)
        builder.setView(nameImbalancedView)

        builder.setTitle(title)
        builder.setMessage(String.format(constrainedBetweenString,lowerBound, upperBound) +
                "Use a comma separated list of numbers.\n" +
                message)

        val nameEditLine = nameImbalancedView.findViewById<EditText>(R.id.nameEditId)
        nameEditLine.setText(defaultName)
        nameEditLine.selectAll()
        nameEditLine.requestFocusFromTouch()

        val numbersEditLine = nameImbalancedView.findViewById<EditText>(R.id.numbersEditID)
        numbersEditLine.setText(defaultNumbers.joinToString(","))

        builder.setNegativeButton("Cancel") { _, _ -> }


        builder.setPositiveButton("OK") { _, _ ->
            try {

                val numberList = mutableListOf<Int>()

                val rawString = numbersEditLine.text.toString()

                val numberStrings = rawString.split(",")

                for(numberString in numberStrings) {
                    numberList.add(numberString.toInt())
                }

                listener.respondToOK(nameEditLine.text.toString(), numberList)
            } catch (error: NumberFormatException) {
                Toast.makeText(context, "Issue with parsing numbers", Toast.LENGTH_SHORT).show()
            }
        }

        val dialog = builder.create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    interface NameFacesDialogListener
    {
        fun respondToOK(name: String, numbers: List<Int>)
    }


}
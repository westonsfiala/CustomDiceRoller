package com.fialasfiasco.customdiceroller.aggregate_roller

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.helper.NumberDialog
import kotlinx.android.synthetic.main.fragment_aggregate_roll_layout.*
import java.lang.NumberFormatException

/**
 * A fragment representing a list of Items.
 */
class AggregateRollFragment : Fragment(), AggregateRollRecyclerViewAdapter.AggregateRollInterfaceListener {

    private lateinit var pageViewModel: PageViewModel

    private var itemsInRow = 2

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onStart() {
        super.onStart()
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        itemsInRow = preferences.getString(
            getString(R.string.items_per_row_custom_key),
            resources.getInteger(R.integer.items_per_row_custom_default).toString()
        )!!.toInt()

        aggregateRecycler.layoutManager = GridLayoutManager(context, itemsInRow)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_aggregate_roll_layout, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.aggregateRecycler)

        // Set the adapter
        recycler.layoutManager = GridLayoutManager(context, itemsInRow)
        recycler.adapter = AggregateRollRecyclerViewAdapter(pageViewModel, this)


        val rollButton = view.findViewById<Button>(R.id.rollButton)
        rollButton.setOnClickListener {

            NumberDialog(context, inflater).createDialog("test", 0, 100, 22,
                object : NumberDialog.NumberDialogListener {
                    override fun respondToOK(outputValue: Int) {
                        Toast.makeText(context, outputValue.toString(), Toast.LENGTH_LONG).show()
                    }
                })
        }

        AlertDialog.Builder(context)

        return view
    }

    override fun onDisplayTextClicked(holder : AggregateRollRecyclerViewAdapter.AggregateDieViewHolder, position: Int) {
        NumberDialog(context, layoutInflater).createDialog(
            "Number of Dice",
            0,
            100,
            pageViewModel.getAggregateDie(position).mDieCount,
            object : NumberDialog.NumberDialogListener {
                override fun respondToOK(outputValue: Int) {
                    try {
                        pageViewModel.setAggregateDieCount(pageViewModel.getAggregateDie(position).mSimpleDie.mDie, outputValue)
                        holder.mCount.text = pageViewModel.getAggregateDie(position).mDieCount.toString()
                    } catch (error: NumberFormatException) {
                    }
                }
            })
    }

    companion object {

        @JvmStatic
        fun newInstance() = AggregateRollFragment()
    }
}
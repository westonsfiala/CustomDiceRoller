package com.fialasfiasco.customdiceroller.aggregate_roller

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.helper.NumberDialog
import java.lang.NumberFormatException

/**
 * A fragment representing a list of Items.
 */
class AggregateRollerRecycler : Fragment(), AggregateRollerRecyclerViewAdapter.AggregateRollInterfaceListener {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_complex_roll, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.aggregateRecycler)

        // Set the adapter
        recycler.layoutManager = GridLayoutManager(context, 2)
        recycler.adapter = AggregateRollerRecyclerViewAdapter(pageViewModel, this)


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

    override fun onDisplayTextClicked(holder : AggregateRollerRecyclerViewAdapter.AggregateDieViewHolder, position: Int) {
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
        fun newInstance() = AggregateRollerRecycler()
    }
}
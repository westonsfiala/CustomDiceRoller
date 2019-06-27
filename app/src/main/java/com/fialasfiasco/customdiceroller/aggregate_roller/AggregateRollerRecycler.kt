package com.fialasfiasco.customdiceroller.aggregate_roller

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
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

        return view
    }

    override fun onDisplayTextClicked(holder : AggregateRollerRecyclerViewAdapter.AggregateDieViewHolder, position: Int) {
        val builder = AlertDialog.Builder(context)

        val numberView = layoutInflater.inflate(R.layout.number_edit_layout, null)

        val editLine = numberView.findViewById<EditText>(R.id.numberEditId)

        editLine.setText(pageViewModel.getAggregateDie(position).mDieCount.toString())
        editLine.selectAll()
        editLine.requestFocusFromTouch()

        builder.setView(numberView)

        builder.setTitle("Number of Dice")
        builder.setMessage("Constrained between 0 and 100")

        builder.setPositiveButton("OK") { _, _ ->
            try {
                pageViewModel.setAggregateDieCount(pageViewModel.getAggregateDie(position).mSimpleDie.mDie,
                    editLine.text.toString().toInt())
                holder.mCount.text = pageViewModel.getAggregateDie(position).mDieCount.toString()
            } catch (error: NumberFormatException) {
            }
        }
        builder.setNegativeButton("Cancel") { _, _ -> }

        val dialog = builder.create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    companion object {

        @JvmStatic
        fun newInstance() = AggregateRollerRecycler()
    }
}
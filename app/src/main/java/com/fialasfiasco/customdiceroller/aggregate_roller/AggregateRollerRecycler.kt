package com.fialasfiasco.customdiceroller.aggregate_roller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel


/**
 * A fragment representing a list of Items.
 */
class AggregateRollerRecycler : Fragment() {

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
        recycler.adapter = AggregateRollerRecyclerViewAdapter(pageViewModel)

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance() = AggregateRollerRecycler()
    }
}
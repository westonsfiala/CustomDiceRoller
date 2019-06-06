package com.fialasfiasco.customdiceroller.ui.main


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.fialasfiasco.customdiceroller.R



class HistoryStamp
{
    private var rollResult = 0
    private var rollText = "temp"
    private var rollDetails = "temp"
    private var timeStamp = "temp"

    fun createView(inflater: LayoutInflater) : View
    {

        val createdView = inflater.inflate(R.layout.roll_history_layout, null)

        createdView.findViewById<TextView>(R.id.rollResultText).text = "$rollResult"
        createdView.findViewById<TextView>(R.id.rollTitleText).text = rollText
        createdView.findViewById<TextView>(R.id.rollDetailsText).text = rollDetails
        createdView.findViewById<TextView>(R.id.rollTimeStampText).text = timeStamp

        return createdView
    }

    companion object {

    @JvmStatic
    fun newInstance(_rollResult: Int,
                    _rollText: String,
                    _rollDetails: String,
                    _timeStamp: String) =
        HistoryStamp().apply {
            rollResult = _rollResult
            rollText = _rollText
            rollDetails = _rollDetails
            timeStamp = _timeStamp
        }
}
}

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HistoryFragment : androidx.fragment.app.Fragment() {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        pageViewModel.singleRollHistory.observe(this, Observer<HistoryStamp> {
            addHistory(view!!,it)
        })

        pageViewModel.clearHistory.observe(this, Observer<Boolean> {
            clearHistory()
        })

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    private fun addHistory(view: View, history: HistoryStamp?)
    {
        val historyLayout = view.findViewById<LinearLayout>(R.id.historyLayout)
        historyLayout?.addView(history?.createView(LayoutInflater.from(context)),0)
    }

    private fun clearHistory()
    {
        val historyLayout = view?.findViewById<LinearLayout>(R.id.historyLayout)
        historyLayout?.removeAllViews()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment HistoryFragment.
         */
        @JvmStatic
        fun newInstance() =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

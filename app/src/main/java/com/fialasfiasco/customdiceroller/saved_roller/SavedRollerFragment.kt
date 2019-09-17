package com.fialasfiasco.customdiceroller.saved_roller

import android.app.AlertDialog
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.*
import com.fialasfiasco.customdiceroller.dice.Roll
import com.fialasfiasco.customdiceroller.helper.*
import com.fialasfiasco.customdiceroller.history.HistoryStamp
import kotlinx.android.synthetic.main.fragment_saved_roll.*
import kotlin.math.min

/**
 * A simple [Fragment] subclass.
 * Use the [SavedRollerFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SavedRollerFragment : androidx.fragment.app.Fragment(),
    SavedRollerRecyclerViewAdapter.OnSavedRollViewInteractionListener,
    DiceRollerDialog.DiceRollerListener{

    private lateinit var pageViewModel: PageViewModel

    private var rollerDialog : DiceRollerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onStart() {
        super.onStart()
        setupObservers()
        setupDiceButtons()
        setupNoSavedRollsText()
    }

    private fun setupObservers() {
        pageViewModel.savedRollPool.observe(this, Observer<Set<String>> {
            savedRollViewRecycler.adapter?.notifyDataSetChanged()
            setupNoSavedRollsText()
        })
    }

    private fun setupDiceButtons() {
        // Set the adapter
        savedRollViewRecycler.layoutManager = LinearLayoutManager(context)
        savedRollViewRecycler.adapter = SavedRollerCategoryRecyclerViewAdapter(context!!, pageViewModel, this)
        savedRollViewRecycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    private fun setupNoSavedRollsText()
    {
        if(pageViewModel.getNumSavedRollCategories() == 0)
        {
            noSavedRollsText.visibility = TextView.VISIBLE
        }
        else
        {
            noSavedRollsText.visibility = TextView.INVISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        rollerDialog?.resume()
    }

    override fun onPause() {
        super.onPause()
        rollerDialog?.pause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupRollerDialog()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_roll, container, false)
    }

    private fun setupRollerDialog()
    {
        val size = Point()
        activity?.windowManager?.defaultDisplay?.getSize(size)

        rollerDialog = DiceRollerDialog(
            context!!,
            activity,
            min(size.x, size.y),
            pageViewModel,
            this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rollerDialog?.kill()
        rollerDialog = null
    }

    override fun onRollClicked(roll: Roll) {
        rollerDialog?.runRoll(roll, pageViewModel.getShakeEnabled())
    }

    override fun onRemoveRollClicked(roll: Roll) {
        // Confirm the removal of roll
        val confirmRemoveBuilder = AlertDialog.Builder(context)

        confirmRemoveBuilder.setTitle("Remove - " + roll.getDisplayName())
        confirmRemoveBuilder.setMessage("Are you sure you wish to remove - " + roll.getDisplayName())

        confirmRemoveBuilder.setPositiveButton("Yes") { _, _ ->
            pageViewModel.removeSavedRollFromPool(roll)
        }
        confirmRemoveBuilder.setNegativeButton("No") { _, _ -> }

        confirmRemoveBuilder.show()
    }

    override fun onEditRollClicked(roll: Roll) {
        pageViewModel.beginRollEdit(roll)
    }

    override fun onRollResult(stamp: HistoryStamp) {
        pageViewModel.addRollHistory(stamp)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SimpleRollFragment.
         */
        @JvmStatic
        fun newInstance() = SavedRollerFragment()
    }
}
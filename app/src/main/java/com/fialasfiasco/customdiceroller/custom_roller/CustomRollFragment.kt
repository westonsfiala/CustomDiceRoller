package com.fialasfiasco.customdiceroller.custom_roller

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.*
import com.fialasfiasco.customdiceroller.dice.DieLoadError
import com.fialasfiasco.customdiceroller.dice.Roll
import com.fialasfiasco.customdiceroller.dice.saveSplitStrings
import com.fialasfiasco.customdiceroller.helper.*
import com.fialasfiasco.customdiceroller.history.HistoryStamp
import kotlinx.android.synthetic.main.fragment_custom_roll.*
import kotlin.math.min

/**
 * A fragment representing a list of Items.
 */
class CustomRollFragment : Fragment(),
    CustomRollRecyclerViewAdapter.CustomRollInterfaceListener,
    DiceRollerDialog.DiceRollerListener
{
    private lateinit var pageViewModel: PageViewModel

    private var rollerDialog : DiceRollerDialog? = null

    private var mLastSavedName = ""
    private var mLastSavedCategory = ""


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onStart() {
        super.onStart()
        setupRecycler()
        setupObservers()
        setupAddDieButton()
        setupSaveButton()
        setupRollButton()
        setupNoDieInRollText()
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
        return inflater.inflate(R.layout.fragment_custom_roll, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rollerDialog?.kill()
        rollerDialog = null
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

    private fun setupRecycler()
    {
        // Set the adapter
        customRecycler.layoutManager = LinearLayoutManager(context)
        customRecycler.adapter = CustomRollRecyclerViewAdapter(context!!, layoutInflater, pageViewModel, this)
    }

    private fun setupObservers()
    {
        // Notify about new items and then scroll to the top.
        pageViewModel.customDiePool.observe(this, Observer<Roll> {
            mLastSavedName = it.getDisplayName()
            mLastSavedCategory = it.getCategoryName()

            customRecycler.adapter?.notifyDataSetChanged()
            setupNoDieInRollText()
        })
    }

    private fun setupAddDieButton() {
        addDieButton.setOnClickListener {
            val popupMenu = PopupMenu(context, addDieButton)

            for (dieIndex in 0 until pageViewModel.getNumberDiceItems())
            {
                val die = pageViewModel.getDie(dieIndex)
                val item = popupMenu.menu?.add(Menu.NONE, dieIndex, Menu.NONE, die.getDisplayName())
                //val dieDrawable  = ThemedDieImageGetter(context!!, pageViewModel).getDieVectorDrawable(die.getImageID())
                //item?.icon = dieDrawable

                // This weird block is to force the icon to show up. Not sure why it doesn't by its own.
                //try {
                //    val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                //    fieldMPopup.isAccessible = true
                //    val mPopup = fieldMPopup.get(popupMenu)
                //    mPopup.javaClass
                //        .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                //        .invoke(mPopup, true)
                //} catch (e: Exception){
                //}
            }

            popupMenu.setOnMenuItemClickListener {
                val newDie = pageViewModel.getNextValidDieForCustomRoll(pageViewModel.getDie(it.itemId))
                if(!pageViewModel.addDieToCustomRoll(newDie)) {
                    Toast.makeText(context, "Unable to add die to roll", Toast.LENGTH_SHORT).show()
                } else {
                    customRecycler.adapter?.notifyDataSetChanged()
                    setupNoDieInRollText()
                }

                true
            }

            popupMenu.show()
        }
    }

    private fun setupSaveButton()
    {
        saveButton.setOnClickListener {

            val tempRoll = pageViewModel.createRollFromCustomRollerState("", "")
            if(tempRoll.getTotalDiceInRoll() == 0)
            {
                Toast.makeText(context, "A roll must have at least one die", Toast.LENGTH_SHORT).show()
            } else {
                EditDialogs(context, layoutInflater).createNameCategoryDialog(
                    "Name & Category of roll",
                    "Choose something memorable.",
                    mLastSavedName,
                    mLastSavedCategory,
                    object : EditDialogs.NameCategoryDialogListener {
                        override fun respondToOK(name: String, category: String) {
                            createSavedRoll(name, category, false)
                        }

                        override fun existingCategories() : List<String> {
                            return pageViewModel.getExistingCategories()
                        }
                    })
            }
        }
    }

    private fun createSavedRoll(name: String, category: String, override: Boolean) {

        for(disallowedNames in saveSplitStrings)
        {
            if(name.contains(disallowedNames))
            {
                Toast.makeText(context,"Roll names may not contain \"$disallowedNames\"", Toast.LENGTH_SHORT).show()
                return
            }
            if(category.contains(disallowedNames))
            {
                Toast.makeText(context,"Roll categories may not contain \"$disallowedNames\"", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val tempOverride = if(mLastSavedName == name && mLastSavedCategory == category) {
            true
        } else {
            override
        }

        if(!tempOverride && pageViewModel.hasSavedRollByName(name, category)) {
            val dialog = AlertDialog.Builder(context)

            dialog.setTitle("Roll with name - $name - already exists")
            dialog.setMessage("Would you like to override the existing roll?")
            dialog.setPositiveButton("Yes") { _,_ -> createSavedRoll(name, category, true)}
            dialog.setNegativeButton("No") {_,_ -> }
            dialog.show()
            return
        }

        mLastSavedName = name
        mLastSavedCategory = category

        try {
            val newRoll = pageViewModel.createRollFromCustomRollerState(name, category)
            if(!pageViewModel.addSavedRollToPool(newRoll, tempOverride)) {
                Toast.makeText(context, "Problem making the $name roll", Toast.LENGTH_LONG).show()
            }
        }
        catch (error : DieLoadError)
        {
            Toast.makeText(context, "Problem making the $name roll", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRollButton()
    {
        rollButton.setOnClickListener {
            val customRoll = pageViewModel.createRollFromCustomRollerState("", "")

            rollerDialog?.runRoll(customRoll, pageViewModel.getShakeEnabled())
        }
    }

    private fun setupNoDieInRollText()
    {
        if(pageViewModel.getNumberCustomRollItems() == 0)
        {
            noDieInRollText.visibility = TextView.VISIBLE
        }
        else
        {
            noDieInRollText.visibility = TextView.INVISIBLE
        }
    }

    override fun onNumberDiceInRollChange() {
        setupNoDieInRollText()
    }

    override fun onRollResult(stamp: HistoryStamp) {
        pageViewModel.addRollHistory(stamp)
    }

    companion object {

        @JvmStatic
        fun newInstance() = CustomRollFragment()
    }
}
package com.fialasfiasco.customdiceroller.helper

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fialasfiasco.customdiceroller.R
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.fragment_up_down_buttons.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [UpDownButtonsFragment.UpDownButtonsListener] interface
 * to handle interaction events.
 *
 */
class UpDownButtonsFragment : androidx.fragment.app.Fragment() {
    private var listener: UpDownButtonsListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_up_down_buttons, container)
    }

    fun setListener(newListener: UpDownButtonsListener)
    {
        listener = newListener

        upButton.setOnClickListener  {
            listener?.upButtonClick(this)
        }
        upButton.setOnLongClickListener {
            listener?.upButtonLongClick(this)
            true
        }

        downButton.setOnClickListener {
            listener?.downButtonClick(this)
        }
        downButton.setOnLongClickListener {
            listener?.downButtonLongClick(this)
            true
        }

        displayText.setOnClickListener {
            listener?.displayTextClick(this)
        }
    }

    fun setDisplayText(display : String)
    {
        displayText?.text = display
    }

    interface UpDownButtonsListener {
        fun upButtonClick(upDownButtonsFragment: UpDownButtonsFragment)
        fun upButtonLongClick(upDownButtonsFragment: UpDownButtonsFragment)

        fun downButtonClick(upDownButtonsFragment: UpDownButtonsFragment)
        fun downButtonLongClick(upDownButtonsFragment: UpDownButtonsFragment)

        fun displayTextClick(upDownButtonsFragment: UpDownButtonsFragment)
    }

}

package com.fialasfiasco.customdiceroller.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.fialasfiasco.customdiceroller.R
import kotlinx.android.synthetic.main.fragment_roll.*

private const val ARG_DICE_NUMBER = "diceNumber"
private const val ARG_IMAGE_ID = "imageID"

/**
 * A simple [Fragment] subclass.
 * Use the [RollFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RollFragment : Fragment() {
    private var rollName: String = "TEMP"
    private var dieNumber = 0
    private var imageID: Int = R.drawable.ic_d20_icon
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val diceNumber = it.getInt(ARG_DICE_NUMBER,1)
            rollName = "d$diceNumber"
            dieNumber = diceNumber
            imageID = it.getInt(ARG_IMAGE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val createdView = inflater.inflate(R.layout.fragment_roll, container, false)

        val icon = createdView.findViewById<ImageButton>(R.id.displayImage)
        icon.setImageResource(imageID)
        val containerWidth = container?.width
        icon.maxWidth = containerWidth!!.div(4)

        createdView.findViewById<TextView>(R.id.displayText).text = rollName

        return createdView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        displayImage.setOnClickListener {
            mListener?.onRollClicked(this)
        }
    }

    fun getDiceNumber() : Int
    {
        return dieNumber
    }

    fun getDiceImageID() : Int
    {
        return imageID
    }

    interface OnFragmentInteractionListener
    {
        fun onRollClicked(rollFragment: RollFragment)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param dice Parameter 1.
         * @param imageID Parameter 2.
         * @param listener Parameter 3.
         * @return A new instance of fragment RollFragment.
         */
        @JvmStatic
        fun newInstance(dice: Int, imageID: Int, listener: OnFragmentInteractionListener) =
            RollFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_DICE_NUMBER, dice)
                    putInt(ARG_IMAGE_ID, imageID)
                }
                mListener = listener
            }
    }
}

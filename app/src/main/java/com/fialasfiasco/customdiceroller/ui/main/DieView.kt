package com.fialasfiasco.customdiceroller.ui.main

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import com.fialasfiasco.customdiceroller.R
import kotlinx.android.synthetic.main.die_view_layout.view.*

class DieView: ConstraintLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context?, attributeSet: AttributeSet, defStyleAddr : Int) : super(context, attributeSet, defStyleAddr)

    init {
        LayoutInflater.from(context).inflate(R.layout.die_view_layout, this, true)
    }

    private var mImageID: Int = R.drawable.ic_d20_icon
    private var mDieLookupId: Int = 0
    private var mListener: OnDieViewInteractionListener? = null

    fun attachAndInitialize(dieName: String, imageID: Int, dieLookupId: Int, listener: OnDieViewInteractionListener)
    {
        displayText.text = dieName
        mImageID = imageID
        dieDisplay.setImageResource(mImageID)
        mDieLookupId = dieLookupId
        mListener = listener

        dieViewLayout.setOnClickListener {
            mListener?.onDieClicked(this)
        }
    }

    fun getDiceImageID() : Int
    {
        return mImageID
    }

    fun getDiceLookupId() : Int
    {
        return mDieLookupId
    }

    interface OnDieViewInteractionListener
    {
        fun onDieClicked(dieView: DieView)
    }
}

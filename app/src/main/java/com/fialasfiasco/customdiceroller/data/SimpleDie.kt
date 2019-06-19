package com.fialasfiasco.customdiceroller.data

import com.fialasfiasco.customdiceroller.R

class SimpleDie(val mDie : Int)
{
    val mImageID = when(mDie)
    {
        2 -> R.drawable.ic_d2
        4 -> R.drawable.ic_d4
        6 -> R.drawable.ic_d6
        8 -> R.drawable.ic_d8
        10 -> R.drawable.ic_d10
        12 -> R.drawable.ic_d12
        20 -> R.drawable.ic_d20
        100 -> R.drawable.ic_d10
        else -> R.drawable.ic_unknown
    }
}
package com.fialasfiasco.customdiceroller.data

import com.fialasfiasco.customdiceroller.R
import kotlin.random.Random

const val customDieStringStart = "Custom"
const val customDieSplitString = ":"

class CustomDie(private val mDieName: String, startPoint : Int, endpoint : Int) : InnerDie()
{

    private var mMinimum = 0
    private var mMaximum = 0

    init {
        if(mDieName.isEmpty())
        {
            throw DieLoadError()
        }

        mMinimum = Math.min(startPoint, endpoint)
        mMaximum = Math.max(startPoint, endpoint)

        if(mMinimum < MIN_DICE_NUM_NEGATIVE || mMaximum > MAX_DICE_NUM)
        {
            throw DieLoadError()
        }
    }

    override fun saveToString() : String
    {
        return String.format("%s:%s:%d:%d", customDieStringStart,mDieName,mMinimum,mMaximum)
    }

    override fun roll() : List<Int>
    {
        return listOf(Random.Default.nextInt(mMinimum, mMaximum+1))
    }

    override fun average() : Float
    {
        return (mMinimum + mMaximum) / 2.0f
    }

    override fun getName() : String
    {
        return mDieName
    }

    override fun getInfo() : String
    {
        return String.format("Rolls a number between %d and %d\nAverage of %d", mMinimum, mMaximum, average().toInt())
    }

    override fun getImageID() : Int
    {
        if(mDieName == "Fate")
        {
            return R.drawable.ic_fate
        }
        return R.drawable.ic_unknown
    }
}
package com.fialasfiasco.customdiceroller.data

import com.fialasfiasco.customdiceroller.R
import kotlin.random.Random

const val customDieStringStart = "Custom"
const val customDieSplitString = ":"
const val customDieDisplayInHexID = "0x"

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

        if(mMinimum < MIN_DICE_SIDE_COUNT_CUSTOM || mMaximum > MAX_DICE_SIDE_COUNT)
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

    override fun displayInHex(): Boolean {
        // Only display hex when you start with "0x" and have more characters after that.
        return mDieName.length > (customDieDisplayInHexID.length) && mDieName.startsWith(customDieDisplayInHexID)
    }

    override fun getName() : String
    {
        return mDieName
    }

    override fun getInfo() : String
    {
        if(displayInHex())
        {
            return String.format("Rolls a number between 0x%x and 0x%x\nAverage of 0x%x", mMinimum, mMaximum, average().toInt())
        }

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
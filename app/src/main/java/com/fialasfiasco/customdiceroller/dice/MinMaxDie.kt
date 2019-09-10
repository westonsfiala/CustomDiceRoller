package com.fialasfiasco.customdiceroller.dice

import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.MIN_BOUNDING_VALUE
import com.fialasfiasco.customdiceroller.data.MAX_BOUNDING_VALUE
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

const val minMaxDieStringStart = "MinMax"
const val customDieStringStartLegacy = "Custom"

class MinMaxDie(private val mDieName: String, startPoint : Int, endpoint : Int) : Die()
{

    private var mMinimum = 0
    private var mMaximum = 0

    init {
        if(mDieName.isEmpty())
        {
            throw DieLoadError()
        }

        mMinimum = min(startPoint, endpoint)
        mMaximum = max(startPoint, endpoint)

        if(mMinimum < MIN_BOUNDING_VALUE || mMaximum > MAX_BOUNDING_VALUE)
        {
            throw DieLoadError()
        }
    }

    override fun saveToString() : String
    {
        return String.format("%s%s%s%s%d%s%d", minMaxDieStringStart,
            saveSplitStrings[dieSplitStringIndex], mDieName,
            saveSplitStrings[dieSplitStringIndex], mMinimum,
            saveSplitStrings[dieSplitStringIndex], mMaximum)
    }

    override fun roll() :  Int
    {
        return Random.Default.nextInt(mMinimum, mMaximum+1)
    }

    override fun max(): Int {
        return mMaximum
    }

    override fun min(): Int {
        return mMinimum
    }

    override fun average() : Float
    {
        return (mMinimum + mMaximum) / 2.0f
    }

    override fun displayInHex(): Boolean {
        // Only display hex when you start with "0x" and have more characters after that.
        return mDieName.length > (dieDisplayInHexID.length) && mDieName.startsWith(
            dieDisplayInHexID
        )
    }

    override fun getDisplayName() : String
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
        return when {
            mMinimum == -1 && mMaximum == 1 -> R.drawable.ic_fate
            mMinimum == 1 && mMaximum == 2 -> R.drawable.ic_d2
            mMinimum == 1 && mMaximum == 3 -> R.drawable.ic_d3
            mMinimum == 1 && mMaximum == 4 -> R.drawable.ic_d4
            mMinimum == 1 && mMaximum == 6 -> R.drawable.ic_d6
            mMinimum == 1 && mMaximum == 8 -> R.drawable.ic_d8
            mMinimum == 1 && mMaximum == 10 -> R.drawable.ic_d10
            mMinimum == 1 && mMaximum == 12 -> R.drawable.ic_d12
            mMinimum == 1 && mMaximum == 20 -> R.drawable.ic_d20
            mMinimum == 1 && mMaximum == 100 -> R.drawable.ic_d100
            else -> R.drawable.ic_unknown
        }
    }
}
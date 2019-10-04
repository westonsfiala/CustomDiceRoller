package com.fialasfiasco.customdiceroller.dice

import com.fialasfiasco.customdiceroller.data.*
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

const val minMaxDieStringStart = "MinMax"
const val customDieStringStartLegacy = "Custom"

class MinMaxDie(dieName: String, startPoint : Int, endpoint : Int) : Die(dieName)
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
            mMinimum == -1 && mMaximum == 1 -> DIE_FATE
            mMinimum == 1 && mMaximum == 2 -> DIE_2
            mMinimum == 1 && mMaximum == 3 -> DIE_3
            mMinimum == 1 && mMaximum == 4 -> DIE_4
            mMinimum == 1 && mMaximum == 6 -> DIE_6
            mMinimum == 1 && mMaximum == 8 -> DIE_8
            mMinimum == 1 && mMaximum == 10 -> DIE_10
            mMinimum == 1 && mMaximum == 12 -> DIE_12
            mMinimum == 1 && mMaximum == 20 -> DIE_20
            mMinimum == 1 && mMaximum == 100 -> DIE_100
            else -> DIE_UNKNOWN
        }
    }
}
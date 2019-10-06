package com.fialasfiasco.customdiceroller.dice

import com.fialasfiasco.customdiceroller.data.*
import kotlin.random.Random

const val simpleDieStringStart = "Simple"

class SimpleDie(dieName: String, private val mDie: Int) : Die(dieName)
{
    init {
        if(mDie < MIN_DICE_SIDE_COUNT_SIMPLE || mDie > MAX_BOUNDING_VALUE)
        {
            throw DieLoadError()
        }

        if(mDieName.isEmpty())
        {
            mDieName = String.format("d%d", mDie)
        }
    }

    override fun clone(newName: String): Die {
        return SimpleDie(newName, mDie)
    }

    override fun saveToString() : String
    {
        return String.format("%s%s%s%s%d",
            simpleDieStringStart,
            saveSplitStrings[dieSplitStringIndex], mDieName,
            saveSplitStrings[dieSplitStringIndex], mDie)
    }

    override fun roll() :  Int
    {
        return if(mDie == 0) {
            0
        } else {
            Random.Default.nextInt(1, mDie + 1)
        }
    }

    override fun max(): Int {
        return mDie
    }

    override fun min(): Int {
        return 1
    }

    override fun average() : Float
    {
        return (mDie + 1) / 2.0f
    }

    override fun getInfo() : String
    {
        return String.format("Rolls a number between 1 and %d\nAverage of %d", mDie, average().toInt())
    }

    override fun getImageID() : Int
    {
        return when(mDie)
        {
            2 -> DIE_2
            3 -> DIE_3
            4 -> DIE_4
            6 -> DIE_6
            8 -> DIE_8
            10 -> DIE_10
            12 -> DIE_12
            20 -> DIE_20
            100 -> DIE_100
            else -> DIE_UNKNOWN
        }
    }
}
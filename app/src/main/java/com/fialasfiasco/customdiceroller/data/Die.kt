package com.fialasfiasco.customdiceroller.data

import com.fialasfiasco.customdiceroller.R
import java.lang.NumberFormatException
import java.lang.RuntimeException
import kotlin.random.Random

class DieLoadError : RuntimeException()

open class Die
{
    private var mDie = 0

    constructor(saveString: String) {
        try {
            val splitSaveString = saveString.split(":")

            mDie = when {
                splitSaveString.size == 1 -> splitSaveString[0].toInt()
                splitSaveString.size == 2 -> splitSaveString[1].toInt()
                else -> throw DieLoadError()
            }
        }
        catch (error : NumberFormatException)
        {
            throw DieLoadError()
        }
    }

    constructor(die : Int)
    {
        mDie = die
    }

    open fun saveToString() : String
    {
        return String.format("Simple:%d",mDie)
    }

    open fun roll() : List<Int>
    {
        return listOf(Random.Default.nextInt(1, mDie + 1))
    }

    open fun average() : Float
    {
        return (mDie + 1) / 2.0f
    }

    open fun getName() : String
    {
        return String.format("d%d", mDie)
    }

    open fun getInfo() : String
    {
        return String.format("Rolls a number between 1 and %d\nAverage of %d", mDie, average().toInt())
    }

    open fun getImageID() : Int
    {
        return when(mDie)
        {
            2 -> R.drawable.ic_d2
            4 -> R.drawable.ic_d4
            6 -> R.drawable.ic_d6
            8 -> R.drawable.ic_d8
            10 -> R.drawable.ic_d10
            12 -> R.drawable.ic_d12
            20 -> R.drawable.ic_d20
            100 -> R.drawable.ic_d100
            else -> R.drawable.ic_unknown
        }
    }
}
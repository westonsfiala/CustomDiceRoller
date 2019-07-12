package com.fialasfiasco.customdiceroller.data

import com.fialasfiasco.customdiceroller.R
import java.lang.NumberFormatException
import kotlin.random.Random

class CustomDie
{
    private lateinit var mDieName : String
    private lateinit var mDieSides : List<Int>

    constructor(saveString: String) {
        try {
            val splitSaveString = saveString.split(":")

            if(splitSaveString.size != 3)
            {
                throw DieLoadError()
            }

            mDieName = splitSaveString[1]
            mDieSides = getSidesFromString(splitSaveString[2])
        }
        catch (error : NumberFormatException)
        {
            throw DieLoadError()
        }
    }

    constructor(dieName: String, dieSides : List<Int>)
    {
        mDieName = dieName
        mDieSides = dieSides
    }

    private fun getSidesFromString(sidesSaveString : String) : List<Int>
    {

    }

    override fun saveToString() : String
    {
        return String.format("Custom:%s:%s",mDie)
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
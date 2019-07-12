package com.fialasfiasco.customdiceroller.data

import java.lang.NumberFormatException

class DieFactory {

    fun createUnknownDie(saveString: String) : Die
    {
        return SimpleDie(1)
    }

    fun createSimpleDie(saveString: String) : Die
    {
        try {
            val splitSaveString = saveString.split(":")

            val dieNumber = when {
                splitSaveString.size == 1 -> splitSaveString[0].toInt()
                splitSaveString.size == 2 -> splitSaveString[1].toInt()
                else -> throw DieLoadError()
            }
            return Die(dieNumber)
        }
        catch (error : NumberFormatException)
        {
            throw DieLoadError()
        }
    }


    fun createAggregateDie(saveString: String) : AggregateDie
    {
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

    fun createCustomDie(saveString: String) : CustomDie
    {
        return Die(1)
    }
}
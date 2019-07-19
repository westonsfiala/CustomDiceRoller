package com.fialasfiasco.customdiceroller.data

import java.lang.NumberFormatException

class DieFactory {

    fun createUnknownDie(saveString: String) : Die
    {
        return when {
            saveString.startsWith(aggregateDieStringStart) -> createAggregateDie(saveString)
            saveString.startsWith(customDieStringStart) -> createCustomDie(saveString)
            else -> createSimpleDie(saveString)
        }
    }

    fun createUnknownInnerDie(saveString: String) : InnerDie
    {
        return when {
            saveString.startsWith(customDieStringStart) -> createCustomDie(saveString)
            else -> createSimpleDie(saveString)
        }
    }

    fun createSimpleDie(saveString: String) : SimpleDie
    {
        try {
            val splitSaveString = saveString.split(simpleDieSplitString)

            // If we only have a single string in the split it could a valid number.
            val dieNumber = when {
                splitSaveString.size == 1 -> splitSaveString[0].toInt()
                splitSaveString.size == 2 -> splitSaveString[1].toInt()
                else -> throw DieLoadError()
            }
            return SimpleDie(dieNumber)
        }
        catch (error : NumberFormatException)
        {
            throw DieLoadError()
        }
    }


    fun createAggregateDie(saveString: String) : AggregateRoll
    {
        try {
            val splitSaveString = saveString.split(aggregateDieSplitString)

            // Aggregate;InnerDie;DieCount
            if(splitSaveString.size != 3)
            {
                throw DieLoadError()
            }

            val innerDieString = splitSaveString[1]
            val dieCount = splitSaveString[2].toInt()

            return if(innerDieString.startsWith(customDieStringStart)) {
                val innerDie = createCustomDie(innerDieString)
                AggregateRoll(innerDie, dieCount)
            }
            else
            {
                val innerDie = createSimpleDie(innerDieString)
                AggregateRoll(innerDie, dieCount)
            }
        }
        catch (error : NumberFormatException)
        {
            throw DieLoadError()
        }
    }

    fun createCustomDie(saveString: String) : CustomDie
    {
        try {
            val splitSaveString = saveString.split(customDieSplitString)

            // Custom:Name:Min:Max
            if(splitSaveString.size != 4)
            {
                throw DieLoadError()
            }

            val name = splitSaveString[1]
            val min = splitSaveString[2].toInt()
            val max = splitSaveString[3].toInt()

            return CustomDie(name,min,max)
        }
        catch (error : NumberFormatException)
        {
            throw DieLoadError()
        }
    }
}
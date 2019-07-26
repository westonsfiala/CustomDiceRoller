package com.fialasfiasco.customdiceroller.data

import java.lang.NumberFormatException

class DieFactory {

    fun createUnknownDie(saveString: String) : Die
    {
        return when {
            saveString.startsWith(customDieStringStart) -> createCustomDie(saveString)
            else -> createSimpleDie(saveString)
        }
    }

    private fun createSimpleDie(saveString: String) : SimpleDie
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

    private fun createCustomDie(saveString: String) : CustomDie
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

    fun createRoll(saveString: String) : Roll
    {
        try {
            val splitSaveString = saveString.split(aggregateRollSplitString)

            // Aggregate;Name;Mod;InnerDie;DieCount(repeat)
            if(splitSaveString.size.rem(2) != 1)
            {
                throw DieLoadError()
            }

            val rollName = splitSaveString[1]
            val modifier = splitSaveString[2].toInt()

            val aggregateRoll = Roll(rollName, modifier)

            for(index in 3 until splitSaveString.size step 2) {
                val savedInnerDie = createUnknownDie(splitSaveString[index])
                val savedDieCount = splitSaveString[index+1].toInt()
                aggregateRoll.addDieToRoll(savedInnerDie, RollProperties(savedDieCount, 0, 0, 0))
            }

            return aggregateRoll
        }
        catch (error : NumberFormatException)
        {
            throw DieLoadError()
        }
    }
}
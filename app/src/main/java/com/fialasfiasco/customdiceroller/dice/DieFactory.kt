package com.fialasfiasco.customdiceroller.dice

import java.lang.NumberFormatException

val saveSplitStrings = arrayOf(
    "__DIE_SAVE_STRING_SPLITTER__",
    "__ROLL_SAVE_STRING_SPLITTER__",
    "__PROPERTIES_SAVE_STRING_SPLITTER__")

val legacySplitStrings = arrayOf(
    ":",
    ";"
)

const val dieSplitStringIndex = 0
const val rollSplitStringIndex = 1
const val rollPropertiesSplitStringIndex = 2

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
            val splitSaveString = if(saveString.contains(saveSplitStrings[dieSplitStringIndex])) {
                saveString.split(saveSplitStrings[dieSplitStringIndex])
            } else {
                saveString.split(legacySplitStrings[dieSplitStringIndex])
            }

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
            val splitSaveString = if(saveString.contains(saveSplitStrings[dieSplitStringIndex])) {
                saveString.split(saveSplitStrings[dieSplitStringIndex])
            } else {
                saveString.split(legacySplitStrings[dieSplitStringIndex])
            }
            // Custom:Name:Min:Max
            if(splitSaveString.size != 4)
            {
                throw DieLoadError()
            }

            val name = splitSaveString[1]
            val min = splitSaveString[2].toInt()
            val max = splitSaveString[3].toInt()

            return CustomDie(name, min, max)
        }
        catch (error : NumberFormatException)
        {
            throw DieLoadError()
        }
    }

    fun createRoll(saveString: String) : Roll
    {
        try {
            val splitSaveString = if(saveString.contains(saveSplitStrings[rollSplitStringIndex])) {
                saveString.split(saveSplitStrings[rollSplitStringIndex])
            } else {
                saveString.split(legacySplitStrings[rollSplitStringIndex])
            }

            // Aggregate;Name;Mod;InnerDie;DieProperties(repeat)
            if(splitSaveString.size.rem(2) != 1)
            {
                throw DieLoadError()
            }

            val rollName = splitSaveString[1]
            val modifier = splitSaveString[2].toInt()

            val aggregateRoll = Roll(rollName, modifier)

            for(index in 3 until splitSaveString.size step 2) {
                val savedInnerDie = createUnknownDie(splitSaveString[index])
                val savedDieProperties = splitSaveString[index+1].split(saveSplitStrings[rollPropertiesSplitStringIndex])

                val properties = when(savedDieProperties.size){
                    // Save scheme with only count
                    1 -> {RollProperties(savedDieProperties[0].toInt(), 0, 0, 0)}
                    // Save scheme with count, mod, adv/disadv, drop X
                    4 -> {RollProperties(
                        savedDieProperties[0].toInt(),
                        savedDieProperties[1].toInt(),
                        savedDieProperties[2].toInt(),
                        savedDieProperties[3].toInt())}
                    else -> throw DieLoadError()
                }

                aggregateRoll.addDieToRoll(savedInnerDie,properties)
            }

            return aggregateRoll
        }
        catch (error : NumberFormatException)
        {
            throw DieLoadError()
        }
    }
}
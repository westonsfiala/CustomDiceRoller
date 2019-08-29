package com.fialasfiasco.customdiceroller.dice

import java.lang.NumberFormatException
import kotlin.math.min

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

            // Aggregate;Name;Die;DieProperties(repeat)
            val rollName = splitSaveString[1]

            var dieRepeatStart = 2

            // Roll Modifiers were removed, but a released version had them in.
            var legacyModifier = 0
            var addToFirst = false
            if(splitSaveString.size.rem(2) == 1) {
                legacyModifier = splitSaveString[2].toInt()
                addToFirst = true
                dieRepeatStart += 1
            }

            val aggregateRoll = Roll(rollName)

            for(index in dieRepeatStart until splitSaveString.size step 2) {
                val savedInnerDie = createUnknownDie(splitSaveString[index])
                val savedDieProperties = splitSaveString[index+1].split(saveSplitStrings[rollPropertiesSplitStringIndex])

                val properties = when(savedDieProperties.size){
                    // Save scheme with only count
                    1 -> {RollProperties(
                        savedDieProperties[0].toInt(), // count
                        0, // modifier
                        0, // advantage/disadvantage
                        0, // drop high
                        0, // drop low
                        false,
                        0, // reroll under
                        false,
                        0, // minimum roll
                        false  // explode
                        )}
                    // Save scheme with count, mod, adv/disadv, drop X high/low
                    4 -> {RollProperties(
                        savedDieProperties[0].toInt(), // count
                        savedDieProperties[1].toInt(), // modifier
                        savedDieProperties[2].toInt(), // advantage/disadvantage
                        min(0, -savedDieProperties[3].toInt()), // drop high
                        min(0, savedDieProperties[3].toInt()), // drop low
                        false,
                        0, // reroll under
                        false,
                        0, // minimum roll
                        false  // explode
                    )}
                    // Save scheme with count, mod, advantage/disadvantage, drop X High, drop X Low, ReRoll Under X, Minimum Roll Value, Explode
                    10 -> {RollProperties(
                        savedDieProperties[0].toInt(), // count
                        savedDieProperties[1].toInt(), // modifier
                        savedDieProperties[2].toInt(), // advantage/disadvantage
                        savedDieProperties[3].toInt(), // drop high
                        savedDieProperties[4].toInt(), // drop low
                        savedDieProperties[5].toBoolean(), // use reroll
                        savedDieProperties[6].toInt(), // reroll
                        savedDieProperties[7].toBoolean(), // use minimum roll
                        savedDieProperties[8].toInt(), // minimum roll
                        savedDieProperties[9].toBoolean()  // explode
                    )}
                    else -> throw DieLoadError()
                }

                if(addToFirst)
                {
                    addToFirst = false
                    properties.mModifier += legacyModifier
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
package com.fialasfiasco.customdiceroller.helper

fun getNumDiceString(numDice : Int) : String
{
    return String.format("%dd", numDice)
}

fun getModifierString(mod : Int) : String
{
    return if(mod >= 0) {
        String.format("+%d", mod)
    }
    else
    {
        String.format("%d", mod)
    }
}

fun getDropHighString(drop : Int) : String
{
    return String.format("Drop %d Highest", drop)
}

fun getDropLowString(drop : Int) : String
{
    return String.format("Drop %d Lowest", drop)
}

fun getKeepHighString(drop : Int) : String
{
    return String.format("Keep %d Highest", drop)
}

fun getKeepLowString(drop : Int) : String
{
    return String.format("Keep %d Lowest", drop)
}

fun getReRollString(value : Int) : String
{
    return String.format("Re-Roll Die <= %d", value)
}

fun getMinimumDieValueString(value : Int) : String
{
    return String.format("Minimum Die Value = %d", value)
}
package com.fialasfiasco.customdiceroller.helper

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

fun getDropDiceString(drop : Int) : String
{
    return when {
        drop == 0 -> "Drop none"
        drop > 0 -> String.format("Drop %d lowest", drop)
        drop < 0 -> String.format("Drop %d highest", Math.abs(drop))
        else -> ""
    }
}
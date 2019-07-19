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
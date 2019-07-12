package com.fialasfiasco.customdiceroller.data

import com.fialasfiasco.customdiceroller.R

class AggregateDie(val mInnerDie : Die, val mDieCount : Int) : Die(-1)
{
    override fun roll() : List<Int>
    {
        val rollList = mutableListOf<Int>()
        for(rollNum in 0 until mDieCount)
        {
            val innerRoll = mInnerDie.roll()
            for(roll in innerRoll)
            {
                rollList.add(roll)
            }
        }
        return rollList
    }

    override fun average() : Float
    {
        return mDieCount * mInnerDie.average()
    }

    override fun getName() : String
    {
        val innerName = mInnerDie.getName()

        return if(innerName.startsWith("d"))
        {
            String.format("%d%s",mDieCount,mInnerDie.getName())
        }
        else
        {
            String.format("%dd%s", mDieCount, mInnerDie.getName())
        }
    }

    override fun getImageID(): Int {
        return R.drawable.ic_cubes
    }
}
package com.fialasfiasco.customdiceroller

import com.fialasfiasco.customdiceroller.dice.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RollSaveLoadTests {

    @Test
    fun createSaveLoad_Roll_Simple() {
        try {
            val roll = Roll("Fiala Strike", "Attack")
            roll.addDieToRoll(SimpleDie("d20", 20), RollProperties())
            val rollSaveString = roll.saveToString()
            val recreatedRoll = DieFactory().createRoll(rollSaveString)
            assertEquals(roll.getDisplayName(), recreatedRoll.getDisplayName())
            assertEquals(roll.getTotalDiceInRoll(), 1)
        } catch (error : DieLoadError) {
            assert(false)
        }
    }

    @Test
    fun createSaveLoad_Roll_Complicated() {
        try {
            val roll = Roll("Fiala Strike", "Attack")
            roll.addDieToRoll(SimpleDie("d20", 20), fullRollProperties)
            roll.addDieToRoll(MinMaxDie("d4", 1,4), fullRollProperties)
            roll.addDieToRoll(ImbalancedDie("fib", listOf(1,1,2,3,5,8)), fullRollProperties)
            val rollSaveString = roll.saveToString()
            val recreatedRoll = DieFactory().createRoll(rollSaveString)
            assertEquals(roll.getDisplayName(), recreatedRoll.getDisplayName())
            assertEquals(roll.getCategoryName(), recreatedRoll.getCategoryName())
            assertEquals(roll.getTotalDiceInRoll(), recreatedRoll.getTotalDiceInRoll())
        } catch (error : DieLoadError) {
            assert(false)
        }
    }

    @Test
    fun loadRoll_testProperties_default_1_7_7() {
        try {
            val recreatedRoll = DieFactory().createRoll(testSimplePropertiesString_1_7_7)
            assertEquals(recreatedRoll.getDisplayName(), "Fiala Strike")
            assertEquals(recreatedRoll.getTotalDiceInRoll(), 1)
            val dieProps = recreatedRoll.getRollPropertiesAt(0)
            val defaultProps = RollProperties()
            assertEquals(dieProps, defaultProps)
        } catch (error : DieLoadError) {
            assert(false)
        }
    }

    @Test
    fun loadRoll_testProperties_default_1_7_8() {
        try {
            val recreatedRoll = DieFactory().createRoll(testSimplePropertiesString_1_7_8)
            assertEquals(recreatedRoll.getDisplayName(), "Fiala Strike")
            assertEquals(recreatedRoll.getTotalDiceInRoll(), 1)
            val dieProps = recreatedRoll.getRollPropertiesAt(0)
            val defaultProps = RollProperties()
            assertEquals(dieProps, defaultProps)
        } catch (error : DieLoadError) {
            assert(false)
        }
    }

    @Test
    fun loadRoll_testProperties_full_1_7_7() {
        try {
            val recreatedRoll = DieFactory().createRoll(testComplicatedPropString_1_7_7)
            assertEquals(recreatedRoll.getDisplayName(), "Fiala Strike")
            val dieProps = recreatedRoll.getRollPropertiesAt(0)
            assertEquals(dieProps, fullRollProperties_1_7_7_expected)
        } catch (error : DieLoadError) {
            assert(false)
        }
    }

    @Test
    fun loadRoll_testProperties_full_1_7_8() {
        try {
            val recreatedRoll = DieFactory().createRoll(testComplicatedPropString_1_7_8)
            assertEquals(recreatedRoll.getDisplayName(), "Fiala Strike")
            val dieProps = recreatedRoll.getRollPropertiesAt(0)
            assertEquals(dieProps, fullRollProperties)
        } catch (error : DieLoadError) {
            assert(false)
        }
    }

    @Test
    fun loadRollString() {
        try {
            for(rollString in rollSaveStrings) {
                DieFactory().createRoll(rollString)
            }
        } catch (error : DieLoadError) {
            assert(false)
        }
    }

    companion object {
        val rollSaveStrings = arrayOf(
            "Aggregate__ROLL_SAVE_STRING_SPLITTER__Fiala Strike__ROLL_CATEGORY_SAVE_STRING_SPLITTER__Attack__ROLL_SAVE_STRING_SPLITTER__Simple__DIE_SAVE_STRING_SPLITTER__d20__DIE_SAVE_STRING_SPLITTER__20__ROLL_SAVE_STRING_SPLITTER__1__PROPERTIES_SAVE_STRING_SPLITTER__0__PROPERTIES_SAVE_STRING_SPLITTER__0__PROPERTIES_SAVE_STRING_SPLITTER__0__PROPERTIES_SAVE_STRING_SPLITTER__0__PROPERTIES_SAVE_STRING_SPLITTER__false__PROPERTIES_SAVE_STRING_SPLITTER__0__PROPERTIES_SAVE_STRING_SPLITTER__false__PROPERTIES_SAVE_STRING_SPLITTER__0__PROPERTIES_SAVE_STRING_SPLITTER__false", // Version 1.7.7
            "Aggregate__ROLL_SAVE_STRING_SPLITTER__Fiala Strike__ROLL_CATEGORY_SAVE_STRING_SPLITTER__Attack__ROLL_SAVE_STRING_SPLITTER__Simple__DIE_SAVE_STRING_SPLITTER__d20__DIE_SAVE_STRING_SPLITTER__20__ROLL_SAVE_STRING_SPLITTER__4__PROPERTIES_SAVE_STRING_SPLITTER__11__PROPERTIES_SAVE_STRING_SPLITTER__1__PROPERTIES_SAVE_STRING_SPLITTER__1__PROPERTIES_SAVE_STRING_SPLITTER__2__PROPERTIES_SAVE_STRING_SPLITTER__true__PROPERTIES_SAVE_STRING_SPLITTER__2__PROPERTIES_SAVE_STRING_SPLITTER__true__PROPERTIES_SAVE_STRING_SPLITTER__10__PROPERTIES_SAVE_STRING_SPLITTER__true", // Version 1.7.7
            "Aggregate__ROLL_SAVE_STRING_SPLITTER__Fiala Strike__ROLL_CATEGORY_SAVE_STRING_SPLITTER__Attack__ROLL_SAVE_STRING_SPLITTER__Simple__DIE_SAVE_STRING_SPLITTER__d20__DIE_SAVE_STRING_SPLITTER__20__ROLL_SAVE_STRING_SPLITTER__4__PROPERTIES_SAVE_STRING_SPLITTER__11__PROPERTIES_SAVE_STRING_SPLITTER__1__PROPERTIES_SAVE_STRING_SPLITTER__1__PROPERTIES_SAVE_STRING_SPLITTER__2__PROPERTIES_SAVE_STRING_SPLITTER__true__PROPERTIES_SAVE_STRING_SPLITTER__2__PROPERTIES_SAVE_STRING_SPLITTER__true__PROPERTIES_SAVE_STRING_SPLITTER__10__PROPERTIES_SAVE_STRING_SPLITTER__true__ROLL_SAVE_STRING_SPLITTER__MinMax__DIE_SAVE_STRING_SPLITTER__d4__DIE_SAVE_STRING_SPLITTER__1__DIE_SAVE_STRING_SPLITTER__4__ROLL_SAVE_STRING_SPLITTER__4__PROPERTIES_SAVE_STRING_SPLITTER__11__PROPERTIES_SAVE_STRING_SPLITTER__1__PROPERTIES_SAVE_STRING_SPLITTER__1__PROPERTIES_SAVE_STRING_SPLITTER__2__PROPERTIES_SAVE_STRING_SPLITTER__true__PROPERTIES_SAVE_STRING_SPLITTER__2__PROPERTIES_SAVE_STRING_SPLITTER__true__PROPERTIES_SAVE_STRING_SPLITTER__10__PROPERTIES_SAVE_STRING_SPLITTER__true__ROLL_SAVE_STRING_SPLITTER__Imbalanced__DIE_SAVE_STRING_SPLITTER__fib__DIE_SAVE_STRING_SPLITTER__1__FACE_DIE_SAVE_STRING_SPLITTER__1__FACE_DIE_SAVE_STRING_SPLITTER__2__FACE_DIE_SAVE_STRING_SPLITTER__3__FACE_DIE_SAVE_STRING_SPLITTER__5__FACE_DIE_SAVE_STRING_SPLITTER__8__ROLL_SAVE_STRING_SPLITTER__4__PROPERTIES_SAVE_STRING_SPLITTER__11__PROPERTIES_SAVE_STRING_SPLITTER__1__PROPERTIES_SAVE_STRING_SPLITTER__1__PROPERTIES_SAVE_STRING_SPLITTER__2__PROPERTIES_SAVE_STRING_SPLITTER__true__PROPERTIES_SAVE_STRING_SPLITTER__2__PROPERTIES_SAVE_STRING_SPLITTER__true__PROPERTIES_SAVE_STRING_SPLITTER__10__PROPERTIES_SAVE_STRING_SPLITTER__true", // Version 1.7.7
            "Aggregate__ROLL_SPLIT__Fiala Strike__CATEGORY_SPLIT__Attack__ROLL_SPLIT__Simple__DIE_SPLIT__d20__DIE_SPLIT__20__ROLL_SPLIT__1__PROP_SPLIT__0__PROP_SPLIT__0__PROP_SPLIT__0__PROP_SPLIT__0__PROP_SPLIT__false__PROP_SPLIT__0__PROP_SPLIT__false__PROP_SPLIT__0__PROP_SPLIT__false,", // Version 1.7.8
            "Aggregate__ROLL_SPLIT__Fiala Strike__CATEGORY_SPLIT__Attack__ROLL_SPLIT__Simple__DIE_SPLIT__d20__DIE_SPLIT__20__ROLL_SPLIT__4__PROP_SPLIT__11__PROP_SPLIT__1__PROP_SPLIT__1__PROP_SPLIT__2__PROP_SPLIT__true__PROP_SPLIT__2__PROP_SPLIT__true__PROP_SPLIT__10__PROP_SPLIT__true__ROLL_SPLIT__MinMax__DIE_SPLIT__d4__DIE_SPLIT__1__DIE_SPLIT__4__ROLL_SPLIT__4__PROP_SPLIT__11__PROP_SPLIT__1__PROP_SPLIT__1__PROP_SPLIT__2__PROP_SPLIT__true__PROP_SPLIT__2__PROP_SPLIT__true__PROP_SPLIT__10__PROP_SPLIT__true__ROLL_SPLIT__Imbalanced__DIE_SPLIT__fib__DIE_SPLIT__1__FACE_SPLIT__1__FACE_SPLIT__2__FACE_SPLIT__3__FACE_SPLIT__5__FACE_SPLIT__8__ROLL_SPLIT__4__PROP_SPLIT__11__PROP_SPLIT__1__PROP_SPLIT__1__PROP_SPLIT__2__PROP_SPLIT__true__PROP_SPLIT__2__PROP_SPLIT__true__PROP_SPLIT__10__PROP_SPLIT__true" // Version 1.7.8
        )

        const val testSimplePropertiesString_1_7_7 = "Aggregate__ROLL_SAVE_STRING_SPLITTER__Fiala Strike__ROLL_CATEGORY_SAVE_STRING_SPLITTER__Attack__ROLL_SAVE_STRING_SPLITTER__Simple__DIE_SAVE_STRING_SPLITTER__d20__DIE_SAVE_STRING_SPLITTER__20__ROLL_SAVE_STRING_SPLITTER__1__PROPERTIES_SAVE_STRING_SPLITTER__0__PROPERTIES_SAVE_STRING_SPLITTER__0__PROPERTIES_SAVE_STRING_SPLITTER__0__PROPERTIES_SAVE_STRING_SPLITTER__0__PROPERTIES_SAVE_STRING_SPLITTER__false__PROPERTIES_SAVE_STRING_SPLITTER__0__PROPERTIES_SAVE_STRING_SPLITTER__false__PROPERTIES_SAVE_STRING_SPLITTER__0__PROPERTIES_SAVE_STRING_SPLITTER__false" // Version 1.7.7
        const val testComplicatedPropString_1_7_7 = "Aggregate__ROLL_SAVE_STRING_SPLITTER__Fiala Strike__ROLL_CATEGORY_SAVE_STRING_SPLITTER__Attack__ROLL_SAVE_STRING_SPLITTER__Simple__DIE_SAVE_STRING_SPLITTER__d20__DIE_SAVE_STRING_SPLITTER__20__ROLL_SAVE_STRING_SPLITTER__6__PROPERTIES_SAVE_STRING_SPLITTER__11__PROPERTIES_SAVE_STRING_SPLITTER__1__PROPERTIES_SAVE_STRING_SPLITTER__1__PROPERTIES_SAVE_STRING_SPLITTER__2__PROPERTIES_SAVE_STRING_SPLITTER__true__PROPERTIES_SAVE_STRING_SPLITTER__2__PROPERTIES_SAVE_STRING_SPLITTER__true__PROPERTIES_SAVE_STRING_SPLITTER__10__PROPERTIES_SAVE_STRING_SPLITTER__true" // Version 1.7.7
        const val testSimplePropertiesString_1_7_8 = "Aggregate__ROLL_SPLIT__Fiala Strike__CATEGORY_SPLIT__Attack__ROLL_SPLIT__Simple__DIE_SPLIT__d20__DIE_SPLIT__20__ROLL_SPLIT__1__PROP_SPLIT__0__PROP_SPLIT__0__PROP_SPLIT__0__PROP_SPLIT__0__PROP_SPLIT__0__PROP_SPLIT__0__PROP_SPLIT__false__PROP_SPLIT__0__PROP_SPLIT__false__PROP_SPLIT__0__PROP_SPLIT__false" // Version 1.7.8
        const val testComplicatedPropString_1_7_8 = "Aggregate__ROLL_SPLIT__Fiala Strike__CATEGORY_SPLIT__Attack__ROLL_SPLIT__Simple__DIE_SPLIT__d20__DIE_SPLIT__20__ROLL_SPLIT__6__PROP_SPLIT__11__PROP_SPLIT__1__PROP_SPLIT__1__PROP_SPLIT__2__PROP_SPLIT__1__PROP_SPLIT__1__PROP_SPLIT__true__PROP_SPLIT__2__PROP_SPLIT__true__PROP_SPLIT__10__PROP_SPLIT__true" // Version 1.7.8
        val fullRollProperties_1_7_7_expected = RollProperties(6,11, rollAdvantageValue, 1, 2, 0, 0, true, 2, true, 10, true)
        val fullRollProperties = RollProperties(6,11, rollAdvantageValue, 1, 2, 1, 1, true, 2, true, 10, true)
    }
}

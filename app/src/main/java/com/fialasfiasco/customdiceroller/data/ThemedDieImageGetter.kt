package com.fialasfiasco.customdiceroller.data

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import com.fialasfiasco.customdiceroller.R

const val DIE_UNKNOWN = -1
const val DIE_FATE = 0
const val DIE_2 = 2
const val DIE_3 = 3
const val DIE_4 = 4
const val DIE_6 = 6
const val DIE_8 = 8
const val DIE_10 = 10
const val DIE_12 = 12
const val DIE_20 = 20
const val DIE_100 = 100

fun getThemeFromString(context: Context, themeString: String) : Int{
    return when (themeString) {
        context.getString(R.string.white_theme) -> R.style.DefaultColor
        context.getString(R.string.fire_theme) -> R.style.FireGradientColor
        context.getString(R.string.forest_theme) -> R.style.ForestGradientColor
        context.getString(R.string.beach_theme) -> R.style.BeachGradientColor
        context.getString(R.string.rgb_theme) -> R.style.RGBGradientColor

        context.getString(R.string.gold_theme) -> R.style.GoldGradientColor
        context.getString(R.string.steel_theme) -> R.style.SteelGradientColor

        context.getString(R.string.creamsicle_theme) -> R.style.CreamsicleGradientColor
        context.getString(R.string.mint_chocolate_theme) -> R.style.MintChocolateGradientColor
        context.getString(R.string.rainbow_sherbert_theme) -> R.style.RainbowSherbertGradientColor
        context.getString(R.string.superman_theme) -> R.style.SupermanGradientColor
        else -> R.style.DefaultColor
    }
}

class ThemedDieImageGetter(private val context: Context, private val pageViewModel: PageViewModel) {

    fun getDieDrawable(imageID: Int) : Drawable {
        val dieDrawableID = when(pageViewModel.getTheme()) {
            R.style.FireGradientColor -> {
                when (imageID) {
                    DIE_UNKNOWN -> R.drawable.unknown_die_fire
                    DIE_FATE -> R.drawable.fate_fire
                    DIE_2 -> R.drawable.d2_fire
                    DIE_3 -> R.drawable.d6_up_3_fire
                    DIE_4 -> R.drawable.d4_fire
                    DIE_6 -> R.drawable.d6_up_6_fire
                    DIE_8 -> R.drawable.d8_fire
                    DIE_10 -> R.drawable.d10_fire
                    DIE_12 -> R.drawable.d12_fire
                    DIE_20 -> R.drawable.d20_fire
                    DIE_100 -> R.drawable.d100_fire
                    else -> R.drawable.unknown_die_fire
                }
            }
            R.style.ForestGradientColor -> {
                when (imageID) {
                    DIE_UNKNOWN -> R.drawable.unknown_die_forest
                    DIE_FATE -> R.drawable.fate_forest
                    DIE_2 -> R.drawable.d2_forest
                    DIE_3 -> R.drawable.d6_up_3_forest
                    DIE_4 -> R.drawable.d4_forest
                    DIE_6 -> R.drawable.d6_up_6_forest
                    DIE_8 -> R.drawable.d8_forest
                    DIE_10 -> R.drawable.d10_forest
                    DIE_12 -> R.drawable.d12_forest
                    DIE_20 -> R.drawable.d20_forest
                    DIE_100 -> R.drawable.d100_forest
                    else -> R.drawable.unknown_die_forest
                }
            }
            R.style.BeachGradientColor -> {
                when (imageID) {
                    DIE_UNKNOWN -> R.drawable.unknown_die_beach
                    DIE_FATE -> R.drawable.fate_beach
                    DIE_2 -> R.drawable.d2_beach
                    DIE_3 -> R.drawable.d6_up_3_beach
                    DIE_4 -> R.drawable.d4_beach
                    DIE_6 -> R.drawable.d6_up_6_beach
                    DIE_8 -> R.drawable.d8_beach
                    DIE_10 -> R.drawable.d10_beach
                    DIE_12 -> R.drawable.d12_beach
                    DIE_20 -> R.drawable.d20_beach
                    DIE_100 -> R.drawable.d100_beach
                    else -> R.drawable.unknown_die_beach
                }
            }
            R.style.RGBGradientColor -> {
                when (imageID) {
                    DIE_UNKNOWN -> R.drawable.unknown_die_rgb
                    DIE_FATE -> R.drawable.fate_rgb
                    DIE_2 -> R.drawable.d2_rgb
                    DIE_3 -> R.drawable.d6_up_3_rgb
                    DIE_4 -> R.drawable.d4_rgb
                    DIE_6 -> R.drawable.d6_up_6_rgb
                    DIE_8 -> R.drawable.d8_rgb
                    DIE_10 -> R.drawable.d10_rgb
                    DIE_12 -> R.drawable.d12_rgb
                    DIE_20 -> R.drawable.d20_rgb
                    DIE_100 -> R.drawable.d100_rgb
                    else -> R.drawable.unknown_die_rgb
                }
            }
            R.style.GoldGradientColor -> {
                when (imageID) {
                    DIE_UNKNOWN -> R.drawable.unknown_die_gold
                    DIE_FATE -> R.drawable.fate_gold
                    DIE_2 -> R.drawable.d2_gold
                    DIE_3 -> R.drawable.d6_up_3_gold
                    DIE_4 -> R.drawable.d4_gold
                    DIE_6 -> R.drawable.d6_up_6_gold
                    DIE_8 -> R.drawable.d8_gold
                    DIE_10 -> R.drawable.d10_gold
                    DIE_12 -> R.drawable.d12_gold
                    DIE_20 -> R.drawable.d20_gold
                    DIE_100 -> R.drawable.d100_gold
                    else -> R.drawable.unknown_die_gold
                }
            }
            R.style.SteelGradientColor -> {
                when (imageID) {
                    DIE_UNKNOWN -> R.drawable.unknown_die_steel
                    DIE_FATE -> R.drawable.fate_steel
                    DIE_2 -> R.drawable.d2_steel
                    DIE_3 -> R.drawable.d6_up_3_steel
                    DIE_4 -> R.drawable.d4_steel
                    DIE_6 -> R.drawable.d6_up_6_steel
                    DIE_8 -> R.drawable.d8_steel
                    DIE_10 -> R.drawable.d10_steel
                    DIE_12 -> R.drawable.d12_steel
                    DIE_20 -> R.drawable.d20_steel
                    DIE_100 -> R.drawable.d100_steel
                    else -> R.drawable.unknown_die_steel
                }
            }
            R.style.CreamsicleGradientColor -> {
                when (imageID) {
                    DIE_UNKNOWN -> R.drawable.unknown_die_creamsicle
                    DIE_FATE -> R.drawable.fate_creamsicle
                    DIE_2 -> R.drawable.d2_creamsicle
                    DIE_3 -> R.drawable.d6_up_3_creamsicle
                    DIE_4 -> R.drawable.d4_creamsicle
                    DIE_6 -> R.drawable.d6_up_6_creamsicle
                    DIE_8 -> R.drawable.d8_creamsicle
                    DIE_10 -> R.drawable.d10_creamsicle
                    DIE_12 -> R.drawable.d12_creamsicle
                    DIE_20 -> R.drawable.d20_creamsicle
                    DIE_100 -> R.drawable.d100_creamsicle
                    else -> R.drawable.unknown_die_creamsicle
                }
            }
            R.style.MintChocolateGradientColor -> {
                when (imageID) {
                    DIE_UNKNOWN -> R.drawable.unknown_die_mint_chocolate
                    DIE_FATE -> R.drawable.fate_mint_chocolate
                    DIE_2 -> R.drawable.d2_mint_chocolate
                    DIE_3 -> R.drawable.d6_up_3_mint_chocolate
                    DIE_4 -> R.drawable.d4_mint_chocolate
                    DIE_6 -> R.drawable.d6_up_6_mint_chocolate
                    DIE_8 -> R.drawable.d8_mint_chocolate
                    DIE_10 -> R.drawable.d10_mint_chocolate
                    DIE_12 -> R.drawable.d12_mint_chocolate
                    DIE_20 -> R.drawable.d20_mint_chocolate
                    DIE_100 -> R.drawable.d100_mint_chocolate
                    else -> R.drawable.unknown_die_mint_chocolate
                }
            }
            R.style.RainbowSherbertGradientColor -> {
                when (imageID) {
                    DIE_UNKNOWN -> R.drawable.unknown_die_sherbert
                    DIE_FATE -> R.drawable.fate_sherbert
                    DIE_2 -> R.drawable.d2_sherbert
                    DIE_3 -> R.drawable.d6_up_3_sherbert
                    DIE_4 -> R.drawable.d4_sherbert
                    DIE_6 -> R.drawable.d6_up_6_sherbert
                    DIE_8 -> R.drawable.d8_sherbert
                    DIE_10 -> R.drawable.d10_sherbert
                    DIE_12 -> R.drawable.d12_sherbert
                    DIE_20 -> R.drawable.d20_sherbert
                    DIE_100 -> R.drawable.d100_sherbert
                    else -> R.drawable.unknown_die_sherbert
                }
            }
            R.style.SupermanGradientColor -> {
                when (imageID) {
                    DIE_UNKNOWN -> R.drawable.unknown_die_superman
                    DIE_FATE -> R.drawable.fate_superman
                    DIE_2 -> R.drawable.d2_superman
                    DIE_3 -> R.drawable.d6_up_3_superman
                    DIE_4 -> R.drawable.d4_superman
                    DIE_6 -> R.drawable.d6_up_6_superman
                    DIE_8 -> R.drawable.d8_superman
                    DIE_10 -> R.drawable.d10_superman
                    DIE_12 -> R.drawable.d12_superman
                    DIE_20 -> R.drawable.d20_superman
                    DIE_100 -> R.drawable.d100_superman
                    else -> R.drawable.unknown_die_superman
                }
            }
            // The else & default case are the same.
            else -> {
                when (imageID) {
                    DIE_UNKNOWN -> R.drawable.unknown_die
                    DIE_FATE -> R.drawable.fate
                    DIE_2 -> R.drawable.d2
                    DIE_3 -> R.drawable.d6_up_3
                    DIE_4 -> R.drawable.d4
                    DIE_6 -> R.drawable.d6_up_6
                    DIE_8 -> R.drawable.d8
                    DIE_10 -> R.drawable.d10
                    DIE_12 -> R.drawable.d12
                    DIE_20 -> R.drawable.d20
                    DIE_100 -> R.drawable.d100
                    else -> R.drawable.unknown_die
                }
            }
        }

        return context.getDrawable(dieDrawableID)!!
    }
}
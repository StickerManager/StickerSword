/*
 * Sticker Sword -Inject stickers on app for Sticker Manager.
 * Copyright (C) 2024 FoskyM<i@fosky.top>
 * https://github.com/StickerManager/StickerSword
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * This file is created by FoskyM on 2024/09/25.
 */
@file:Suppress("SameParameterValue")

package net.fosky.sticker.sticker_sword.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat
import net.fosky.sticker.sticker_sword.utils.factory.dp
import net.fosky.sticker.sticker_sword.utils.factory.isSystemInDarkMode
import top.defaults.drawabletoolbox.DrawableBuilder

class MaterialSwitch(context: Context, attrs: AttributeSet?) : SwitchCompat(context, attrs) {

    private fun trackColors(selected: Int, pressed: Int, normal: Int): ColorStateList {
        val colors = intArrayOf(selected, pressed, normal)
        val states = arrayOfNulls<IntArray>(3)
        states[0] = intArrayOf(android.R.attr.state_checked)
        states[1] = intArrayOf(android.R.attr.state_pressed)
        states[2] = intArrayOf()
        return ColorStateList(states, colors)
    }

    private val thumbColor get() = if (context.isSystemInDarkMode) 0xFF7C7C7C else 0xFFCCCCCC

    init {
        trackDrawable = DrawableBuilder()
            .rectangle()
            .rounded()
            .solidColor(0xFF656565.toInt())
            .height(20.dp(context))
            .cornerRadius(15.dp(context))
            .build()
        thumbDrawable = DrawableBuilder()
            .rectangle()
            .rounded()
            .solidColor(Color.WHITE)
            .size(20.dp(context), 20.dp(context))
            .cornerRadius(20.dp(context))
            .strokeWidth(8.dp(context))
            .strokeColor(Color.TRANSPARENT)
            .build()
        trackTintList = trackColors(
            0xFF656565.toInt(),
            thumbColor.toInt(),
            thumbColor.toInt()
        )
        isSingleLine = true
        ellipsize = TextUtils.TruncateAt.END
    }
}
/*
 * Sticker Sword - Inject stickers on app for Sticker Manager.
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
@file:Suppress("unused")

package net.fosky.sticker.sticker_sword.utils.factory

import android.content.Context
import android.content.res.Configuration

/**
 * System dark mode is enabled or not
 *
 * 系统深色模式是否开启
 * @return [Boolean] Whether to enable / 是否开启
 */
val Context.isSystemInDarkMode get() = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

/**
 * System dark mode is disable or not
 *
 * 系统深色模式是否没开启
 * @return [Boolean] Whether to enable / 是否开启
 */
inline val Context.isNotSystemInDarkMode get() = isSystemInDarkMode.not()

/**
 * dp to pxInt
 *
 * dp 转换为 pxInt
 * @param context using instance / 使用的实例
 * @return [Int]
 */
fun Number.dp(context: Context) = dpFloat(context).toInt()

/**
 * dp to pxFloat
 *
 * dp 转换为 pxFloat
 * @param context using instance / 使用的实例
 * @return [Float]
 */
fun Number.dpFloat(context: Context) = toFloat() * context.resources.displayMetrics.density
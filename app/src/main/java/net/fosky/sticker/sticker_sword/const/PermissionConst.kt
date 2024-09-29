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
 * This file is created by FoskyM on 2024/09/29.
 */
package net.fosky.sticker.sticker_sword.const

import android.Manifest
import android.os.Build
import net.fosky.sticker.sticker_sword.BuildConfig

/**
 * Permission Constants
 */
object PermissionConst {
    val hookPermissions: MutableList<String> = ArrayList()

    init {
        if (Build.VERSION_CODES.R <= Build.VERSION.SDK_INT) {
            hookPermissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        }
    }
}
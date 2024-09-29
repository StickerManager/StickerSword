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
 *
 * Idea from https://github.com/tianma8023/XposedSmsCode/blob/master/app/src/main/java/com/tianma/xsmscode/xp/hook/permission/PermissionGranterHook.java
 */
@file:Suppress("ConstPropertyName")
package net.fosky.sticker.sticker_sword.hook.permission

import android.os.Build
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ApplicationInfoClass
import com.highcapable.yukihookapi.hook.type.android.PackageInfoClass
import de.robv.android.xposed.XposedHelpers.getObjectField
import net.fosky.sticker.sticker_sword.hook.entity.QQTIMHooker.hook


object PermissionGrantHooker : YukiBaseHooker()  {
    override fun onHook() {
        YLog.info("Hooking permission")

        // loadHooker(PermissionInsertHooker)

        val sdkInt = Build.VERSION.SDK_INT
        YLog.info("Hooking permission on Android API Version $sdkInt")
        if (sdkInt >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            loadHooker(PermissionManagerService34Hooker)
        } else if (sdkInt >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            loadHooker(PermissionManagerService33Hooker)
        } else if (sdkInt >= Build.VERSION_CODES.S) { // Android 12~12L
            loadHooker(PermissionManagerService31Hooker)
        } else if (sdkInt >= Build.VERSION_CODES.R) { // Android 11
            loadHooker(PermissionManagerService30Hooker)
        } else if (sdkInt >= Build.VERSION_CODES.P) { // Android 9.0~10
            loadHooker(PermissionManagerServiceHooker)
        } else { // Android 5.0 ~ 8.1
            loadHooker(PackageServiceHooker)
        }
    }
}
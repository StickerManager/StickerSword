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
 * Idea from https://blog.csdn.net/siyujiework/article/details/103202519
 * Have no effect but keep file for learn.
 */
@file:Suppress("ConstPropertyName")

package net.fosky.sticker.sticker_sword.hook.permission

import android.content.res.TypedArray
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import net.fosky.sticker.sticker_sword.const.PackageName
import net.fosky.sticker.sticker_sword.const.PermissionConst.hookPermissions
import net.fosky.sticker.sticker_sword.utils.factory.get
import net.fosky.sticker.sticker_sword.utils.factory.set


object PermissionInsertHooker : YukiBaseHooker() {
    override fun onHook() {
        YLog.info("Inserting permission")
        val PackageParserClass by lazyClassOrNull("android.content.pm.PackageParser")
        PackageParserClass?.method {
            name = "parseUsesPermission"
            paramCount = 3
        }?.hook()?.after {
            val pkg = args[0]
            val _packageName = pkg!!::class.java.method {
                name = "getPackageName"
            }.get(pkg).call()
            YLog.info("Hooking permission for ${_packageName}")
            val appNames = PackageName::class.java.declaredFields.mapNotNull {
                it.isAccessible = true
                it.get(null) as? String
            }
            if (_packageName in appNames) {
                for (permissionToGrant in hookPermissions) {
                    val requestedPermissions = pkg.get<Any>("requestedPermissions") as ArrayList<String>
                    if (-1 == requestedPermissions.indexOf(permissionToGrant)) {
                        requestedPermissions.add(permissionToGrant)
                    }
                    pkg.set("requestedPermissions", requestedPermissions)
                }
            }
        }
    }
}
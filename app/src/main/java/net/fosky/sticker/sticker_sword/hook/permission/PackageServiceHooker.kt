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
 * Idea from https://github.com/tianma8023/XposedSmsCode/blob/master/app/src/main/java/com/tianma/xsmscode/xp/hook/permission/PackageManagerServiceHook.java
 * Without test
 */
@file:Suppress("ConstPropertyName")
package net.fosky.sticker.sticker_sword.hook.permission

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import net.fosky.sticker.sticker_sword.const.PackageName
import net.fosky.sticker.sticker_sword.const.PermissionConst.hookPermissions
import net.fosky.sticker.sticker_sword.utils.factory.get


object PackageServiceHooker : YukiBaseHooker()  {
    const val CLASS_PACKAGE_MANAGER_SERVICE: String = "com.android.server.pm.PackageManagerService"

    override fun onHook() {
        val pmsClass by lazyClassOrNull(CLASS_PACKAGE_MANAGER_SERVICE)
        // Android 5.0 +
        YLog.debug("Hooking grantPermissionsLPw() for Android 21+")

        pmsClass?.method {
            name = "grantPermissionsLPw"
            paramCount = 3
        }?.hook()?.after {
            val pkg = args[0]
            val _packageName = pkg.get<Any>("packageName") as String
            val appNames = PackageName::class.java.declaredFields.mapNotNull {
                it.isAccessible = true
                it.get(null) as? String
            }
            if (_packageName in appNames) {
                YLog.debug("PackageName: ${_packageName}")
                val extras = pkg.get<Any>("mExtras")
                val permissionsState = extras!!::class.java.method {
                    name = "getPermissionsState"
                }.get(extras).call()
                val requestedPermissions = pkg.get<Any>("requestedPermissions") as List<String>
                val settings = instance.get<Any>("mSettings")
                val permissions = settings.get<Any>("mPermissions")

                for (permissionToGrant in hookPermissions) {
                    if (!requestedPermissions.contains(permissionToGrant)) {
                        val granted = permissionsState!!::class.java.method {
                            name = "hasInstallPermission"
                            paramCount = 1
                        }.get(permissionsState).call(permissionToGrant) as Boolean

                        if (!granted) {
                            // com.android.server.pm.BasePermission bpToGrant
                            val bpToGrant = permissions!!::class.java.method {
                                name = "call"
                                paramCount = 1
                            }.get(permissions).call(permissionToGrant)

                            val result = permissionsState!!::class.java.method {
                                name = "grantInstallPermission"
                                paramCount = 1
                            }.get(permissionsState).call(bpToGrant) as Int

                            YLog.debug("Add $bpToGrant; result = $result")
                        } else {
                            YLog.debug("Already have $permissionToGrant permission")
                        }
                    }
                }
            }
        }
    }
}
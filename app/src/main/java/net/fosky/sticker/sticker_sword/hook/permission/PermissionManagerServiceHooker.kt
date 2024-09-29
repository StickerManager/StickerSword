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
 * Idea from https://github.com/tianma8023/XposedSmsCode/blob/master/app/src/main/java/com/tianma/xsmscode/xp/hook/permission/PermissionManagerServiceHook.java
 */
@file:Suppress("ConstPropertyName")

package net.fosky.sticker.sticker_sword.hook.permission

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import net.fosky.sticker.sticker_sword.const.PackageName
import net.fosky.sticker.sticker_sword.const.PermissionConst.hookPermissions
import java.lang.reflect.Method


object PermissionManagerServiceHooker : YukiBaseHooker() {
    // for Android 28+
    const val CLASS_PERMISSION_MANAGER_SERVICE: String =
        "com.android.server.pm.permission.PermissionManagerService"
    const val CLASS_PERMISSION_CALLBACK: String =
        "com.android.server.pm.permission.PermissionManagerInternal.PermissionCallback"
    const val CLASS_PACKAGE_PARSER_PACKAGE: String = "android.content.pm.PackageParser.Package"

    // for MIUI 10 Android Q
    const val CLASS_PERMISSION_CALLBACK_Q: String =
        "com.android.server.pm.permission.PermissionManagerServiceInternal.PermissionCallback"

    private fun afterGrantPermissionsSinceP(param: MethodHookParam) {

        // com.android.server.pm.parsing.pkg.AndroidPackage 对象
        val pkg = param.args[0]


        // final String _packageName = (String) XposedHelpers.getObjectField(pkg, "packageName");
        val _packageName = XposedHelpers.callMethod(pkg, "getPackageName") as String

        val appNames = PackageName::class.java.declaredFields.mapNotNull {
            it.isAccessible = true
            it.get(null) as? String
        }
        if (_packageName in appNames) {
            YLog.debug("PackageName: ${_packageName}")

            // PackageParser$Package.mExtras 实际上是 com.android.server.pm.PackageSetting mExtras 对象
            val extras = XposedHelpers.getObjectField(pkg, "mExtras")

            // com.android.server.pm.permission.PermissionsState 对象
            val permissionsState = XposedHelpers.callMethod(extras, "getPermissionsState")


            // Manifest.xml 中声明的permission列表
            val requestedPermissions =
                XposedHelpers.getObjectField(pkg, "requestedPermissions") as List<String>


            // com.android.server.pm.permission.PermissionSettings mSettings 对象
            val settings = XposedHelpers.getObjectField(param.thisObject, "mSettings")

            // ArrayMap<String, com.android.server.pm.permission.BasePermission> mPermissions 对象
            val permissions = XposedHelpers.getObjectField(settings, "mPermissions")

            val permissionsToGrant: List<String> = hookPermissions
            for (permissionToGrant in permissionsToGrant) {
                if (!requestedPermissions.contains(permissionToGrant)) {
                    val granted = XposedHelpers.callMethod(
                        permissionsState, "hasInstallPermission", permissionToGrant
                    ) as Boolean
                    // grant permissions
                    if (!granted) {
                        // com.android.server.pm.permission.BasePermission bpToGrant
                        val bpToGrant =
                            XposedHelpers.callMethod(permissions, "get", permissionToGrant)
                        val result = XposedHelpers.callMethod(
                            permissionsState,
                            "grantInstallPermission",
                            bpToGrant
                        ) as Int
                        YLog.debug("Add $bpToGrant; result = $result")
                    } else {
                        YLog.debug("Already have $permissionToGrant permission")
                    }
                    // revoke permissions
                    // if (!granted) {
                    //     XLog.d("Don't have " + permissionToGrant + " permission");
                    // } else {
                    //     XLog.d("Already have " + permissionToGrant + " permission");
                    //     // com.android.server.pm.permission.BasePermission bpToGrant
                    //     final Object bpToGrant = XposedHelpers.callMethod(permissions, "get", permissionToGrant);
                    //     int result = (int) XposedHelpers.callMethod(permissionsState, "revokeInstallPermission", bpToGrant);
                    //     XLog.d("Remove permission " + bpToGrant + "; result = " + result);
                    // }
                }
            }

        }
    }

    private fun findTargetMethod(mClassLoader: ClassLoader): Method? {
        val pmsClass = XposedHelpers.findClass(CLASS_PERMISSION_MANAGER_SERVICE, mClassLoader)
        val packageClass = XposedHelpers.findClass(CLASS_PACKAGE_PARSER_PACKAGE, mClassLoader)
        var callbackClass = XposedHelpers.findClassIfExists(CLASS_PERMISSION_CALLBACK, mClassLoader)
        if (callbackClass == null) {
            // Android Q PermissionCallback 不一样
            callbackClass = XposedHelpers.findClassIfExists(CLASS_PERMISSION_CALLBACK_Q, mClassLoader)
        }

        var method = XposedHelpers.findMethodExactIfExists(
            pmsClass, "grantPermissions",  /* PackageParser.Package pkg   */
            packageClass,  /* boolean replace             */
            Boolean::class.javaPrimitiveType,  /* String packageOfInterest    */
            String::class.java,  /* PermissionCallback callback */
            callbackClass
        )

        if (method == null) { // method grantPermissions() not found
            // Android Q
            method = XposedHelpers.findMethodExactIfExists(
                pmsClass, "restorePermissionState",  /* PackageParser.Package pkg   */
                packageClass,  /* boolean replace             */
                Boolean::class.javaPrimitiveType,  /* String packageOfInterest    */
                String::class.java,  /* PermissionCallback callback */
                callbackClass
            )
            if (method == null) { // method restorePermissionState() not found
                val _methods = XposedHelpers.findMethodsByExactParameters(
                    pmsClass, Void.TYPE,  /* PackageParser.Package pkg   */
                    packageClass,  /* boolean replace             */
                    Boolean::class.javaPrimitiveType,  /* String packageOfInterest    */
                    String::class.java,  /* PermissionCallback callback */
                    callbackClass
                )
                if (_methods != null && _methods.size > 0) {
                    method = _methods[0]
                }
            }
        }
        return method
    }

    override fun onHook() {
        YLog.info("Hooking permission in PermissionManagerService34Hooker")

        val method = appClassLoader?.let { findTargetMethod(it) }

        XposedBridge.hookMethod(method, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                afterGrantPermissionsSinceP(param)
            }
        })
    }
}
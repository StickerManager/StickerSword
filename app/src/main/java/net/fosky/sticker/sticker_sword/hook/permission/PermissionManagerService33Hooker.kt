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
 * This file is created by FoskyM on 2024/09/29.
 * From https://github.com/tianma8023/XposedSmsCode/blob/master/app/src/main/java/com/tianma/xsmscode/xp/hook/permission/PermissionManagerServiceHook33.java
 */
@file:Suppress("ConstPropertyName")

package net.fosky.sticker.sticker_sword.hook.permission

import android.os.UserHandle
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import net.fosky.sticker.sticker_sword.const.PackageName
import net.fosky.sticker.sticker_sword.const.PermissionConst.hookPermissions
import net.fosky.sticker.sticker_sword.utils.factory.get
import java.lang.reflect.Method


object PermissionManagerService33Hooker : YukiBaseHooker() {
    const val CLASS_PERMISSION_MANAGER_SERVICE: String =
        "com.android.server.pm.permission.PermissionManagerServiceImpl"
    const val CLASS_ANDROID_PACKAGE: String = "com.android.server.pm.parsing.pkg.AndroidPackage"
    const val CLASS_PERMISSION_CALLBACK: String =
        "$CLASS_PERMISSION_MANAGER_SERVICE.PermissionCallback"

    private fun afterRestorePermissionStateSinceAndroid13(param: MethodHookParam) {
        // com.android.server.pm.parsing.pkg.AndroidPackage 对象
        val pkg = param.args[0]

        val _packageName = XposedHelpers.callMethod(pkg, "getPackageName") as String

        val appNames = PackageName::class.java.declaredFields.mapNotNull {
            it.isAccessible = true
            it.get(null) as? String
        }
        if (_packageName in appNames) {
            YLog.debug("PackageName: ${_packageName}")

            // PermissionManagerServiceImpl 对象
            val pmsImpl = param.thisObject

            // UserHandle.USER_ALL
            val filterUserId = param.args[4] as Int
            val USER_ALL = XposedHelpers.getStaticIntField(UserHandle::class.java, "USER_ALL")
            val userIds = if (filterUserId == USER_ALL
            ) XposedHelpers.callMethod(pmsImpl, "getAllUserIds") as IntArray
            else intArrayOf(filterUserId)

            val permissionsToGrant: List<String> = hookPermissions

            if (userIds != null) {
                // PackageManagerInternal 对象 mPackageManagerInt
                val mPackageManagerInt =
                    XposedHelpers.getObjectField(pmsImpl, "mPackageManagerInt")

                // PackageStateInternal 对象 ps
                // final PackageStateInternal ps = mPackageManagerInt.getPackageStateInternal(pkg.getPackageName());
                val ps = XposedHelpers.callMethod(
                    mPackageManagerInt,
                    "getPackageStateInternal",
                    _packageName
                )

                // Manifest.xml 中声明的permission列表
                // List<String> requestPermissions = pkg.getRequestedPermissions();
                val requestedPermissions =
                    XposedHelpers.callMethod(pkg, "getRequestedPermissions") as List<String>

                // com.android.server.pm.permission.DevicePermissionState 对象
                val mState = XposedHelpers.getObjectField(pmsImpl, "mState")

                // com.android.server.pm.permission.PermissionRegistry 对象
                val mRegistry = XposedHelpers.getObjectField(pmsImpl, "mRegistry")

                for (userId in userIds) {
                    // com.android.server.pm.permission.UserPermissionState 对象
                    val userState =
                        XposedHelpers.callMethod(mState, "getOrCreateUserState", userId)
                    val appId = XposedHelpers.callMethod(ps, "getAppId") as Int
                    //  com.android.server.pm.permission.UidPermissionState 对象
                    val uidState =
                        XposedHelpers.callMethod(userState, "getOrCreateUidState", appId)

                    for (permissionToGrant in permissionsToGrant) {
                        if (!requestedPermissions.contains(permissionToGrant)) {
                            val granted = XposedHelpers.callMethod(
                                uidState,
                                "isPermissionGranted",
                                permissionToGrant
                            ) as Boolean
                            if (!granted) {
                                // permission not grant before
                                // final Permission bp = mRegistry.getPermission(permName);
                                val bpToGrant = XposedHelpers.callMethod(
                                    mRegistry,
                                    "getPermission",
                                    permissionToGrant
                                )
                                // uidState.grantPermission(bp)
                                val result = XposedHelpers.callMethod(
                                    uidState,
                                    "grantPermission",
                                    bpToGrant
                                ) as Boolean
                                YLog.debug("Add $permissionToGrant; result = $result")
                            } else {
                                // permission has been granted already
                                YLog.debug("Already have $permissionToGrant permission")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun findTargetMethod(mClassLoader: ClassLoader): Method? {
        val pmsClass = XposedHelpers.findClass(CLASS_PERMISSION_MANAGER_SERVICE, mClassLoader)
        val androidPackageClass = XposedHelpers.findClass(CLASS_ANDROID_PACKAGE, mClassLoader)
        val callbackClass = XposedHelpers.findClassIfExists(CLASS_PERMISSION_CALLBACK, mClassLoader)

        // 精确匹配
        var method = XposedHelpers.findMethodExactIfExists(
            pmsClass, "restorePermissionState",  /* AndroidPackage pkg          */
            androidPackageClass,  /* boolean replace             */
            Boolean::class.javaPrimitiveType,  /* String packageOfInterest    */
            String::class.java,  /* PermissionCallback callback */
            callbackClass,  /* int filterUserId            */
            Int::class.javaPrimitiveType
        )

        if (method == null) { // method restorePermissionState() not found
            // 参数类型精确匹配
            val _methods = XposedHelpers.findMethodsByExactParameters(
                pmsClass, Void.TYPE,  /* AndroidPackage pkg          */
                androidPackageClass,  /* boolean replace             */
                Boolean::class.javaPrimitiveType,  /* String packageOfInterest    */
                String::class.java,  /* PermissionCallback callback */
                callbackClass,  /* int filterUserId            */
                Int::class.javaPrimitiveType
            )
            if (_methods != null && _methods.size > 0) {
                method = _methods[0]
            }
        }
        return method
    }


    override fun onHook() {
        YLog.info("Hooking permission in PermissionManagerService34Hooker")

        val method = appClassLoader?.let { findTargetMethod(it) }

        XposedBridge.hookMethod(method, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                afterRestorePermissionStateSinceAndroid13(param)
            }
        })
    }
}
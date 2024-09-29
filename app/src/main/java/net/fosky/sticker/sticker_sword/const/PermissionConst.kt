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
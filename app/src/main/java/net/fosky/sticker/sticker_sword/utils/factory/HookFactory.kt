package net.fosky.sticker.sticker_sword.utils.factory

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.*
import net.fosky.sticker.sticker_sword.const.PackageName
import net.fosky.sticker.sticker_sword.hook.permission.PackageServiceHooker.hook


fun Any?.set(key: String, value: Any) {
    val instance = this ?: return
    instance::class.java.field {
        name = key
        superClass()
    }.get(instance).set(value)
}

fun <T> Any?.get(key: String): Any? {
    val instance = this ?: return null
    return instance::class.java.field {
        name = key
        superClass()
    }.get(instance).any()
}

fun Any.compatToActivity() =
    if (this is Activity) this else current().method { name = "getActivity"; superClass() }
        .invoke()

/**
 * Grant Files Access Permission
 * 测试发现经过 PermissionGrantHooker 处理后，无需手动授权
 * 系统：HyperOS 1.0.22.0.UMLCNXM (Android 14) [Redmi K60U]
 * 所以不大确定其它情况是否需要这个方法，以防万一先保留
 */
fun hookGrantFilesAccessPermission(activityClass: Class<*>, packageName: String) {
    val method = activityClass.method {
        name = "onCreate"
        paramCount = 1
    }
    method.hook().after {
        if (Build.VERSION_CODES.R <= Build.VERSION.SDK_INT) {
            if (!Environment.isExternalStorageManager()) {
                val activity = instance.compatToActivity()
                val context = activity?.applicationContext
                val permission = Manifest.permission.MANAGE_EXTERNAL_STORAGE
                if (activity?.let {
                        ContextCompat.checkSelfPermission(
                            it,
                            permission
                        )
                    } != PackageManager.PERMISSION_GRANTED) {
                    if (activity != null) {
                        val permissions = arrayOf(permission)
                        ActivityCompat.requestPermissions(activity, permissions, 100)
                        (activity.startActivityForResult(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                            data = Uri.parse("package:$packageName")
                        }, 2339))
                    };
                }
            }
        }
    }
}


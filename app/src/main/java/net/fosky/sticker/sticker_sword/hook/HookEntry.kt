package net.fosky.sticker.sticker_sword.hook

import android.app.Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.applyModuleTheme
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import net.fosky.sticker.sticker_sword.R
import net.fosky.sticker.sticker_sword.const.PackageName
import net.fosky.sticker.sticker_sword.hook.entity.*

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        debugLog {
            tag = "StickerSword"
            isEnable = true
        }

    }

    override fun onHook() = encase {
        loadApp(PackageName.QQ, PackageName.TIM) { loadHooker(QQTIMHooker) }
    }
}
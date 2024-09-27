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
 * This file is created by FoskyM on 2024/09/25.
 */
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
        loadApp(PackageName.WECHAT) { loadHooker(WechatHooker) }
    }
}
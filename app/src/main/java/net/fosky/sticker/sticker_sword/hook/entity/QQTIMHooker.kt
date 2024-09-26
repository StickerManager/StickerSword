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
@file:Suppress("ConstPropertyName")

package net.fosky.sticker.sticker_sword.hook.entity

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ServiceCompat
import androidx.fragment.app.Fragment
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.allConstructors
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.registerModuleAppActivities
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.BuildClass
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.android.MessageClass
import com.highcapable.yukihookapi.hook.type.java.AnyArrayClass
import com.highcapable.yukihookapi.hook.type.java.AnyClass
import com.highcapable.yukihookapi.hook.type.java.BooleanClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.highcapable.yukihookapi.hook.type.java.IntClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.StringType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import java.util.concurrent.Executors
import net.fosky.sticker.sticker_sword.const.PackageName
import net.fosky.sticker.sticker_sword.hook.entity.QQTIMHooker.FavoriteEmoticonInfo
import net.fosky.sticker.sticker_sword.hook.entity.QQTIMHooker.hook
import net.fosky.sticker.sticker_sword.hook.entity.QQTIMHooker.lazyClassOrNull
import net.fosky.sticker.sticker_sword.utils.factory.StickerFactory

abstract class ExtraEmoticon {
    //    abstract fun emoticonId(): String
//    abstract fun emoticonName(): String
    abstract fun QQEmoticonObject(): Any?
}

abstract class ExtraEmoticonPanel {
    abstract fun emoticons(): List<ExtraEmoticon>
    abstract fun emoticonPanelIconURL(): String?
    abstract fun uniqueId(): String
}

abstract class ExtraEmoticonProvider {
    abstract fun extraEmoticonList(): List<ExtraEmoticonPanel>
    abstract fun uniqueId(): String
}

val executor = Executors.newFixedThreadPool(2)

class StickerManagerEmoticonProvider : ExtraEmoticonProvider() {
    class Panel(val id: String, val slug: String, val name: String) : ExtraEmoticonPanel() {
        val FavoriteEmoticonInfo by lazyClassOrNull("${PackageName.QQ}.emoticonview.FavoriteEmoticonInfo")
        private var emoticons: List<ExtraEmoticon> = listOf()
        private var iconPath: String? = null
        private fun updateEmoticons() {
            val stickers = StickerFactory.getStickersByCategorySlug(slug)
            val emoticons = mutableListOf<ExtraEmoticon>()

            for (sticker in stickers) {
                emoticons.add(object : ExtraEmoticon() {
                    val info = FavoriteEmoticonInfo?.buildOf()

                    init {
                        info.set("path", sticker.path)
                        info.set("remark", sticker.notes)
                        info.set("actionData", "${uniqueId()}:${sticker.path}")
                    }

                    override fun QQEmoticonObject(): Any? {
                        return info
                    }
                })
            }

            this.emoticons = emoticons
            if (iconPath == null && stickers.isNotEmpty()) {
                iconPath = stickers[0].path
            }
        }

        init {
            executor.execute {
                updateEmoticons()
            }
        }

        private var lastEmoticonUpdateTime = 0L
        override fun emoticons(): List<ExtraEmoticon> {
            if (System.currentTimeMillis() - lastEmoticonUpdateTime > 1000 * 5) {
                lastEmoticonUpdateTime = System.currentTimeMillis()
                executor.execute {
                    updateEmoticons()
                }
            }
            return emoticons
        }

        override fun emoticonPanelIconURL(): String? {
            return if (iconPath != null) "file://$iconPath" else null
        }

        override fun uniqueId(): String {
            return id
        }
    }

    private val panelsMap = mutableMapOf<String, Panel?>()

    override fun extraEmoticonList(): List<ExtraEmoticonPanel> {
        val categories = StickerFactory.getAllStickersByCategory()
        val panels = mutableListOf<ExtraEmoticonPanel>()
        for (category in categories) {
            if (!panelsMap.containsKey(category.slug)) {
                val panel = Panel(category.id, category.slug, category.name)
                panelsMap[category.slug] = panel
            }
            panels.add(panelsMap[category.slug]!!)
        }

        // restore last use sorting
//        val db = ConfigManager.getDumpTG_LastUseEmoticonPackStore()
//        panels.sortByDescending { db.getLongOrDefault(it.uniqueId(), 0) }
        return panels
    }

    override fun uniqueId(): String {
        return "StickerManagerEmoticonProvider"
    }
}


object QQTIMHooker : YukiBaseHooker() {
    val EmoticonPanelController by lazyClassOrNull("${PackageName.QQ}.emoticonview.EmoticonPanelController")
    val EmotionPanelViewPagerAdapter by lazyClassOrNull("${PackageName.QQ}.emoticonview.EmotionPanelViewPagerAdapter")
    val EmoticonTabAdapter by lazyClassOrNull("${PackageName.QQ}.emoticonview.EmoticonTabAdapter")
    val EmoticonPanelInfo by lazyClassOrNull("${PackageName.QQ}.emoticonview.EmotionPanelInfo")
    val EmoticonPackage by lazyClassOrNull("${PackageName.QQ}.data.EmoticonPackage")
    val FavoriteEmoticonInfo by lazyClassOrNull("${PackageName.QQ}.emoticonview.FavoriteEmoticonInfo")

    private val isQQ get() = packageName == PackageName.QQ
    private var hostVersionName = "<unknown>"

    private fun Any.compatToActivity() =
        if (this is Activity) this else current().method { name = "getActivity"; superClass() }
            .invoke()

    private fun hookEmoticon() {
        FavoriteEmoticonInfo?.method {
            name = "getDrawable"
        }?.hook()?.before {
            result = FavoriteEmoticonInfo?.method {
                name = "getZoomDrawable"
            }?.get()?.call(instance, args[0], args[1], 300, 300)
        }

        val providers: List<ExtraEmoticonProvider> = listOf(StickerManagerEmoticonProvider())

        data class StickerEpId(var providerId: String = "", var panelId: String)

        fun parseStickerEpId(epId: String): StickerEpId? {
            if (!epId.startsWith("ss:")) return null
            val data = epId.substring(3)
            val providerId = data.substring(0, data.indexOf(":"))
            val panelId = data.substring(data.indexOf(":") + 1)
            return StickerEpId(providerId, panelId)
        }

        var emoticonPanelViewAdapterInstance: Any? = null

        EmotionPanelViewPagerAdapter?.allConstructors { _, constructor ->
            constructor.hook().after {
                emoticonPanelViewAdapterInstance = instance
            }
        }

        EmotionPanelViewPagerAdapter?.method {
            name = "handleIPSite"
            paramCount = 3
        }?.hook()?.before {
            val pack = args[0]
            YLog.info("handleIPSite: $pack, hook checking")
            if (pack != null && parseStickerEpId(pack.get<String>("epId")!!.toString()) != null) {
                args[0] = null
            }
        }

        EmoticonTabAdapter?.method {
            name = "generateTabUrl"
            paramCount = 2
        }?.hook()?.before {
            val id = parseStickerEpId(args[0] as String)
            if (id != null) {
                val provider = providers.find { it.uniqueId() == id.providerId }
                if (provider != null) {
                    val panel = provider.extraEmoticonList().find { it.uniqueId() == id.panelId }
                    if (panel != null) {
                        val url = panel.emoticonPanelIconURL()
                        if (url != null)
                            result = java.net.URL(url)
                        else
                            result = null
                    }
                }
            }
        }

        var lastPanelDataSize = -1
        EmoticonPanelController?.method {
            name = "getPanelDataList"
            emptyParam()
        }?.hook()?.after {
            val stackTrace = Thread.currentThread().stackTrace
            if (stackTrace.any { it.className == "com.tencent.mobileqq.emoticonview.EmoticonReportDtHelper" && it.methodName == "addDTReport" }) {
                return@after
            }

            if (result == null) return@after
            val list = result as MutableList<Any>
            if (list.size == 1 && list[0].get<Int>("type") == 16 /* AIOEmoReply */) return@after
            val iterator = list.iterator()

            val typeWhiteList = mutableSetOf(
                4, // 收藏表情,
                6, // 商店表情
                7, // Emoji 表情,
                18, // 搜索表情,

//                12, // GIF
//                13, // 表情商城,
//                17, // 专属表情
//                19, // 超级表情
            )

//            if (!removeQQMisc) {
//                typeWhiteList += 13
//                typeWhiteList += 12
//                typeWhiteList += 17
//                typeWhiteList += 19
//            }

            val existingIds = mutableSetOf<String>()

            while (iterator.hasNext()) {
                val element = iterator.next()

                if (!typeWhiteList.contains(element.get<Int>("type")!!)) {
                    if (element.get<Int>("type")!! == 6) {
                        val id = element.get<Any>("emotionPkg")!!.get<Any>("epId") as String
                        // if (!id.startsWith("ss:") && removeQQEmoticons) iterator.remove()
                        existingIds.add(id)
                    } else {
                        iterator.remove()
                    }
                } else {
                    if (element.get<Int>("type")!! == 6)
                        existingIds.add(element.get<Any>("emotionPkg")!!.get<Any>("epId") as String)
                }
            }

            var i = 3
            // 添加自定义面板
            for (provider in providers) {
                for (panel in provider.extraEmoticonList()) {
                    i++
                    val epid = "ss:${provider.uniqueId()}:${panel.uniqueId()}"
                    if (existingIds.contains(epid)) continue
                    val pack = EmoticonPackage?.buildOf()

                    pack.set("epId", epid)
                    pack.set("name", "StickerManagerExtraSticker")
                    pack.set("type", 3, IntType)
                    pack.set("ipJumpUrl", "https://github.com/StickerManager/Release/")
                    pack.set("ipDetail", "SM")
                    pack.set("valid", true, BooleanType)
                    pack.set("status", 2, IntType)
                    pack.set("latestVersion", 1488377358, IntType)
                    pack.set("aio", true, BooleanType)

                    val info = EmoticonPanelInfo?.buildOf(6, 4, pack) {
                        paramCount = 3
                    }

                    list.add(info!!)
                }
            }

            if (lastPanelDataSize != list.size) {
                lastPanelDataSize = list.size
                emoticonPanelViewAdapterInstance!!::class.java
                    .method {
                        name = "notifyDataSetChanged"
                        superClass()
                    }.get(emoticonPanelViewAdapterInstance).call()
            }

            result = list
        }

        EmotionPanelViewPagerAdapter?.method {
            name = "getEmotionPanelData"
            paramCount = 3
        }?.hook()?.before {
            val pkg = args[2].get<Any>("emotionPkg") ?: return@before
            val epid = pkg.get<Any>("epId") ?: return@before
            val id = parseStickerEpId(epid as String)
            if (id != null) {
                val provider = providers.find { it.uniqueId() == id.providerId }
                if (provider != null) {
                    val panel = provider.extraEmoticonList().find { it.uniqueId() == id.panelId }
                    if (panel != null) {
                        val emoticons = panel.emoticons()
                        val list = mutableListOf<Any>()
                        for (emoticon in emoticons) {
                            list.add(emoticon.QQEmoticonObject()!!)
                        }
                        result = list
                    }
                }
            }
        }
    }

    override fun onHook() {
        withProcess(mainProcessName) {
            hookEmoticon()
        }
    }
}

private fun Any?.set(key: String, value: Any) {
    this.set(key, value, value::class.java)
}

private fun Any?.set(key: String, value: Any, fieldType: Any) {
    val instance = this ?: return
    instance::class.java.field {
        name = key
//        type = fieldType
        superClass()
    }.get(instance).set(value)
}

private fun <T> Any?.get(key: String): Any? {
    return this.get<Any>(key, AnyClass)
}

private fun <T> Any?.get(key: String, _type: Any): Any? {
    val instance = this ?: return null
    return instance::class.java.field {
        name = key
//        type = _type
        superClass()
    }.get(instance).any()
}


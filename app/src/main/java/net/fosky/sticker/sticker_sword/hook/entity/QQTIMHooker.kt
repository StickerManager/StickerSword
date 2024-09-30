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
 * This file is created by FoskyM on 2024/09/25.
 * Idea from https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/cc/microblock/hook/DumpTelegramStickers.kt
 */
@file:Suppress("ConstPropertyName")

package net.fosky.sticker.sticker_sword.hook.entity

import android.view.View;
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.allConstructors
import com.highcapable.yukihookapi.hook.factory.allFields
import com.highcapable.yukihookapi.hook.factory.allMethods
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.StringClass
import net.fosky.sticker.sticker_sword.const.PackageName
import net.fosky.sticker.sticker_sword.hook.entity.QQTIMHooker.lazyClassOrNull
import net.fosky.sticker.sticker_sword.utils.factory.*
import java.util.concurrent.Executors


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
        val PicEmoticonInfo by lazyClassOrNull("${PackageName.QQ}.emoticonview.PicEmoticonInfo")
        val Emoticon by lazyClassOrNull("${PackageName.QQ}.data.Emoticon")

        private var emoticons: List<ExtraEmoticon> = listOf()
        private var iconPath: String? = null
        private fun updateEmoticons() {
            val stickers = StickerFactory.getStickersByCategorySlug(slug)
            val emoticons = mutableListOf<ExtraEmoticon>()

            for (sticker in stickers) {
                emoticons.add(object : ExtraEmoticon() {
                    val _info = FavoriteEmoticonInfo?.buildOf()

                    init {
                        _info.set("path", sticker.path)
                        _info.set("actionData", "${uniqueId()}:${sticker.path}")
                    }
                    // 很怪的事情，过了一晚上就无法发送用 PicEmoticonInfo 增加的表情，疑似云控了？调试好久没找到哪里有做检测
                    // com.tencent.mobileqq.emoticonview.QQEmoticonPanelLinearLayoutHelper -> performClick
                    // 理论上下一步会调用 com.tencent.qqnt.aio.adapter.emoticon.NTEmoticonPanelProvider -> send
                    // 但是只执行了 -> d, -> onResume, -> onPause
                    // 不知道是不是在这之前有个校验啥的，没找到，先扔着了

//                    val _info = PicEmoticonInfo?.buildOf("uin") {
//                        param(StringClass)
//                    }
//                    val _emoticon = Emoticon?.buildOf()
//
//                    init {
//                        _emoticon?.set("eId", sticker.path)
//                        _emoticon?.set("epId", "ss:${sticker.id}")
//                        _emoticon?.set("encryptKey", "")
//                        _emoticon?.set("extensionHeight", 300)
//                        _emoticon?.set("extensionWidth", 300)
//                        _emoticon?.set("height", 200)
//                        _emoticon?.set("width", 200)
//                        _emoticon?.set("keywords", "[\"${sticker.notes}\"]")
//                        _emoticon?.set("name", sticker.notes)
//
//                        if (_emoticon != null) {
//                            _info?.set("emoticon", _emoticon)
//                        }
//
//                        _info?.set("imageType", 3)
//                    }

                    override fun QQEmoticonObject(): Any? {
                        return _info
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
    val PicEmoticonInfo by lazyClassOrNull("${PackageName.QQ}.emoticonview.PicEmoticonInfo")
    val URLDrawable by lazyClassOrNull("com.tencent.image.URLDrawable")
    val URLDrawableOptionsClassName = "com.tencent.image.URLDrawable\$URLDrawableOptions"
    val URLDrawableOptions by lazyClassOrNull(URLDrawableOptionsClassName)

    private val isQQ get() = packageName == PackageName.QQ
    private var hostVersionName = "<unknown>"

    private fun hookEmoticon() {
        // 既然暂时无法用 PicEmoticonInfo 增加表情，那这段也先注释了
//        PicEmoticonInfo?.apply {
//            method {
//                name = "getPanelImageURL"
//            }.hook().before {
//                val emoticon = PicEmoticonInfo?.field {
//                    name = "emoticon"
//                }?.get(instance)?.any()
//                val epId = emoticon?.get<Any>("epId") as String
//                if (epId.startsWith("ss:")) {
//                    val path = emoticon.get<String>("eId")
//                    result = java.net.URL("file://$path")
//                }
//            }
//            // 修复长按预览表情无法加载
//            method {
//                name = "getPicEmoticonLoadingDrawable"
//                paramCount = 7
//            }.hook().after {
//                val emoticon = PicEmoticonInfo?.field {
//                    name = "emoticon"
//                }?.get(instance)?.any()
//                val epId = emoticon?.get<Any>("epId") as String
//                if (epId.startsWith("ss:")) {
//                    val obj = args[0]
//                    val path = emoticon.get<String>("eId")
//                    val url = java.net.URL("file://$path")
//
//                    val uRLDrawableO = URLDrawableOptions?.method {
//                        name = "obtain"
//                    }?.get()?.call()
//                    obj.get<Any>("mFailed")?.let { uRLDrawableO.set("mFailedDrawable", it) }
//                    obj.get<Any>("mDefault")?.let { uRLDrawableO.set("mLoadingDrawable", it) }
//                    obj.get<Any>("reqWidth")?.let { uRLDrawableO.set("mRequestWidth", it) }
//                    obj.get<Any>("reqHeight")?.let { uRLDrawableO.set("mRequestHeight", it) }
//
//                    uRLDrawableO.set("mPlayGifImage", true)
//                    uRLDrawableO.set("mGifRefreshDelay", 0)
//
//                    val drawable = URLDrawable?.method {
//                        name = "getDrawable"
//                        param("java.net.URL", URLDrawableOptionsClassName)
//                    }?.get()?.call(url, uRLDrawableO)
//                    YLog.debug("getPicEmoticonLoadingDrawable: $drawable")
//                    URLDrawable?.apply {
//                        method {
//                            name = "setTag"
//                            paramCount = 1
//                        }.get(drawable).call(emoticon)
//                        method {
//                            name = "addHeader"
//                            paramCount = 2
//                        }.get(drawable).call("my_uin", obj.get<Any>("uin") as String)
//                        method {
//                            name = "addHeader"
//                            paramCount = 2
//                        }.get(drawable).call("emo_type", obj.get<Any>("imageType").toString())
//                        val p3 = args[3] as Boolean
//                        method {
//                            name = "addHeader"
//                            paramCount = 2
//                        }.get(drawable).call("2g_use_gif", if (p3) "true" else "false")
//                    }
//                    result = drawable
//                }
//            }
//        }

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
            if (stackTrace.any { it.className == "${PackageName.QQ}.emoticonview.EmoticonReportDtHelper" && it.methodName == "addDTReport" }) {
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
                    pack.set("type", 3)
                    pack.set("ipJumpUrl", "https://github.com/StickerManager/Release/")
                    pack.set("ipDetail", "SM")
                    pack.set("valid", true)
                    pack.set("status", 2)
                    pack.set("latestVersion", 1488377358)
                    pack.set("aio", true)

                    val type = 6
                    val column = 4

                    val info = EmoticonPanelInfo?.buildOf(type, column, pack) {
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
        val _splashActivity by lazyClassOrNull("${PackageName.QQ}.activity.SplashActivity")
        if (_splashActivity != null) {
            hookGrantFilesAccessPermission(_splashActivity!!, PackageName.QQ)
        }
        withProcess(mainProcessName) {
            hookEmoticon()
        }
    }
}

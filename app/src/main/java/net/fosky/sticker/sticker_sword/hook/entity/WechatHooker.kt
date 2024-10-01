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
 * This file is created by FoskyM on 2024/09/27.
 */
@file:Suppress("ConstPropertyName")
package net.fosky.sticker.sticker_sword.hook.entity

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.allConstructors
import com.highcapable.yukihookapi.hook.factory.allMethods
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import net.fosky.sticker.sticker_sword.const.PackageName

object WechatHooker : YukiBaseHooker()  {
    val AbsEmojiView by lazyClassOrNull("${PackageName.WECHAT}.emoji.view.AbsEmojiView")
    val BaseEmojiView by lazyClassOrNull("${PackageName.WECHAT}.emoji.view.BaseEmojiView")
    val EmojiGroupInfo by lazyClassOrNull("${PackageName.WECHAT}.storage.emotion.EmojiGroupInfo")
    val EmojiIPSetInfo by lazyClassOrNull("${PackageName.WECHAT}.storage.emotion.EmojiIPSetInfo")
    val EmojiInfo by lazyClassOrNull("${PackageName.WECHAT}.storage.emotion.EmojiInfo")
    val SmileyInfo by lazyClassOrNull("${PackageName.WECHAT}.storage.emotion.SmileyInfo")
    val SmileyPanelConfigInfo by lazyClassOrNull("${PackageName.WECHAT}.storage.emotion.SmileyPanelConfigInfo")
    val MMAnimateView by lazyClassOrNull("${PackageName.WECHAT}.plugin.gif.MMAnimateView")
    val EmojiPanelRecyclerView by lazyClassOrNull("${PackageName.WECHAT}.emoji.panel.EmojiPanelRecyclerView")
    val EmojiPanelGroupView by lazyClassOrNull("${PackageName.WECHAT}.emoji.panel.EmojiPanelGroupView")
    // 没摸明白怎么调用的
    // EmojiInfo 在微信启动时构造，看了一下是数据库读取的，混淆了类名和方法，不好调用
    // 后面没找到是哪个View调用了读取，先扔这里了
    override fun onHook() {
        YLog.info("Hooking Wechat")
//        EmojiPanelGroupView?.allConstructors { _, constructor ->
//            constructor.hook().before {
//                var argString = ""
//                for (arg in args) {
//                    argString += arg.toString() + "\n"
//                }
//                val traceElements = Thread.currentThread().stackTrace
//                var stackTraceString = ""
//                for (traceElement in traceElements) {
//                    stackTraceString += traceElement.toString() + "\n"
//                }
//                YLog.debug("EmojiPanelGroupView.constructor\n$stackTraceString")
//            }
//        }
//        EmojiPanelRecyclerView?.allConstructors { _, constructor ->
//            constructor.hook().before {
//                var argString = ""
//                for (arg in args) {
//                    argString += arg.toString() + "\n"
//                }
//                val traceElements = Thread.currentThread().stackTrace
//                var stackTraceString = ""
//                for (traceElement in traceElements) {
//                    stackTraceString += traceElement.toString() + "\n"
//                }
//                YLog.debug("EmojiPanelRecyclerView.constructor\n$stackTraceString")
//            }
//        }

//        EmojiInfo?.allConstructors { _, constructor ->
//            constructor.hook().before {
//                var argString = ""
//                for (arg in args) {
//                    argString += arg.toString() + "\n"
//                }
////                YLog.debug("EmojiInfo.constructor, args: $argString")
//                val traceElements = Thread.currentThread().stackTrace
//                var stackTraceString = ""
//                for (traceElement in traceElements) {
//                    stackTraceString += traceElement.toString() + "\n"
//                }
//                YLog.debug("EmojiInfo.constructor\n$stackTraceString")
//            }
//        }
//        EmojiInfo?.allMethods { _, method ->
//            method.hook().before {
//                if (method.name == "F0") return@before
//                var argString = ""
//                for (arg in args) {
//                    argString += arg.toString() + "\n"
//                }
//                val traceElements = Thread.currentThread().stackTrace
//                var stackTraceString = ""
//                for (traceElement in traceElements) {
//                    stackTraceString += traceElement.toString() + "\n"
//                }
//                YLog.debug("EmojiInfo.method: ${method.name}\n${argString}\n$stackTraceString")
//            }
//        }
    }
}
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
 * This file is created by FoskyM on 2024/09/27.
 */
@file:Suppress("ConstPropertyName")
package net.fosky.sticker.sticker_sword.hook.entity

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
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


    override fun onHook() {
        TODO("Not yet implemented")
    }
}
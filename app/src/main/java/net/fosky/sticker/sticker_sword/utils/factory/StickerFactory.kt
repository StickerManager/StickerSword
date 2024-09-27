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
package net.fosky.sticker.sticker_sword.utils.factory

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import org.json.JSONObject
import java.io.File
import com.highcapable.yukihookapi.hook.log.YLog
import org.json.JSONArray

data class Sticker (
    val id: String,
    val notes: String,
    val path: String
)

data class StickerCategory (
    val id: String,
    val name: String,
    val slug: String,
    val description: String,
    val stickers: List<Sticker>
)

object StickerFactory {
    val stickerManagerDataPath = "/storage/self/primary/Pictures/net.fosky.sticker.sticker_manager/data/export/data.json"

    fun isFileExists(): Boolean {
        val file = File(stickerManagerDataPath)
        return file.exists()
    }

    @SuppressLint("Range")
    fun getAllStickersByCategory(): List<StickerCategory> {
        val stickerCategories = mutableListOf<StickerCategory>()

        val stickerManagerData = File(stickerManagerDataPath)
        if (stickerManagerData.exists()) {
            val stickerManagerDataContent = stickerManagerData.readText()
            val jsonArray = JSONArray(stickerManagerDataContent)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val stickersArray = jsonObject.getJSONArray("stickers")
                val stickers = mutableListOf<Sticker>()

                for (j in 0 until stickersArray.length()) {
                    val stickerObject = stickersArray.getJSONObject(j)
                    val sticker = Sticker(
                        id = stickerObject.getString("id"),
                        notes = stickerObject.getString("notes"),
                        path = stickerObject.getString("path")
                    )
                    stickers.add(sticker)
                }

                val stickerCategory = StickerCategory(
                    id = jsonObject.getString("id"),
                    name = jsonObject.getString("name"),
                    slug = jsonObject.getString("slug"),
                    description = jsonObject.getString("description"),
                    stickers = stickers
                )
                stickerCategories.add(stickerCategory)
            }

            YLog.info("StickerManager data file loaded. ${stickerCategories.size} categories found")
        } else {
            YLog.error("StickerManager data file not found")
        }

        return stickerCategories
    }

    fun getStickersByCategorySlug(slug: String) : List<Sticker> {
        return getAllStickersByCategory().find { it.slug == slug }?.stickers ?: listOf()
    }

}

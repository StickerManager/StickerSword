# Sticker Sword
Inject stickers on app for Sticker Manager.

为 Sticker Manager 提供注入到社交平台 APP 的功能。

English | [中文文档](https://github.com/StickerManager/StickerSword/blob/main/README_ZH.md)

## Usage
1. Edit stickers.
    1. Download [Sticker Manager](https://github.com/StickerManager/Release/releases) 
    2. Create categories and import stickers.
    3. Sort and edit notes for stickers (optional).
    4. Export (json) data in `Sticker Manager`'s setting page.

2. Mount data by `Sticker Sword`
    1. Download
    2. Set the scope in LSPosed/Xposed and more.
    3. Restart target app.

## Development

1. This module is powered by [YukiHookAPI](https://highcapable.github.io/YukiHookAPI/), please view the doucument while developing.

2. Add package name in [array.xml](https://github.com/StickerManager/StickerSword/blob/main/app/src/main/res/values/array.xml)

3. Define package name in [ConstFactory](https://github.com/StickerManager/StickerSword/blob/main/app/src/main/java/net/fosky/sticker/sticker_sword/const/ConstFactory.kt)

4. Add a `Hooker` in [entity](https://github.com/StickerManager/StickerSword/blob/main/app/src/main/java/net/fosky/sticker/sticker_sword/hook/entity)

5. Add `Hook Loader` in [HookEntry](https://github.com/StickerManager/StickerSword/blob/main/app/src/main/java/net/fosky/sticker/sticker_sword/hook/HookEntry.kt)

```kotlin
    override fun onHook() = encase {
        loadApp(PackageName.QQ, PackageName.TIM) { loadHooker(QQTIMHooker) }

        // Add your hook loader
        // loadApp(PackageName.XXX) { loadHooker(XXXHooker) }
    }
```

6. Notes
    1. Code needs to be formatted, and files should be encoded in UTF-8.
    2. Commit messages **should preferably** follow the [Angular commit guidelines](https://docs.google.com/document/d/1QrDFcIiPjSLDn3EL15IJygNPiHORgU1_OOAqWjiDU5Y/edit?pli=1#heading=h.4e0o8t4fffjf).
    3. Commits should be [GPG signed](https://docs.github.com/en/authentication/managing-commit-signature-verification).

## LICENSE

- [AGPL-3.0](https://www.gnu.org/licenses/agpl-3.0.html)

```
Copyright (C) 2024 FoskyM<i@fosky.top>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

Powered by [YukiHookAPI](https://github.com/HighCapable/YukiHookAPI)
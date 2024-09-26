# Sticker Sword
Inject stickers on app for Sticker Manager.

为 Sticker Manager 提供注入到社交平台 APP 的功能。

[English](https://github.com/StickerManager/StickerSword/blob/main/README.md) | 中文文档

## 使用
1. 编辑表情贴纸
    1. 下载 [Sticker Manager](https://github.com/StickerManager/Release/releases) 
    2. 创建分类并导入表情贴纸。
    3. 排序并为贴纸设置备注（可选）
    4. 在 `Sticker Manager` 的设置页中导出数据（`JSON`）.

2. 通过 `Sticker Sword` 挂载贴纸数据
    1. 下载
    2. 在框架中设置作用域
    3. 重启目标应用

### 请勿用于非法用途

1. 本模块免费、仅供学习交流而开发，禁止用于不法用途或者倒卖。

2. 未经授权，禁止私自转载、搬运本软件的安装包及源代码到 GitHub 以外的平台。

## 开发

1. 本模块使用 [YukiHookAPI](https://highcapable.github.io/YukiHookAPI/) 开发，请在开发时查看文档。

2. 在 [array.xml](https://github.com/StickerManager/StickerSword/blob/main/app/src/main/res/values/array.xml) 中增加作用域 APP 包名

3. 在 [ConstFactory](https://github.com/StickerManager/StickerSword/blob/main/app/src/main/java/net/fosky/sticker/sticker_sword/const/ConstFactory.kt) 的 `PackageName` 定义你需要的作用域 APP 包名

4. 在 [entity](https://github.com/StickerManager/StickerSword/blob/main/app/src/main/java/net/fosky/sticker/sticker_sword/hook/entity) 中增加 `Hooker` 类

5. 在 [HookEntry](https://github.com/StickerManager/StickerSword/blob/main/app/src/main/java/net/fosky/sticker/sticker_sword/hook/HookEntry.kt) 中增加 Hook 部分

```kotlin
    override fun onHook() = encase {
        loadApp(PackageName.QQ, PackageName.TIM) { loadHooker(QQTIMHooker) }

        // 增加你的加载部分
        // loadApp(PackageName.XXX) { loadHooker(XXXHooker) }
    }
```

6. 注意事项
    1. 代码需要格式化，文件以 UTF-8 编码
    2. Commit 信息**尽量**遵守 [Angular 提交规范](https://docs.google.com/document/d/1QrDFcIiPjSLDn3EL15IJygNPiHORgU1_OOAqWjiDU5Y/edit?pli=1#heading=h.4e0o8t4fffjf)
    3. Commit 进行 [GPG 加签](https://docs.github.com/zh/authentication/managing-commit-signature-verification)

## 许可证

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

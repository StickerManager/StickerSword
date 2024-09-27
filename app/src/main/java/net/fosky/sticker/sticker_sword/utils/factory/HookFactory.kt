package net.fosky.sticker.sticker_sword.utils.factory

import com.highcapable.yukihookapi.hook.factory.field

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

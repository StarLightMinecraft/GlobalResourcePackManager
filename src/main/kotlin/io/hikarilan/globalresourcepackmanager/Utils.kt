package io.hikarilan.globalresourcepackmanager

import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.hikarilan.globalresourcepackmanager.Utils.miniMessageToComponent
import io.hikarilan.globalresourcepackmanager.Utils.toMiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import java.lang.reflect.Type

object Utils {

    val locationJsonType: Type = object : TypeToken<Map<String, Any>>() {}.type

    private val miniMessage = MiniMessage.miniMessage()

    fun String.miniMessageToComponent(): Component = miniMessage.deserialize(this)

    fun Component.toMiniMessage(): String = miniMessage.serialize(this)

    fun JsonObject.resolveResourcePackDetail(): ResourcePackDetail? {
        return ResourcePackDetail(
            url = this["url"]?.asString ?: return null,
            hash = this["hash"]?.asString ?: return null,
            required = this["required"]?.asBoolean ?: return null,
            resourcePackPrompt = this["resourcePackPrompt"]?.asString?.miniMessageToComponent() ?: Component.empty()
        )
    }

    fun ResourcePackRegion.detailToJsonObject(): JsonObject {
        return JsonObject().apply {
            addProperty("type", type.toString())
            add("detail", JsonObject().apply {
                addProperty("url", detail.url)
                addProperty("hash", detail.hash)
                addProperty("required", detail.required)
                detail.resourcePackPrompt?.let { addProperty("resourcePackPrompt", it.toMiniMessage()) }
            })
            addProperty("isShowPromptInChatScreen", isShowPromptInChatScreen)
        }
    }


}
package io.hikarilan.globalresourcepackmanager

import com.google.gson.JsonObject
import io.hikarilan.globalresourcepackmanager.Utils.detailToJsonObject
import io.hikarilan.globalresourcepackmanager.Utils.resolveResourcePackDetail
import io.hikarilan.globalresourcepackmanager.Utils.toMiniMessage
import org.bukkit.Bukkit
import java.util.*

interface RegionResolver {

    fun resolve(jsonObject: JsonObject): ResourcePackRegion?

    fun save(region: ResourcePackRegion): JsonObject?

}

object GlobalRegionResolver : RegionResolver {

    override fun resolve(jsonObject: JsonObject): ResourcePackRegion? {
        if (jsonObject["type"].asString != ResourcePackRegion.RegionType.GLOBAL.toString()) return null
        val detail = jsonObject["detail"].asJsonObject
        return GlobalResourcePackRegion(
            detail = detail.resolveResourcePackDetail() ?: return null,
            isShowPromptInChatScreen = jsonObject["isShowPromptInChatScreen"]?.asBoolean ?: true
        )
    }

    override fun save(region: ResourcePackRegion): JsonObject? {
        if (region.type != ResourcePackRegion.RegionType.GLOBAL) return null
        return region.detailToJsonObject()
    }

}

object CustomRegionResolver : RegionResolver {

    override fun resolve(jsonObject: JsonObject): ResourcePackRegion? {
        if (jsonObject["type"].asString != ResourcePackRegion.RegionType.CUSTOM.toString()) return null
        val detail = jsonObject["detail"].asJsonObject
        return CustomResourcePackRegion(
            owner = Bukkit.getOfflinePlayer(UUID.fromString(jsonObject["owner"]?.asString ?: return null)),
            region = CuboidBoundBox(jsonObject["region"]?.asJsonObject ?: return null),
            pDetail = detail.resolveResourcePackDetail() ?: return null,
            isShowPromptInChatScreen = jsonObject["isShowPromptInChatScreen"]?.asBoolean ?: true
        )
    }

    override fun save(region: ResourcePackRegion): JsonObject? {
        if (region.type != ResourcePackRegion.RegionType.CUSTOM) return null
        if (region !is CustomResourcePackRegion) return null
        return JsonObject().apply {
            addProperty("type", region.type.toString())
            addProperty("owner", region.owner.uniqueId.toString())
            add("region", region.region.toJsonObject())
            add("detail", JsonObject().apply {
                addProperty("url", region.detail.url)
                addProperty("hash", region.detail.hash)
                addProperty("required", region.detail.required)
                region.pDetail.resourcePackPrompt?.let { addProperty("resourcePackPrompt", it.toMiniMessage()) }
            })
            addProperty("isShowPromptInChatScreen", region.isShowPromptInChatScreen)
        }
    }


}
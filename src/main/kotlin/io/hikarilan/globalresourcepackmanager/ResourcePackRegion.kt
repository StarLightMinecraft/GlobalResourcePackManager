package io.hikarilan.globalresourcepackmanager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Location
import org.bukkit.OfflinePlayer

interface ResourcePackRegion {

    val type: RegionType

    val detail: ResourcePackDetail

    val isShowPromptInChatScreen: Boolean

    fun isInRegion(location: Location): Boolean

    fun priority(): Int

    enum class RegionType {
        GLOBAL,
        CUSTOM
    }

}

class GlobalResourcePackRegion(
    override val detail: ResourcePackDetail,
    override val isShowPromptInChatScreen: Boolean
) : ResourcePackRegion {
    override val type: ResourcePackRegion.RegionType = ResourcePackRegion.RegionType.GLOBAL

    override fun isInRegion(location: Location): Boolean = true

    override fun priority(): Int = -1
}

class CustomResourcePackRegion(
    val owner: OfflinePlayer,
    val region: IBoundBox,
    val pDetail: ResourcePackDetail,
    override val isShowPromptInChatScreen: Boolean
) : ResourcePackRegion {

    override val type: ResourcePackRegion.RegionType = ResourcePackRegion.RegionType.CUSTOM

    override val detail: ResourcePackDetail = pDetail.copy(
        required = false,
        resourcePackPrompt = Component
            .text("您正在访问的区域所有者 ").color(TextColor.color(0xFFA101))
            .append(Component.text(owner.name ?: "null"))
            .append(Component.text(" 建议您安装指定资源包以获得最佳体验").color(TextColor.color(0xFFA101)))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("以下是来自区域所有者的消息：").color(TextColor.color(0xB3DEE5)))
            .append(Component.newline())
            .append(Component.newline())
            .append(pDetail.resourcePackPrompt ?: Component.empty())
            .append(Component.newline())
    )

    override fun isInRegion(location: Location): Boolean = region.checkIn(location)

    override fun priority(): Int = 1


}
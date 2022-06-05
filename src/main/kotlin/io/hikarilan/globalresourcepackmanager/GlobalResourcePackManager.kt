package io.hikarilan.globalresourcepackmanager

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Files
import java.util.*
import kotlin.io.path.reader

class GlobalResourcePackManager : JavaPlugin(), Listener {

    companion object {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    }

    private val file = dataFolder.toPath().resolve("regions.json")

    private val regions = mutableListOf<ResourcePackRegion>()

    private val resolvers = listOf(GlobalRegionResolver, CustomRegionResolver)

    // yep this is used player because we need reload the resource pack when player reconnect to the server
    private val usingResourcePack = WeakHashMap<Player, ResourcePackDetail>()

    override fun onEnable() {
        saveResource("regions.json", false)

        load()

        logger.info("Loaded ${regions.size} region(s)")

        Bukkit.getPluginManager().registerEvents(this, this)
    }

    override fun onDisable() {
        save()
    }

    @EventHandler
    private fun onJoin(e: PlayerJoinEvent) {
        e.player.checkRegionAndApplyResourcePack()
    }

    @EventHandler
    private fun onMove(e: PlayerMoveEvent) {
        if (!e.hasChangedPosition()) return
        e.player.checkRegionAndApplyResourcePack()
    }

    private fun Player.checkRegionAndApplyResourcePack() {
        regions.filter { it.isInRegion(this.location) }.maxByOrNull { it.priority() }?.let {
            if (usingResourcePack[this] == it.detail) return
            if (it.isShowPromptInChatScreen) {
                when (this.resourcePackStatus) {
                    PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED, PlayerResourcePackStatusEvent.Status.DECLINED -> {
                        sendMessage(it.detail.resourcePackPrompt ?: Component.empty())
                    }
                    else -> {/* just ignore it */
                    }
                }
            }
            this.setResourcePack(it.detail.url, it.detail.hash, it.detail.required, it.detail.resourcePackPrompt)
            usingResourcePack[this] = it.detail
        }
    }

    private fun load() {
        for (i in gson.fromJson(file.reader(), JsonArray::class.java)) {
            if (!i.isJsonObject) continue
            regions.add(resolvers.firstNotNullOf { it.resolve(i.asJsonObject) })
        }
    }

    private fun save() {
        Files.writeString(file, gson.toJson(JsonArray().apply {
            for (i in regions) {
                add(resolvers.firstNotNullOf { it.save(i) })
            }
        }))
    }


}
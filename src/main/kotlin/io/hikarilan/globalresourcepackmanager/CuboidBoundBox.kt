package io.hikarilan.globalresourcepackmanager

import com.google.gson.JsonObject
import org.bukkit.Location
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class CuboidBoundBox(
    val from: Location,
    val to: Location
) : IBoundBox {

    constructor(jsonObject: JsonObject) : this(
        from = Location.deserialize(
            GlobalResourcePackManager.gson.fromJson(
                jsonObject.getAsJsonObject("from"),
                Utils.locationJsonType
            )
        ),
        to = Location.deserialize(
            GlobalResourcePackManager.gson.fromJson(
                jsonObject.getAsJsonObject("to"),
                Utils.locationJsonType
            )
        )
    )

    private val minX = min(from.blockX, to.blockX)
    private val minY = min(from.blockY, to.blockY)
    private val minZ = min(from.blockZ, to.blockZ)
    private val maxX = max(from.blockX, to.blockX)
    private val maxY = max(from.blockY, to.blockY)
    private val maxZ = max(from.blockZ, to.blockZ)

    override fun checkIn(loc: Location): Boolean =
        loc.world == from.world && loc.world == to.world
                && loc.blockX in minX..maxX && loc.blockY in minY..maxY && loc.blockZ in minZ..maxZ

    override fun randomLocation(): Location {
        val xLen = maxX - minX + 1
        val yLen = maxY - minY + 1
        val zLen = maxZ - minZ + 1
        return Location(
            from.world,
            (Random.nextInt(xLen) + minX).toDouble(),
            (Random.nextInt(yLen) + minY).toDouble(),
            (Random.nextInt(zLen) + minZ).toDouble()
        )
    }

    override fun toJsonObject(): JsonObject {
        return JsonObject().apply {
            add(
                "from", GlobalResourcePackManager.gson.toJsonTree(
                    from.serialize(),
                    Utils.locationJsonType
                )
            )
            add(
                "to", GlobalResourcePackManager.gson.toJsonTree(
                    to.serialize(),
                    Utils.locationJsonType
                )
            )
        }
    }

}
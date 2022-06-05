package io.hikarilan.globalresourcepackmanager

import com.google.gson.JsonObject
import org.bukkit.Location
import org.bukkit.World

/**
 * 代表一个范围
 */
interface IBoundBox {

    /**
     * 检查指定位置是否处于范围内
     *
     * @param loc 指定位置
     *
     * @return 是否处于范围内
     */
    fun checkIn(loc: Location): Boolean

    /**
     * 获得一个范围内的随机位置
     *
     * @return 随机位置
     */
    fun randomLocation(): Location

    fun toJsonObject(): JsonObject
}
package net.darkmeow.irc.data.datas

import io.netty.channel.Channel

/**
 * 客户端连接数据
 *
 * @param address 客户端 IP 地址
 * @param connectTimestamp 客户端连接时间
 * @param channel Netty Channel
 */
data class ConnectData(
    val address: String,
    val connectTimestamp: Long,
    val channel: Channel
)

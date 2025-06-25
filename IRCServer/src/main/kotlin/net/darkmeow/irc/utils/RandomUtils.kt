package net.darkmeow.irc.utils

import kotlin.random.Random

object RandomUtils {
    /**
     * 生成随机字节集
     *
     * @param size 长度
     */
    fun randomByteArray(size: Int = 16) = ByteArray(size) { Random.nextBytes(1)[0] }

}
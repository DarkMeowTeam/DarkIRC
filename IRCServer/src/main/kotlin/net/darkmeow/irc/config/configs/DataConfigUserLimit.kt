package net.darkmeow.irc.config.configs

import kotlinx.serialization.Serializable

@Serializable
data class DataConfigUserLimit(
    /**
     * 是否允许多设备同时登录
     */
    val allowMultiDeviceLogin: Boolean = false
)
package net.darkmeow.irc.config

import net.darkmeow.irc.config.configs.Config
import net.darkmeow.irc.utils.ymal.YamlConfig
import org.apache.logging.log4j.LogManager
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.readText

class ConfigManager {
    val logger = LogManager.getLogger(ConfigManager::class.java)

    private var configFile: String = "Config.yml"

    lateinit var configs: Config

    fun readConfig() {
        try {
            configs = YamlConfig.loadFromString(Paths.get(configFile).readText(StandardCharsets.UTF_8), Config::class.java)

            logger.info("加载配置成功")
        } catch (e: Exception) {
            logger.warn("加载配置失败", e)
            defaultConfig()
        }
    }

    fun saveConfig() {
        try {
            Files.write(Paths.get(configFile), YamlConfig.saveToString(configs).toByteArray(StandardCharsets.UTF_8))

            logger.info("保存配置成功")
        } catch (e: Exception) {
            logger.warn("保存配置失败", e)
        }
    }

    fun defaultConfig(forceSave: Boolean = false) {
        if (forceSave || !Files.exists(Paths.get(configFile))) {
            configs = Config()

            saveConfig()
        }
    }

}

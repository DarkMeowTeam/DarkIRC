package net.darkmeow.irc.config

import net.darkmeow.irc.config.configs.Config
import net.darkmeow.irc.utils.CryptUtils
import net.darkmeow.irc.utils.YamlUtils
import org.apache.logging.log4j.LogManager
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.security.PublicKey

class ConfigManager {
    companion object {
        val CONFIG_FILE: File = Paths.get("config.yml").toFile()
        val SIGNATURE_PUBLIC_KEY: File = Paths.get("public.pem").toFile()
        val SIGNATURE_PRIVATE_KEY: File = Paths.get("private.pem").toFile()
    }

    val logger = LogManager.getLogger(ConfigManager::class.java)

    lateinit var configs: Config
    lateinit var signatureKey: PublicKey

    fun readConfig() {
        try {
            if (CONFIG_FILE.exists()) {
                configs = YamlUtils.loadFromString(CONFIG_FILE.readText(StandardCharsets.UTF_8), Config::class.java)
            } else {
                configs = Config()
                CONFIG_FILE.writeText(YamlUtils.saveToString(configs), StandardCharsets.UTF_8)
                logger.info("生成默认配置")
            }


            if (configs.ircServer.signature) {
                if (SIGNATURE_PUBLIC_KEY.exists()) {
                    signatureKey = CryptUtils.loadPublicKeyFromPEM(SIGNATURE_PUBLIC_KEY)
                    val privateKey = CryptUtils.loadPrivateKeyFromPEM(SIGNATURE_PRIVATE_KEY)
                    if (!CryptUtils.verifyCode("test", CryptUtils.signCode("test", privateKey), signatureKey)) {
                        throw RuntimeException("签名密钥对验证失败 请删除后重新生成")
                    }
                } else {
                    val keyPair = CryptUtils.generateKeyPair()
                    signatureKey = keyPair.public

                    CryptUtils.exportPublicKeyToPEM(keyPair.public, SIGNATURE_PUBLIC_KEY)
                    CryptUtils.exportPrivateKeyToPEM(keyPair.private, SIGNATURE_PRIVATE_KEY)

                    logger.info("生成密钥对成功")
                }
            }

            logger.info("加载配置成功")
        } catch (e: Exception) {
            throw RuntimeException("加载配置失败", e)
        }
    }

}

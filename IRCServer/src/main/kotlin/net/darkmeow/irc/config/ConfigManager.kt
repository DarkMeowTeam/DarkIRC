package net.darkmeow.irc.config

import com.charleskorn.kaml.Yaml
import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.config.configs.DataConfigRoot
import net.darkmeow.irc.utils.CryptUtils
import net.darkmeow.irc.utils.RandomUtils
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.security.PrivateKey
import java.security.PublicKey
import java.util.Base64

class ConfigManager(
    val system: IRCServer
) {
    companion object {
        val CONFIG_FILE: File = Paths.get("config.yml").toFile()
        val SIGNATURE_PUBLIC_KEY: File = Paths.get("server_public.pem").toFile()
        val SIGNATURE_PRIVATE_KEY: File = Paths.get("server_private.pem").toFile()
    }

    lateinit var configs: DataConfigRoot
    lateinit var serverKey: PrivateKey

    fun readConfig() {
        try {
            if (CONFIG_FILE.exists()) {
                configs = Yaml.default.decodeFromString(
                    deserializer = DataConfigRoot.serializer(),
                    string = CONFIG_FILE.readText(
                        charset = StandardCharsets.UTF_8
                    )
                )
            } else {
                configs = DataConfigRoot()

                CONFIG_FILE.writeText(
                    text = Yaml.default.encodeToString(
                        serializer = DataConfigRoot.serializer(),
                        value = configs
                    ),
                    charset = StandardCharsets.UTF_8
                )
                system.logger.info("生成默认配置")
            }

            if (SIGNATURE_PUBLIC_KEY.exists()) {
                serverKey = CryptUtils.loadPrivateKeyFromPEM(SIGNATURE_PRIVATE_KEY)
                val publicKey = CryptUtils.loadPublicKeyFromPEM(SIGNATURE_PUBLIC_KEY)
                val signatureData = RandomUtils.randomByteArray(size = 16)
                if (!CryptUtils.verifyData(signatureData, CryptUtils.signData(signatureData, serverKey), publicKey)) {
                    throw RuntimeException("签名密钥对验证失败 请删除后重新生成")
                }
            } else {
                val keyPair = CryptUtils.generateKeyPair()
                serverKey = keyPair.private

                CryptUtils.exportPublicKeyToPEM(keyPair.public, SIGNATURE_PUBLIC_KEY)
                CryptUtils.exportPrivateKeyToPEM(keyPair.private, SIGNATURE_PRIVATE_KEY)

                system.logger.info(
                    StringBuilder()
                        .apply {
                            appendLine("生成密钥对成功")
                            Base64
                                .getMimeEncoder(64, byteArrayOf('\n'.code.toByte()))
                                .encodeToString(keyPair.public.encoded)
                                .split("\n")
                                .forEach { text ->
                                    appendLine("    $text")
                                }
                        }
                        .toString()
                )
            }

            system.logger.info("加载配置成功")
        } catch (e: Exception) {
            throw RuntimeException("加载配置失败", e)
        }
    }

}

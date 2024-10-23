package net.darkmeow.irc.utils.ymal

import com.esotericsoftware.yamlbeans.YamlException
import com.esotericsoftware.yamlbeans.YamlConfig
import com.esotericsoftware.yamlbeans.YamlReader
import com.esotericsoftware.yamlbeans.YamlWriter
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter

object YamlConfig {
    @Throws(YamlException::class)
    fun saveToString(o: Any?): String {
        val yamlConfig = YamlConfig()
        yamlConfig.writeConfig.setWriteDefaultValues(true)
        yamlConfig.writeConfig.setEscapeUnicode(false)
        yamlConfig.writeConfig.setWriteRootElementTags(false)
        yamlConfig.writeConfig.setWriteRootTags(false)
        yamlConfig.writeConfig.setUseVerbatimTags(false)
        yamlConfig.writeConfig.setWriteClassname(com.esotericsoftware.yamlbeans.YamlConfig.WriteClassName.NEVER)
        val stringWriter = StringWriter()
        val yamlWriter = YamlWriter(stringWriter, yamlConfig)
        yamlWriter.write(o)
        yamlWriter.close()
        return stringWriter.toString()
    }

    @Throws(IOException::class)
    fun <T> loadFromString(s: String, clazz: Class<T>?): T {
        val yamlReader = YamlReader(StringReader(s))
        val o: T = yamlReader.read(clazz)
        yamlReader.close()
        return o
    }
}

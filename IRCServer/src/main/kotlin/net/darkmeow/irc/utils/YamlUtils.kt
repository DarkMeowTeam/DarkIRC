package net.darkmeow.irc.utils

import com.esotericsoftware.yamlbeans.YamlConfig
import com.esotericsoftware.yamlbeans.YamlReader
import com.esotericsoftware.yamlbeans.YamlWriter
import java.io.StringReader
import java.io.StringWriter

object YamlUtils {

    fun saveToString(other: Any) = StringWriter().also { writer ->
        YamlWriter(writer, YamlConfig().apply {
            writeConfig.setWriteDefaultValues(true)
            writeConfig.setEscapeUnicode(false)
            writeConfig.setWriteRootElementTags(false)
            writeConfig.setWriteRootTags(false)
            writeConfig.setUseVerbatimTags(false)
            writeConfig.setWriteClassname(YamlConfig.WriteClassName.NEVER)
        }).apply {
            write(other)
            close()
        }
    }.toString()

    fun <Data> loadFromString(s: String, clazz: Class<Data>?): Data = YamlReader(StringReader(s)).let { reader ->
        val data = reader.read(clazz)
        reader.close()

        return@let data
    }
}
package net.darkmeow.irc.web.utils

import com.sun.net.httpserver.HttpExchange
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object RequestParamsUtils {
    fun getParams(t: HttpExchange): HashMap<String, String> {
        val map = HashMap<String, String>()

        try {
            val query = t.requestURI.rawQuery

            for (param in query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                val keyValue = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (keyValue.size > 1) {
                    // 对key和value进行分别解码
                    val key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8)
                    val value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                    map[key] = value
                } else if (keyValue.size == 1) {
                    // 只有key没有value时
                    val key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8)
                    map[key] = ""
                }
            }
        } catch (_: Throwable) { }

        return map
    }
}
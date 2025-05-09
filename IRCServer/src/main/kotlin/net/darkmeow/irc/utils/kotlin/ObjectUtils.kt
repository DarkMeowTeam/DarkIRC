package net.darkmeow.irc.utils.kotlin

object ObjectUtils {
    /**
     * 传入对象列表是否全部为空
     *
     * @param obj 对象列表
     *
     * @return 是否全部为空
     */
    fun allNull(vararg obj: Any?) = obj.all { it == null }
}
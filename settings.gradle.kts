rootProject.name = "DarkIRC"

include(":IRCLib", ":IRCServer", ":IRCClient")

pluginManagement {
    repositories {
        mavenLocal()

        maven("https://maven.aliyun.com/repository/central/") // mavenCentral()

        gradlePluginPortal()
    }

    val kotlinVersion: String by settings
    val ktorVersion: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("io.ktor.plugin") version ktorVersion
    }
}
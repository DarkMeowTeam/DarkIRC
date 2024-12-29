rootProject.name = "DarkIRC"

include(":IRCLib", ":IRCServer", ":IRCClient")

pluginManagement {
    repositories {
        mavenLocal()

        maven("https://maven.aliyun.com/repository/central/") // mavenCentral()

        gradlePluginPortal()
    }

    val kotlinVersion: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
    }
}
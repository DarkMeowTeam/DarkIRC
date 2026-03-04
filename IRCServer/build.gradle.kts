plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)

    application
}

application {
    mainClass.set("net.darkmeow.irc.IRCServerLoader")

    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

dependencies {
    implementation(project(":IRCLib"))

    // Logger
    implementation(libs.slf4j.api)
    implementation(libs.log4j.core)
    implementation(libs.log4j.slf4j2.impl)

    // Config
    implementation(libs.kaml)

    // Database
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.time)
    implementation(libs.sqlite.jdbc)
    implementation(libs.mysql.connector.j)

    implementation(libs.jbcrypt)

    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-host-common")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-serialization-jackson")
}

tasks {
    startScripts {
        doLast {
            windowsScript.apply {
                windowsScript
                    .readText()
                    .replace(
                        Regex("set CLASSPATH=.*"),
                        """set CLASSPATH=%APP_HOME%\\lib\\*"""
                    )
                    .also { writeText(it) }
            }
        }
    }
}
val slf4jVersion: String by project
val apacheCommonsCompressVersion: String by project
val tukaaniXZVersion: String by project
val apacheLog4jVersion: String by project
val gsonVersion: String by project
val sqliteJdbcVersion: String by project
val exposedVersion: String by project
val jbcryptVersion: String by project

plugins {
    java
    kotlin("jvm")

    id("io.ktor.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")

    application
}

application {
    mainClass.set("net.darkmeow.irc.IRCServerLoaderKt")
}

dependencies {
    implementation(project(":IRCLib"))

    implementation(kotlin("stdlib"))

    implementation("org.slf4j:slf4j-api:${slf4jVersion}")
    implementation("org.apache.logging.log4j:log4j-core:${apacheLog4jVersion}")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:${apacheLog4jVersion}")

    implementation("org.apache.commons:commons-compress:${apacheCommonsCompressVersion}")
    implementation("org.tukaani:xz:${tukaaniXZVersion}")

    implementation("com.google.code.gson:gson:${gsonVersion}")
    implementation("com.esotericsoftware.yamlbeans:yamlbeans:1.17")

    implementation("org.xerial:sqlite-jdbc:${sqliteJdbcVersion}")

    implementation("org.jetbrains.exposed:exposed-core:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposedVersion}")

    implementation("org.mindrot:jbcrypt:${jbcryptVersion}")

    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-jackson")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-host-common")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-auth")
}

tasks {
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    kotlin {
        jvmToolchain(17)
    }

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
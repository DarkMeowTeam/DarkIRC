val gsonVersion: String by project
val sqliteJdbcVersion: String by project
val exposedVersion: String by project
val log4jVersion: String by project

plugins {
    java
    kotlin("jvm")

    application
}

application {
    mainClass.set("net.darkmeow.irc.IRCServerLoaderKt")
}

dependencies {
    implementation(project(":IRCLib"))

    implementation(kotlin("stdlib"))

    implementation("com.google.code.gson:gson:${gsonVersion}")
    implementation("org.apache.logging.log4j:log4j-core:${log4jVersion}")
    implementation("com.esotericsoftware.yamlbeans:yamlbeans:1.17")

    implementation("org.xerial:sqlite-jdbc:${sqliteJdbcVersion}")

    implementation("org.jetbrains.exposed:exposed-core:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposedVersion}")
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
}
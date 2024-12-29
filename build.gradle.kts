val baseGroup: String by project
val baseVersion: String by project

val nettyVersion: String by project
val gsonVersion: String by project

plugins {
    idea

    java
    kotlin("jvm")
}

group = baseGroup
version = baseVersion

allprojects {
    group = baseGroup
    version = baseVersion

    apply {
        plugin("idea")

        plugin("java")
        plugin("kotlin")
    }

    repositories {
        mavenLocal()

        maven("https://maven.aliyun.com/repository/central/") // mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        implementation("io.netty:netty-all:${nettyVersion}")
        implementation("com.google.code.gson:gson:${gsonVersion}")
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }
    }
}
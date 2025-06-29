val baseGroup: String by project
val baseVersion: String by project

val nettyVersion: String by project
val annotationsVersion: String by project
val lombokVersion: String by project

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
    }

    repositories {
        mavenLocal()
        mavenCentral()

        maven("https://maven.aliyun.com/repository/central/")
        maven("https://jitpack.io")
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:${annotationsVersion}")

        compileOnly("org.projectlombok:lombok:${lombokVersion}")
        annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    }

    tasks {
        test {
            useJUnitPlatform()
        }

        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }
    }
}

arrayOf(":IRCLib", ":IRCClient").forEach { projName ->
    project(projName) {
        dependencies {
            implementation("io.netty:netty-transport:${nettyVersion}")
            implementation("io.netty:netty-codec:${nettyVersion}")
            implementation("io.netty:netty-handler-proxy:${nettyVersion}")
            implementation("io.netty:netty-handler:${nettyVersion}")
        }
    }
}

project(":IRCServer") {
    dependencies {
        implementation("io.netty:netty-all:${nettyVersion}")
    }
}
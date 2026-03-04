val baseGroup: String by project
val baseVersion: String by project

val nettyVersion: String by project
val annotationsVersion: String by project
val lombokVersion: String by project

plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = baseGroup
version = baseVersion

allprojects {
    group = baseGroup
    version = baseVersion

    apply {
        plugin("java")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:${annotationsVersion}")

        compileOnly("org.projectlombok:lombok:${lombokVersion}")
        annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    }

    tasks {
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
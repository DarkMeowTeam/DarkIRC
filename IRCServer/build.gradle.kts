plugins {
    kotlin("jvm") version "2.0.20"
    application
}

group = "net.darkmeow"
version = "0.0.1"

application {
    mainClass.set("net.darkmeow.irc.IRCServerLoaderKt")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("com.esotericsoftware.yamlbeans:yamlbeans:1.17")
}


tasks {
    jar {
        manifest {
            attributes(
                "Main-Class" to "net.darkmeow.irc.IRCServerLoaderKt"
            )
        }

        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
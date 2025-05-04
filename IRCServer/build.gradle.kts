val gsonVersion: String by project

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
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("com.esotericsoftware.yamlbeans:yamlbeans:1.17")
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")
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
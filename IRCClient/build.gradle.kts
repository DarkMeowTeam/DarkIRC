plugins {
    kotlin("jvm") version "2.0.20"
    `maven-publish`
}

group = "net.darkmeow"
version = "1.0.0"


repositories {
    mavenCentral()
}

dependencies {
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "irc"
            version = project.version.toString()
        }
    }
    repositories {
        mavenLocal()
    }
}

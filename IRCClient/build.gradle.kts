plugins {
    kotlin("jvm") version "2.0.21"
    `maven-publish`
}

group = "net.darkmeow"
version = "1.0.1119"


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

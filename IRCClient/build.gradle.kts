plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.0"

    `maven-publish`
}

dependencies {
    implementation(project(":IRCLib"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.shadowJar {
    archiveClassifier.set("")

    relocate("com.google", "${project.group}.irc.lib.com.google")
    relocate("io.netty", "${project.group}.irc.lib.io.netty")

    minimize()
}

tasks.build {
    dependsOn(tasks.shadowJar)
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
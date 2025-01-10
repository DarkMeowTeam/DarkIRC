plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"

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

val sourceJar = tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.build {
    dependsOn(sourceJar)
    dependsOn(tasks.shadowJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.shadowJar)
            artifact(sourceJar)
            groupId = project.group.toString()
            artifactId = "IRCClient"
            version = project.version.toString()
        }
    }
    repositories {
        mavenLocal()
    }
}
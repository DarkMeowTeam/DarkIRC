import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

dependencies {
    implementation(project(":IRCLib"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
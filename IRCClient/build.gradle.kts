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
    archiveClassifier.set("all")

    relocate("com.google", "${project.group}.irc.lib.com.google")
    relocate("io.netty", "${project.group}.irc.lib.io.netty")

    minimize()
}

val sourceJar = tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("normal") {
            artifactId = "IRCClient"
            groupId = project.group.toString()
            version = project.version.toString()

            artifact(tasks.jar)
            artifact(sourceJar)

            pom {
                withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")

                    configurations.compileClasspath.get().allDependencies.forEach {
                        val dependencyNode = dependenciesNode.appendNode("dependency")

                        dependencyNode.appendNode("groupId", it.group)
                        dependencyNode.appendNode("artifactId", it.name)
                        dependencyNode.appendNode("version", it.version)
                    }
                }
            }
        }

        create<MavenPublication>("all") {
            artifactId = "IRCClient-all"
            groupId = project.group.toString()
            version = project.version.toString()

            artifact(tasks.shadowJar) {
                classifier = ""
            }
        }
    }
    repositories {
        mavenLocal()
    }
}
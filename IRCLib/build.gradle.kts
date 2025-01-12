plugins {
    java
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


val sourceJar = tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("normal") {
            artifactId = "IRCLib"
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
            repositories {
                mavenLocal()
            }
        }
    }
}
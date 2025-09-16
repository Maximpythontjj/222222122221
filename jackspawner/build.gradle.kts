plugins {
    java
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "com.github.jackspawner"
version = "1.1.2"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveFileName.set("JackSpawner-${version}.jar")
        minimize()
    }
    
    build {
        dependsOn(shadowJar)
    }
    
    processResources {
        filesMatching("plugin.yml") {
            expand(
                "version" to project.version,
                "name" to project.name
            )
        }
    }
}
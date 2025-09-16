plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.jackspawner"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
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
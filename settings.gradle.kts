import java.util.*

rootProject.name = "kotlin-source-text-field"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/amper/amper")
        maven("https://www.jetbrains.com/intellij-repository/releases")
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}


dependencyResolutionManagement {
    fun File.loadProperties(): Properties {
        val properties = Properties()
        this.bufferedReader().use { reader ->
            properties.load(reader)
        }
        return properties
    }

    fun getPassword(): String {
        val propertiesFile: File = rootProject.projectDir.resolve("local.properties")
        val properties = if (propertiesFile.exists()) propertiesFile.loadProperties() else mapOf()
        val githubPersonalToken = properties.getOrDefault("githubPersonalToken", null) as String?
        return githubPersonalToken
            ?: System.getProperty("GITHUB_TOKEN", "").takeIf { it.isNotEmpty() }
            ?: System.getenv("GITHUB_TOKEN")
            ?: error("No token found")
    }
    @Suppress("UnstableApiUsage") 
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        maven {
            url = uri("https://maven.pkg.github.com/zhelenskiy/AnimateContentSingleDimension")
            credentials {
                username = "zhelenskiy"
                password = getPassword()
            }
        }

        maven {
            url = uri("https://maven.pkg.github.com/zhelenskiy/BasicSourceCodeEditor")
            credentials {
                username = "zhelenskiy"
                password = getPassword()
            }
        }
    }
}

include("library", "sample")

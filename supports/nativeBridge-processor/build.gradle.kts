val kspVersion: String = "1.8.0-1.0.8"

plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

group = "com.subscribe.nativebridge.processor"
version = project.properties["publish.version"].toString()

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("com.squareup:kotlinpoet:1.10.2")
                implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
                implementation(project(":nativeBridge-annotation"))
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }
    }
}

val repoPath: String = providers.gradleProperty("subscribe.repo.maven.local").get()
val repoDir: String =
    if (repoPath.startsWith(".")) rootProject.file(repoPath).absolutePath else repoPath

publishing {
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri(repoDir)
        }
    }
}
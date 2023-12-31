val kspVersion: String = "1.9.10-1.0.13"

plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

group = "com.subscribe.nativebridge.processor"
version = providers.gradleProperty("subscribe.repo.maven.version").get()

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
            name = "NativeProcessor"
            url = uri(repoDir)
        }
    }
}
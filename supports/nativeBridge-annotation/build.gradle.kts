plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("org.jetbrains.kotlin.plugin.serialization") version("1.8.10")
}

group = "com.subscribe.nativebridge.annotation"
version = project.properties["publish.version"].toString()

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    )
    macosArm64()
    macosX64()
    mingwX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:atomicfu:0.21.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.0")
            }
        }
    }
}

android {
    namespace = "com.subscribe.nativebridge.annotation"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
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


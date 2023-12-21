plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
}

version = providers.gradleProperty("subscribe.repo.maven.version").get()

gradlePlugin {
    plugins {
        create("kotlinPlugin") {
            id = "com.subscribe.nativebridge"
            group = "com.subscribe.nativebridge"
            implementationClass = "com.subscribe.gradlek.NativeBridgePlugin"
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

dependencies {
    implementation(libs.kotlinGradlePlugin)
    compileOnly(libs.kotlinGradlePlugin)
}

val repoPath: String = providers.gradleProperty("subscribe.repo.maven.local").get()
val repoDir: String =
    if (repoPath.startsWith(".")) rootProject.file(repoPath).absolutePath else repoPath

publishing {
    repositories {
        maven {
            name = "NativeBridge"
            url = uri(repoDir)
        }
    }
}

tasks.named("compileKotlin") {
    doFirst {
        val pluginVersion: String = providers.gradleProperty("subscribe.repo.maven.version").get()
        val kotlinCode = """
            package com.subscribe.nativebridge
            object BuildConfig {
                const val PLUGIN_VERSION: String = "$pluginVersion"
            }
        """.trimIndent()
        val geneDir = file("${project.layout.buildDirectory.asFile.get()}/generated/source/kotlin")
        geneDir.mkdirs()
        val genKtFile = File(geneDir, "BuildConfig.kt")
        genKtFile.delete()
        genKtFile.createNewFile()
        genKtFile.writeText(kotlinCode)
        println("BuildConfig constants generated: $genKtFile")
    }
}

sourceSets {
    main {
        kotlin {
            srcDirs("${project.layout.buildDirectory.asFile.get()}/generated/source/kotlin")
        }
    }
}



plugins {
    id("groovy")
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
}

version = project.properties["publish.version"].toString()

gradlePlugin {
    plugins {
        create("kotlinPlugin") {
            id = "com.subscribe.gradlek"
            group = "com.subscribe.gradlek"
            implementationClass = "com.subscribe.gradlek.NativeBridgePlugin"
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
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


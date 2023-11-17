enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        maven {
            url = uri(providers.gradleProperty("subscribe.repo.maven.remote").get())
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        maven {
            url = uri(providers.gradleProperty("subscribe.repo.maven.remote").get())
        }
        mavenCentral()
    }
}

gradle.addBuildListener(object : BuildListener {
    override fun settingsEvaluated(settings: Settings) {
        // empty impl
    }

    override fun projectsLoaded(gradle: Gradle) {
        println("[Gradle初始化完成...]")
    }

    override fun projectsEvaluated(gradle: Gradle) {
        println("[Gradle配置完成...]")
    }

    override fun buildFinished(result: BuildResult) {
        println("[Gradle执行完成...]")
    }
})

rootProject.name = "KmpNativeBridge"
include(":publish")
project(":publish").projectDir = file("publish")

include("nativeBridge-annotation")
project(":nativeBridge-annotation").projectDir = file("supports/nativeBridge-annotation")
include("nativeBridge-processor")
project(":nativeBridge-processor").projectDir = file("supports/nativeBridge-processor")
include(":nativeBridge-script")
project(":nativeBridge-script").projectDir = file("supports/nativeBridge-script")



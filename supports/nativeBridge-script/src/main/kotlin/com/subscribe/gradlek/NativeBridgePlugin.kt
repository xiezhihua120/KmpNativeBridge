package com.subscribe.gradlek

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer
import java.io.File

/**
 * Created on 2023/11/14
 * @author：xiezh
 * @function：
 * dependencies {
 *     add("kspCommonMainMetadata", project(":nativeBridge-processor"))
 *     add("kspNativePc", project(":nativeBridge-processor"))
 * }
 * kotlin.sourceSets {
 *     named("nativePcMain") {
 *         dependencies { implementation(project(":nativeBridge-annotation")) }
 *         kotlin.srcDir("build${File.separator}generated${File.separator}ksp${File.separator}nativePc${File.separator}nativePcMain${File.separator}kotlin")
 *     }
 * }
 */


class NativeBridgePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println(">>>>>>>>  " + this::class.qualifiedName)
        val extension = target.extensions.create("NativeBridge", NativeBridgeExtension::class.java)
        target.afterEvaluate {
            configNativeBridge(extension, target)
        }
    }

    private fun configNativeBridge(
        extension: NativeBridgeExtension, target: Project
    ) {
        println("这是插件 ${this::class.qualifiedName}")
        println("namedSourceSets = ${extension.namedSourceSets}")
        println("version = ${extension.info.version}")
        target.tasks.find { it.group == "build" && it.name.startsWith("nativePcBinaries") }?.let {
            it.doLast {
                println("拷贝文件到指定目录")
            }
        }

        // Add the ksp compiler
        val sourceSetName = extension.namedSourceSets
        if (sourceSetName == "commonMain") {
            target.dependencies.add(
                "kspCommonMainMetadata", target.rootProject.findProject(":nativeBridge-processor")
            )
        } else {
            val sourceSetPrefix = removeSuffix(sourceSetName, "Main")
            target.dependencies.add(
                "ksp${capitalizeFirstLetter(sourceSetPrefix)}",
                target.rootProject.findProject(":nativeBridge-processor")
            )
        }
        val containerClass = KotlinSourceSetContainer::class.java
        val allSourceSet = target.extensions.getByType(containerClass).sourceSets

        // Add the runtime dependency.
        listOf(sourceSetName).forEach { sourceName ->
            val namedSourceSet = allSourceSet.getByName(sourceName)
            val namedSourceSetApi =
                target.configurations.getByName(namedSourceSet.apiConfigurationName)
            namedSourceSetApi.dependencies.addAll(buildList {
                add(target.dependencies.create(target.rootProject.findProject(":nativeBridge-annotation")))
            })
            namedSourceSet.kotlin.srcDir(
                "build${File.separator}" + "generated${File.separator}" + "ksp${File.separator}" + "${
                    removeSuffix(sourceName, "Main")
                }${File.separator}" + "${sourceName}${File.separator}" + "kotlin"
            )
        }
    }

    private fun capitalizeFirstLetter(str: String?): String? {
        return if (str.isNullOrEmpty()) {
            str
        } else str[0].uppercaseChar().toString() + str.substring(1)
    }

    private fun removeSuffix(str: String, suffix: String): String {
        return if (str.endsWith(suffix)) {
            str.substring(0, str.length - suffix.length)
        } else str
    }
}

open class NativeBridgeExtension(private val project: Project) {
    var namedSourceSets: String = ""
    var info: InfoExtension = InfoExtension(project)

    fun printVersion() {
        println("MainExtension: $namedSourceSets")
    }

    init {
        info = project.extensions.create("info", InfoExtension::class.java, project)
    }
}
open class InfoExtension(private val project: Project) {
    var version: String = "version_${project.name}_0"
}
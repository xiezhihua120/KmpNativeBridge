package com.subscribe.gradlex

import org.gradle.api.Plugin
import org.gradle.api.Project

class DependenciesPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        System.out.println(">>>>>>>>  " + this.getClass().getName())
        println("这是插件：${this.class.name}")
        def extension = project.extensions.create("NativeBridge", MainExtension)
        project.task("sqlAlightPluginTask") { task ->
            task.doLast {
                println("这是插件${this.class.name}，它创建了一个Task：${task.name}")
                println("title = ${extension.title}")
                println("chapter = ${extension.chapter}")
                println("author = ${extension.subExtension.version}")
            }
        }
    }
}

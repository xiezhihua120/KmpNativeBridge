package com.subscribe.gradlex

import org.gradle.api.Project;

/**
 * Created on 2023/11/14
 *
 * @author：xiezh
 * @function：
 */
class MainExtension {
    String title
    int chapter
    SubExtension sub

    MainExtension(Project project) {
        sub = project.extensions.create('sub', SubExtension.class)
    }
}
class SubExtension {
    String author
}
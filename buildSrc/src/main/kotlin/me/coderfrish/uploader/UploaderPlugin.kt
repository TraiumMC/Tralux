package me.coderfrish.uploader

import com.github.javaparser.quality.NotNull
import org.gradle.api.Plugin
import org.gradle.api.Project

open class UploaderPlugin : Plugin<Project> {
    override fun apply(@NotNull target: Project) {
        target.extensions.create("uploader", UploaderExtension::class.java)
        target.tasks.register("release", ReleaseTasks::class.java)
    }
}

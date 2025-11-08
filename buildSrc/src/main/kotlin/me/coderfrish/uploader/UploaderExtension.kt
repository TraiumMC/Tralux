package me.coderfrish.uploader

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import java.io.File

open class UploaderExtension(project: Project) {
    val repository: Repository = Repository(project)

    val token: Property<String> = project.objects.property(String::class.java)

    val name: Property<String> = project.objects.property(String::class.java)

    val tagName: Property<String> = project.objects.property(String::class.java)

    val targetCommitish: Property<String> = project.objects.property(String::class.java)

    val body: Property<String> = project.objects.property(String::class.java)
        .convention("")

    val prorelease: Property<Boolean> = project.objects.property(Boolean::class.java)
        .convention(false)

    val makeLatest: Property<String> = project.objects.property(String::class.java)
        .convention("true")

    val assets: ListProperty<File> = project.objects.listProperty(File::class.java)
        .convention(emptyList())

    fun repository(action: Action<Repository>) {
        action.execute(repository)
    }

    class Repository(project: Project) {
        val name: Property<String> = project.objects.property(String::class.java)

        val owner: Property<String> = project.objects.property(String::class.java)
    }
}

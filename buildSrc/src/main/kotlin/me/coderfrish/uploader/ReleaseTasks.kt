package me.coderfrish.uploader

import me.coderfrish.uploader.utility.GHReleaseClient
import me.coderfrish.uploader.utility.GHReleaseRequest
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.util.*

open class ReleaseTasks : DefaultTask() {
    private val extension: UploaderExtension = project.extensions.getByType(UploaderExtension::class.java)

    init {
        this.group = "publishing"
    }

    @TaskAction
    fun uploadRelease() {
        Objects.requireNonNull(extension.token, "Token not be null.")
        val client = GHReleaseClient(extension.token.get())
        val repoName: String = extension.repository.name.get()
        val repoOwner: String = extension.repository.owner.get()

        Objects.requireNonNull(repoName, "Repository name not be null.")
        Objects.requireNonNull(repoOwner, "Repository owner not be null.")
        Objects.requireNonNull(extension.tagName, "Tag name not be null.")

        val name = extension.name.getOrElse(extension.tagName.get())

        val request: GHReleaseRequest = GHReleaseRequest(
            extension.tagName.get(),
            name,
            extension.body.get(),
            extension.prorelease.get(),
            extension.targetCommitish.get(),
            extension.makeLatest.get()
        )

        val id: String = client.createRelease(repoOwner, repoName, request)

        for (asset in extension.assets.get()) {
            client.uploadAsset(repoOwner, repoName, id, asset)
        }
    }
}

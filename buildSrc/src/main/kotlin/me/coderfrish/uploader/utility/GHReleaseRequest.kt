package me.coderfrish.uploader.utility

import com.google.gson.annotations.SerializedName

data class GHReleaseRequest(
    @SerializedName("tag_name") private val tagName: String,
    private val name: String,
    private val body: String,
    private val prerelease: Boolean,
    @SerializedName("target_commitish") private val targetCommitish: String,
    @SerializedName("make_latest") private val makeLatest: String
)

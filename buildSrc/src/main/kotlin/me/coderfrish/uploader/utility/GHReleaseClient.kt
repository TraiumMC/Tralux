package me.coderfrish.uploader.utility

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class GHReleaseClient(private val token: String, private val baseUrl: String, private val uploadUrl: String) {
    private val gson: Gson = Gson()
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    constructor(token: String) : this(token, "https://api.github.com", "https://uploads.github.com")

    fun createRelease(owner: String, repo: String, request: GHReleaseRequest): String {
        val url = String.format("%s/repos/%s/%s/releases", baseUrl, owner, repo)
        val json: String = gson.toJson(request)

        val httpRequest = Request.Builder().url(url)
            .post(json.toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "token $token")
            .addHeader("Accept", "application/vnd.github.v3+json")
            .build()

        client.newCall(httpRequest).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to create release: " + response.body.string())
            }
            val responseJson = gson.fromJson(response.body.string(), JsonObject::class.java)
            return responseJson.get("id").asString
        }
    }

    fun uploadAsset(owner: String, repo: String, releaseId: String, file: File, contentType: String = "application/octet-stream") {
        val uploadUrl = "$uploadUrl/repos/$owner/$repo/releases/$releaseId/assets?name=${file.name}"
        val requestBody: RequestBody = file.asRequestBody(contentType.toMediaType())

        val request: Request = Request.Builder()
            .url(uploadUrl)
            .post(requestBody)
            .addHeader("Authorization", "token $token")
            .addHeader("Accept", "application/vnd.github.v3+json")
            .addHeader("Content-Type", contentType)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to upload asset: " + response.body.string())
            }
            println("Asset uploaded successfully: " + file.getName())
        }
    }
}

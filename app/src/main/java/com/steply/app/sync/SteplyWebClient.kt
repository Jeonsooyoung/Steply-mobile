package com.steply.app.sync

import com.steply.app.domain.model.UserProfile
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class SteplyWebClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(4, TimeUnit.SECONDS)
        .writeTimeout(4, TimeUnit.SECONDS)
        .build(),
) {
    fun connectProfile(
        session: SteplyWebSessionPayload,
        profile: UserProfile,
        callback: ResultCallback = ResultCallback.Noop,
    ) {
        val candidates = session.candidateServerUrls.ifEmpty { listOf(session.serverUrl) }.distinct()
        tryConnectProfile(
            session = session,
            profile = profile,
            candidates = candidates,
            index = 0,
            errors = mutableListOf(),
            callback = callback,
        )
    }

    private fun tryConnectProfile(
        session: SteplyWebSessionPayload,
        profile: UserProfile,
        candidates: List<String>,
        index: Int,
        errors: MutableList<String>,
        callback: ResultCallback,
    ) {
        if (index >= candidates.size) {
            callback.onFailure(errors.joinToString(separator = "\n"))
            return
        }

        val baseUrl = SteplyWebSessionPayload.normalizeUrl(candidates[index])
        val activeSession = session.withServerUrl(baseUrl)
        val body = JSONObject()
            .put("sessionId", activeSession.sessionId)
            .put("profile", profile.toJson())

        postJson(
            url = "${activeSession.apiBaseUrl}/api/session/${activeSession.sessionId}/connect",
            json = body,
            onSuccess = { responseBody -> callback.onSuccess(responseBody, activeSession) },
            onFailure = { message ->
                errors += "$baseUrl -> $message"
                tryConnectProfile(
                    session = session,
                    profile = profile,
                    candidates = candidates,
                    index = index + 1,
                    errors = errors,
                    callback = callback,
                )
            },
        )
    }

    private fun postJson(
        url: String,
        json: JSONObject,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "Network request failed")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        onSuccess(it.body?.string().orEmpty())
                    } else {
                        onFailure("HTTP ${it.code}: ${it.body?.string().orEmpty()}")
                    }
                }
            }
        })
    }

    interface ResultCallback {
        fun onSuccess(body: String, connectedSession: SteplyWebSessionPayload)
        fun onFailure(message: String)

        object Noop : ResultCallback {
            override fun onSuccess(body: String, connectedSession: SteplyWebSessionPayload) = Unit
            override fun onFailure(message: String) = Unit
        }
    }

    private fun UserProfile.toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("displayName", displayName)
        .put("name", displayName)
        .put("birthYear", birthYear)
        .put("age", age)
        .put("gender", gender)
        .put("heightCm", heightCm)
        .put("movementNotes", movementNotes)
        .put("safetyNote", safetyNote)
        .put("createdAt", createdAt)
        .put("updatedAt", updatedAt)

    private companion object {
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}

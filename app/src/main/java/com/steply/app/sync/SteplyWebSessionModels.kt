package com.steply.app.sync

import android.net.Uri
import org.json.JSONObject

/**
 * Payload encoded in the QR code shown by Steply-Web.
 *
 * Supported QR JSON:
 * {
 *   "type": "steply-web-session",
 *   "sessionId": "...",
 *   "serverUrl": "http://192.168.0.12:3000",
 *   "serverUrls": ["http://192.168.0.12:3000", "http://192.168.0.12:5173"]
 * }
 */
data class SteplyWebSessionPayload(
    val sessionId: String,
    val serverUrl: String,
    val candidateServerUrls: List<String> = listOf(serverUrl),
) {
    val apiBaseUrl: String = serverUrl.trimEnd('/')
    val webSocketUrl: String = apiBaseUrl
        .replaceFirst("http://", "ws://")
        .replaceFirst("https://", "wss://") + "/ws?sessionId=$sessionId&role=mobile"

    fun withServerUrl(url: String): SteplyWebSessionPayload {
        val normalized = normalizeUrl(url)
        return copy(
            serverUrl = normalized,
            candidateServerUrls = listOf(normalized) + candidateServerUrls.filter { normalizeUrl(it) != normalized },
        )
    }

    companion object {
        fun normalizeUrl(value: String): String = value.trim().trimEnd('/')
    }
}

object SteplyWebSessionLink {
    fun parse(rawValue: String): SteplyWebSessionPayload? {
        val trimmed = rawValue.trim()
        if (trimmed.isBlank()) return null

        parseJson(trimmed)?.let { return it }
        parseUri(trimmed)?.let { return it }
        return null
    }

    private fun parseJson(rawValue: String): SteplyWebSessionPayload? {
        return runCatching {
            val json = JSONObject(rawValue)
            val type = json.optString("type")
            if (type != "steply-web-session") return@runCatching null
            val sessionId = json.optString("sessionId").trim()
            val serverUrl = SteplyWebSessionPayload.normalizeUrl(json.optString("serverUrl"))
            val candidates = mutableListOf<String>()
            val serverUrls = json.optJSONArray("serverUrls")
            if (serverUrls != null) {
                for (i in 0 until serverUrls.length()) {
                    val value = SteplyWebSessionPayload.normalizeUrl(serverUrls.optString(i))
                    if (value.isNotBlank()) candidates += value
                }
            }
            if (serverUrl.isNotBlank()) candidates += serverUrl
            val uniqueCandidates = candidates.distinct()
            if (sessionId.isBlank() || serverUrl.isBlank()) {
                null
            } else {
                SteplyWebSessionPayload(
                    sessionId = sessionId,
                    serverUrl = serverUrl,
                    candidateServerUrls = uniqueCandidates.ifEmpty { listOf(serverUrl) },
                )
            }
        }.getOrNull()
    }

    private fun parseUri(rawValue: String): SteplyWebSessionPayload? {
        return runCatching {
            val uri = Uri.parse(rawValue)
            val scheme = uri.scheme?.lowercase()
            if (scheme != "steply" || uri.host != "web-session") return@runCatching null
            val sessionId = uri.getQueryParameter("sessionId")?.trim().orEmpty()
            val serverUrl = uri.getQueryParameter("serverUrl")?.let { SteplyWebSessionPayload.normalizeUrl(it) }.orEmpty()
            if (sessionId.isBlank() || serverUrl.isBlank()) null else SteplyWebSessionPayload(sessionId, serverUrl)
        }.getOrNull()
    }
}

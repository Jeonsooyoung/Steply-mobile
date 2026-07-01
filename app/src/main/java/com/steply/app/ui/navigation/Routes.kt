package com.steply.app.ui.navigation

import android.net.Uri

object Routes {
    const val ProfileList = "profiles"
    const val AddEditProfile = "profiles/edit?profileId={profileId}"
    const val RemoteConnect = "remote_connect"
    const val History = "history"
    const val RemoteCamera = "remote_camera/{sessionId}/{serverUrl}"

    fun addProfile(): String = "profiles/edit"
    fun editProfile(profileId: String): String = "profiles/edit?profileId=$profileId"

    fun remoteCamera(sessionId: String, serverUrl: String): String {
        return "remote_camera/${Uri.encode(sessionId)}/${Uri.encode(serverUrl)}"
    }
}

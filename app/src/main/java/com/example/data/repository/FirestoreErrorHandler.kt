package com.example.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write")
}

fun handleFirestoreError(
    error: Exception?,
    operationType: OperationType,
    path: String?
) {
    val currentUser = try {
        FirebaseAuth.getInstance().currentUser
    } catch (_: Exception) {
        null
    }

    val providerInfoArray = JSONArray()
    currentUser?.providerData?.forEach { profile ->
        val providerObj = JSONObject().apply {
            put("providerId", profile.providerId)
            put("displayName", profile.displayName)
            put("email", profile.email)
            put("photoUrl", profile.photoUrl?.toString())
        }
        providerInfoArray.put(providerObj)
    }

    val authInfo = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("displayName", currentUser?.displayName)
        put("emailVerified", currentUser?.isEmailVerified)
        put("isAnonymous", currentUser?.isAnonymous)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", providerInfoArray)
    }

    val errInfo = JSONObject().apply {
        put("error", error?.message ?: "Unknown Firestore error")
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfo)
    }

    Log.e("FirestoreError", errInfo.toString(), error)
}

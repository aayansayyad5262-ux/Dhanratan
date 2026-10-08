package com.example.data.repository

import android.content.Context
import com.example.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

data class FirestoreUserProfile(
    val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val username: String = "",
    val phone: String = "",
    val balance: Long = 0L,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

fun DocumentSnapshot.toFirestoreUserProfile(): FirestoreUserProfile? {
    if (!exists()) return null
    return FirestoreUserProfile(
        id = id,
        userId = getString("userId") ?: "",
        fullName = getString("fullName") ?: "",
        username = getString("username") ?: "",
        phone = getString("phone") ?: "",
        balance = getLong("balance") ?: 0L,
        createdAt = getTimestamp("createdAt"),
        updatedAt = getTimestamp("updatedAt")
    )
}

class FirestoreUserRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    constructor(context: Context) : this(
        db = FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        auth = FirebaseAuth.getInstance()
    )

    suspend fun createOrSyncUserProfile(
        fullName: String,
        username: String,
        phone: String,
        balance: Long = 0L
    ): FirestoreUserProfile {
        val uid = auth.currentUser?.uid ?: ""
        val path = "users/$uid"
        val docRef = db.collection("users").document(uid)

        try {
            val existingSnap = docRef.get().await()
            if (existingSnap.exists()) {
                val existingCreatedAt = existingSnap.getTimestamp("createdAt")
                val existingUserId = existingSnap.getString("userId") ?: uid
                val updateData = mapOf(
                    "userId" to existingUserId,
                    "fullName" to fullName.take(100).ifBlank { "DhanRatan Player" },
                    "username" to username.take(120).ifBlank { "dhanratan_user" },
                    "phone" to phone.take(15),
                    "balance" to balance.coerceIn(0L, 100000000L),
                    "createdAt" to (existingCreatedAt ?: FieldValue.serverTimestamp()),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updateData).await()
            } else {
                val createData = mapOf(
                    "userId" to uid,
                    "fullName" to fullName.take(100).ifBlank { "DhanRatan Player" },
                    "username" to username.take(120).ifBlank { "dhanratan_user" },
                    "phone" to phone.take(15),
                    "balance" to balance.coerceIn(0L, 100000000L),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(createData).await()
            }
            val finalSnap = docRef.get().await()
            return finalSnap.toFirestoreUserProfile() ?: FirestoreUserProfile(
                id = uid,
                userId = uid,
                fullName = fullName,
                username = username,
                phone = phone,
                balance = balance
            )
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    fun observeOwnProfile(): Flow<FirestoreUserProfile?> = flow {
        val uid = auth.currentUser?.uid ?: ""
        val path = "users/$uid"
        emitAll(
            db.collection("users")
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { querySnapshot ->
                    querySnapshot.documents.firstOrNull()?.toFirestoreUserProfile()
                }
                .catch { e ->
                    val firestoreEx = generateSequence<Throwable>(e) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    val exToReport = firestoreEx ?: (e as? Exception ?: Exception(e))
                    handleFirestoreError(exToReport, OperationType.LIST, path)
                    throw firestoreEx ?: e
                }
        )
    }

    suspend fun getProfileByUserId(targetUserId: String): FirestoreUserProfile? {
        val path = "users/$targetUserId"
        return try {
            val snap = db.collection("users").document(targetUserId).get().await()
            snap.toFirestoreUserProfile()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            throw e
        }
    }

    suspend fun deleteOwnProfile() {
        val uid = auth.currentUser?.uid ?: ""
        val path = "users/$uid"
        try {
            db.collection("users").document(uid).delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            throw e
        }
    }
}

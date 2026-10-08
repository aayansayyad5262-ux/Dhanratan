package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

class FirestoreUserRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createOrSyncUserProfile_authenticatedOwner_succeedsAndReadsBack() = runBlocking {
        val uid = signInTestUser("player1@dhanratan.com")
        val repository = FirestoreUserRepository(firestore, auth)

        val created = withTimeout(FLOW_TIMEOUT_MS) {
            repository.createOrSyncUserProfile(
                fullName = "Ratan Player",
                username = "player1@dhanratan.com",
                phone = "9876543210",
                balance = 250L
            )
        }

        assertEquals(uid, created.id)
        assertEquals(uid, created.userId)
        assertEquals("Ratan Player", created.fullName)
        assertEquals("player1@dhanratan.com", created.username)
        assertEquals("9876543210", created.phone)
        assertEquals(250L, created.balance)
        assertNotNull(created.createdAt)
        assertNotNull(created.updatedAt)

        val observed = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeOwnProfile().first { it != null }
        }
        assertNotNull(observed)
        assertEquals("Ratan Player", observed?.fullName)
    }

    @Test
    fun createOrSyncUserProfile_updateExistingOwner_preservesCreatedAtAndUpdatesFields() = runBlocking {
        val uid = signInTestUser("player2@dhanratan.com")
        val repository = FirestoreUserRepository(firestore, auth)

        val initial = withTimeout(FLOW_TIMEOUT_MS) {
            repository.createOrSyncUserProfile(
                fullName = "Initial Name",
                username = "player2@dhanratan.com",
                phone = "9876500001",
                balance = 100L
            )
        }

        val updated = withTimeout(FLOW_TIMEOUT_MS) {
            repository.createOrSyncUserProfile(
                fullName = "Updated Name",
                username = "player2@dhanratan.com",
                phone = "9876500001",
                balance = 750L
            )
        }

        assertEquals(uid, updated.userId)
        assertEquals("Updated Name", updated.fullName)
        assertEquals(750L, updated.balance)
        assertEquals(initial.createdAt, updated.createdAt)
    }

    @Test
    fun deleteOwnProfile_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser("player3@dhanratan.com")
        val repository = FirestoreUserRepository(firestore, auth)

        withTimeout(FLOW_TIMEOUT_MS) {
            repository.createOrSyncUserProfile(
                fullName = "Delete Me",
                username = "player3@dhanratan.com",
                phone = "9876500003",
                balance = 0L
            )
            repository.deleteOwnProfile()
        }

        val afterDelete = withTimeout(FLOW_TIMEOUT_MS) {
            repository.getProfileByUserId(uid)
        }
        assertNull(afterDelete)
    }

    @Test
    fun observeOwnProfile_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = FirestoreUserRepository(firestore, auth)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeOwnProfile().first()
            }
            fail("Expected PERMISSION_DENIED for unauthenticated user")
        } catch (e: Throwable) {
            val firestoreEx = generateSequence<Throwable>(e) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            assertNotNull("Expected FirebaseFirestoreException in cause chain, got $e", firestoreEx)
            assertEquals(
                FirebaseFirestoreException.Code.PERMISSION_DENIED,
                firestoreEx?.code
            )
        }
    }

    @Test
    fun getProfileByUserId_differentUser_failsWithPermissionDenied() = runBlocking {
        val userAUid = signInTestUser("userA@dhanratan.com")
        val repository = FirestoreUserRepository(firestore, auth)
        withTimeout(FLOW_TIMEOUT_MS) {
            repository.createOrSyncUserProfile(
                fullName = "User A",
                username = "userA@dhanratan.com",
                phone = "9876500011",
                balance = 500L
            )
        }

        signInTestUser("userB@dhanratan.com")
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.getProfileByUserId(userAUid)
            }
            fail("Expected PERMISSION_DENIED when User B reads User A's profile")
        } catch (e: FirebaseFirestoreException) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }
    }
}

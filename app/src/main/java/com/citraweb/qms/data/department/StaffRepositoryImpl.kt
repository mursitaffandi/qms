package com.citraweb.qms.data.department

import com.citraweb.qms.MyApp
import com.citraweb.qms.data.user.User
import com.citraweb.qms.utils.*
import com.citraweb.qms.utils.SharePrefManager.Companion.ID_DEPARTMENT
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class StaffRepositoryImpl : StaffAction {
    private val prefManager = SharePrefManager(MyApp.instance)
    private val db = Firebase.firestore
    private val departmentStore = db.collection(DEPARTMENT_COLLECTION_NAME)
    private val userStore = db.collection(USER_COLLECTION_NAME)

    override fun getQueryQueue(): FirestoreRecyclerOptions<User?> {
        return FirestoreRecyclerOptions.Builder<User>()
                .setQuery(
                        userStore.whereEqualTo(
                                USER_TICKETPARENT,
                                prefManager.getFromPreference(ID_DEPARTMENT)),
                        User::class.java).build()
    }

    override suspend fun detailDepartment(): Flow<Result<Department?>> = callbackFlow {
        val subscription = departmentStore.document(prefManager.getFromPreference(ID_DEPARTMENT)).addSnapshotListener { value, error ->
            if (value!!.exists()){
                val detailDepartment = value.toObject(Department::class.java)
                offer(Result.Success(detailDepartment))
            } else {
                offer(Result.Error(error!!))
            }
        }

        awaitClose { subscription.remove() }

    }

    override suspend fun power(action: StateDepartment): Result<Void?> {
        return try {
            departmentStore.document(prefManager.getFromPreference(ID_DEPARTMENT)).update(DEPARTMENT_STATUS, action.name).await()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

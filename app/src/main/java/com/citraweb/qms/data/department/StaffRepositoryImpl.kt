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

class StaffRepositoryImpl : StaffAction {
    private val prefManager = SharePrefManager(MyApp.instance)
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val departmentStore = db.collection(DEPARTMENT_COLLECTION_NAME)
    private val userStore = db.collection(USER_COLLECTION_NAME)

    /*companion object {
        private val firebaseAuth = FirebaseAuth.getInstance()
        private val db = Firebase.firestore
        private val departmentStore = db.collection(DEPARTMENT_COLLECTION_NAME)
        private val userStore = db.collection(USER_COLLECTION_NAME)

        suspend fun getDepartmentId(): Result<String> {
            return try {
                when (val docId = departmentStore.whereEqualTo(DEPARTMENT_STAFFID, firebaseAuth.uid).get().await()) {
                    is Result.Success -> Result.Success(docId.data.documents[0].id)
                    is Result.Canceled -> Result.Canceled(docId.exception)
                    is Result.Error -> Result.Error(docId.exception)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }*/
    override fun getQueryQueue(): FirestoreRecyclerOptions<User?> {
        return FirestoreRecyclerOptions.Builder<User>()
                .setQuery(
                        userStore.whereEqualTo(
                                USER_TICKETPARENT,
                                prefManager.getFromPreference(ID_DEPARTMENT)),
                        User::class.java).build()
    }

    override suspend fun detailDepartment(): Task<Department?>? {
        return  departmentStore.document(prefManager.getFromPreference(ID_DEPARTMENT)).addSnapshotListener { value, error ->

        }
        return  departmentStore.document(prefManager.getFromPreference(ID_DEPARTMENT)).get().continueWith { p0 -> p0.result?.toObject(Department::class.java) }
    }

    override suspend fun power(action: StateDepartment): Result<Void?> {
        return try {
            departmentStore.document(prefManager.getFromPreference(ID_DEPARTMENT)).update(DEPARTMENT_STATUS, action.name).await()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

package com.citraweb.qms.data.department

import com.citraweb.qms.data.user.User
import com.citraweb.qms.utils.*
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StaffRepositoryImpl(private val staffId: String) : StaffAction {
    private var id: String? = null

    private val docRef = departmentStore.document(staffId)

    companion object {
        private val db = Firebase.firestore
        private val departmentStore = db.collection(DEPARTMENT_COLLECTION_NAME)
        private val userStore = db.collection(DEPARTMENT_COLLECTION_NAME)

        suspend fun getDepartmentId(userId : String): Result<String> {
            return try {
                when (val docId = departmentStore.whereEqualTo(DEPARTMENT_STAFFID, userId).get().await()) {
                    is Result.Success -> Result.Success(docId.data.documents[0].id)
                    is Result.Canceled -> Result.Canceled(docId.exception)
                    is Result.Error -> Result.Error(docId.exception)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
    override fun getQueryQueue(): FirestoreRecyclerOptions<User?> {
        return FirestoreRecyclerOptions.Builder<User>()
                .setQuery(userStore.whereEqualTo(USER_TICKETPARENT, staffId), User::class.java)
                .build()
    }

    override suspend fun detailDepartment(): Task<Department?> {
        return docRef.get().continueWith { p0 -> p0.result?.toObject(Department::class.java) }
    }

    override suspend fun power(action: StateDepartment): Result<Void?> {
        return try {
            docRef.update(DEPARTMENT_STATUS, action.name).await()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

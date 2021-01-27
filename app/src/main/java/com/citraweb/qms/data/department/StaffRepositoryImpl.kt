package com.citraweb.qms.data.department

import com.citraweb.qms.data.user.User
import com.citraweb.qms.utils.*
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class StaffRepositoryImpl(private val staffId: String) : StaffAction {
    private val db = Firebase.firestore
    private val departmentStore = db.collection(DEPARTMENT_COLLECTION_NAME)
    private val userStore = db.collection(DEPARTMENT_COLLECTION_NAME)
    private var docRef: DocumentReference? = null

    init {
//        TODO : /E java.lang.IllegalStateException: Task is not yet complete
        docRef = departmentStore.whereEqualTo(DEPARTMENT_STAFFID, staffId).get().result?.documents?.get(0)?.reference
    }

    override fun getQueryQueue(): FirestoreRecyclerOptions<User?> {
        return FirestoreRecyclerOptions.Builder<User>()
                .setQuery(userStore.whereEqualTo(USER_TICKETPARENT, staffId), User::class.java)
                .build()
    }

    override suspend fun detailDepartment(): Result<Department?> {

        try {
            return when (val detail = docRef!!.get().await()) {
                is Result.Success -> {
                    Result.Success(detail.data.toObject(Department::class.java))
                }
                is Result.Error -> {
                    Timber.e("${detail.exception}")
                    Result.Error(detail.exception)
                }
                is Result.Canceled -> {
                    Timber.e("${detail.exception}")
                    Result.Canceled(detail.exception)
                }
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    override suspend fun power(action: StateDepartment): Result<Void?> {
        return try {
            docRef!!.update(DEPARTMENT_STATUS, action.name).await()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

package com.citraweb.qms.data.department

import com.citraweb.qms.data.user.User
import com.citraweb.qms.utils.DEPARTMENT_COLLECTION_NAME
import com.citraweb.qms.utils.Result
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StaffRepositoryImpl(private val staffId : String) : StaffAction {
    private val db = Firebase.firestore
    private val departmentStore = db.collection(DEPARTMENT_COLLECTION_NAME)
    private val userStore = db.collection(DEPARTMENT_COLLECTION_NAME)
    private lateinit var docRef : DocumentReference
init {
    docRef = departmentStore.whereEqualTo("staffId", staffId).get().result.documents[0].reference
}
    override fun getQuery(departmentId : String) : FirestoreRecyclerOptions<Department> {
        return FirestoreRecyclerOptions.Builder<Department>()
                .setQuery(userStore.whereEqualTo("ticketParent", departmentId), Department::class.java)
                .build()
    }

    override suspend fun detailDepartment(): Result<Department?> {
        departmentStore.whereEqualTo("staffId", staffId).orderBy("").startAt("").endAt()
    }

    override suspend fun open(): Result<Void?> {
        departmentStore.whereEqualTo("staffId", staffId).get().result?.toObjects(Department::class.java)
        departmentStore.document()
    }

    override suspend fun close(): Result<Void?> {
        TODO("Not yet implemented")
    }

}

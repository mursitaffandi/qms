package com.citraweb.qms.utils

import android.content.Context
import android.content.SharedPreferences

class SharePrefManager(c: Context) {
    companion object {
        const val ID_DEPARTMENT = "department"
        const val ID_USER = "user"
    }

    private var pref: SharedPreferences = c.getSharedPreferences("qms_pref", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = pref.edit()

    fun clearAll() {
        editor.apply {
            clear()
            apply()
        }
    }

    fun getFromPreference(key: String): String {
        return pref.getString(key, "")!!
    }

    fun setDepartmentId(id: String) {
        editor.apply {
            putString(ID_DEPARTMENT, id)
            apply()
        }
    }

    fun setUserId(id: String) {
        editor.apply {
            putString(ID_USER, id)
            apply()
        }
    }

}

package com.citraweb.qms.repository

import com.citraweb.qms.data.model.User

/**
 * Authentication result : success (user details) or error message.
 */
data class UserResult(
        val success: User? = null,
        val message: Int? = null
)
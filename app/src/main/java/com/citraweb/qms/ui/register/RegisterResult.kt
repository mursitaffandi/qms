package com.citraweb.qms.ui.register

import com.citraweb.qms.data.model.User

/**
 * Authentication result : success (user details) or error message.
 */
data class RegisterResult(
        val success: User? = null,
        val message: Int? = null
)
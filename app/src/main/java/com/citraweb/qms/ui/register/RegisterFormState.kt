package com.citraweb.qms.ui.register

/**
 * Data validation state of the login form.
 */
data class RegisterFormState(var usernameError: Int? = null,
                             var emailError: Int? = null,
                             var passwordError: Int? = null,
                             var confirmPasswordError: Int? = null,
                             var isDataValid: Boolean = false,
                             )
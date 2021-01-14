
package com.citraweb.qms.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast

// A placeholder username validation check
fun isUserNameValid(username: String): Boolean {
    return username.trim().length > 2
}

// A placeholder mail validation check
fun isEmailValid(username: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(username).matches()

}

// A placeholder password validation check
fun isPasswordValid(password: String): Boolean {
    return password.length > 5
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun <T> Context.openActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    val intent = Intent(this, it)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
}
/*
openActivity(MyActivity::class.java) {
  putString("string.key", "string.value")
  putInt("string.key", 43)
  ...
}
* */

inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
}

inline fun <reified T : Activity> Context.startActivityFinish(block: Intent.() -> Unit = {}) {
    this.
    startActivity(Intent(this, T::class.java).apply(
            block
    ))
}
/*
startActivity<MainActivity>()

startActivity<MainActivity>{
   putExtra("param 1", "Simple")
}
* */

/**
* Don't forget to show manually
* */
fun Context.toas(msg : String): Toast {
    return Toast.makeText(this, msg, Toast.LENGTH_LONG)
}
package com.citraweb.qms.ui.login

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.citraweb.qms.R
import com.citraweb.qms.databinding.ActivityLoginBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.ui.dashboard.DashboardActivity
import com.citraweb.qms.ui.register.RegisterActivity
import com.citraweb.qms.utils.afterTextChanged
import com.citraweb.qms.utils.startActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, MyViewModelFactory())
            .get(LoginViewModel::class.java)

        viewModel.loginFormState.observe(this@LoginActivity, Observer {
            val registerState = it ?: return@Observer

            // disable register button unless both username / password is valid
            binding.btnRegisterLogin.isEnabled = registerState.isDataValid

            if (registerState.emailError != null) {
                binding.tilLoginEmail.error = getString(registerState.emailError)
            }

            if (registerState.passwordError != null) {
                binding.tilLoginPassword.error = getString(registerState.passwordError)
            }
        })

        viewModel.loginResult.observe(this@LoginActivity, Observer {
            val registerResult = it ?: return@Observer

            binding.spinnerLogin.visibility = View.GONE
            if (registerResult.error != null) {
                showLoginFailed(registerResult.error)
            }
            if (registerResult.success != null) {
                updateUiWithUser(registerResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy register activity once successful
            finish()
        })

        binding.tietLoginEmail.afterTextChanged {
            viewModel.loginDataChanged(
                binding.tietLoginEmail.text.toString(),
                binding.tietLoginPassword.text.toString()
            )
        }

        binding.tietLoginPassword.apply {
            afterTextChanged {
                viewModel.loginDataChanged(
                    binding.tietLoginEmail.text.toString(),
                    binding.tietLoginPassword.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        viewModel.login(
                            binding.tietLoginEmail.text.toString(),
                            binding.tietLoginPassword.text.toString()
                        )
                }
                false
            }

            binding.btnRegisterLogin.setOnClickListener {
                binding.spinnerLogin.visibility = View.VISIBLE
                viewModel.login(binding.tietLoginEmail.text.toString(), binding.tietLoginPassword.text.toString())
            }
        }

        binding.tvRegisternow.setOnClickListener {
            startActivity<RegisterActivity>()
        }

    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
        startActivity<DashboardActivity>()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}
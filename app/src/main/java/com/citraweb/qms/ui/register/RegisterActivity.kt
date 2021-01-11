package com.citraweb.qms.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.citraweb.qms.databinding.ActivityRegisterBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.utils.afterTextChanged

class RegisterActivity : AppCompatActivity() {
    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, MyViewModelFactory())
            .get(RegisterViewModel::class.java)

        viewModel.registerFormState.observe(this@RegisterActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            binding.btnRegisterLogin.isEnabled = loginState.isDataValid

            if (loginState.emailError != null) {
                binding.tietRegisterEmail.error = getString(loginState.emailError)
            }
            if (loginState.passwordError != null) {
                binding.tietRegisterPassword.error = getString(loginState.passwordError)
            }
        })

        /*viewModel.loginResult.observe(this@RegisterActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })*/

        binding.tietRegisterEmail.afterTextChanged {
            viewModel.registerDataChanged(
                binding.tietRegisterEmail.text.toString(),
                binding.tietRegisterPassword.text.toString()
            )
        }

        binding.tietRegisterPassword.apply {
            afterTextChanged {
                viewModel.registerDataChanged(
                    binding.tietRegisterEmail.text.toString(),
                    binding.tietRegisterPassword.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        viewModel.registerUserFromAuthWithEmailAndPassword(
                                binding.tietRegisterName.text.toString(),
                            binding.tietRegisterEmail.text.toString(),
                            binding.tietRegisterPassword.text.toString()
                        )
                }
                false
            }

            binding.btnRegisterLogin.setOnClickListener {
                binding.spinnerRegister.visibility = View.VISIBLE
                viewModel.registerUserFromAuthWithEmailAndPassword(binding.tietRegisterName.text.toString(),binding.tietRegisterEmail.text.toString(), binding.tietRegisterPassword.text.toString())
            }
        }

    }
}
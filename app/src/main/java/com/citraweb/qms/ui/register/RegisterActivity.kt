package com.citraweb.qms.ui.register

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.citraweb.qms.databinding.ActivityRegisterBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.ui.dashboard.DashboardActivity
import com.citraweb.qms.utils.afterTextChanged
import com.citraweb.qms.utils.startActivity
import com.citraweb.qms.utils.toas

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

            binding.tilRegisterName.error = null
            binding.tilRegisterEmail.error = null
            binding.tilRegisterPassword.error = null
            binding.tilRegisterConfirmPassword.error = null

            loginState.usernameError?.let { it1 ->
                binding.tilRegisterName.error = getString(it1)
            }
            loginState.emailError?.let { it1 ->
                binding.tilRegisterEmail.error = getString(it1)
            }
            loginState.passwordError?.let { it1 ->
                binding.tilRegisterPassword.error = getString(it1)
            }

            loginState.confirmPasswordError?.let { it1 ->
                binding.tilRegisterConfirmPassword.error = getString(it1)
            }
        })

        viewModel.currentUserLD.observe(this@RegisterActivity, Observer {
            val result = it ?: return@Observer

            binding.spinnerRegister.visibility = View.GONE
            if (result.message != null) {
                toas(getString(result.message)).show()
            }
            if (result.success != null) {
                startActivity<DashboardActivity>()
                finish()
            }
        })
        binding.tietRegisterName.afterTextChanged {
            viewModel.registerDataChanged(
                    binding.tietRegisterName.text.toString(),
                    binding.tietRegisterEmail.text.toString(),
                    binding.tietRegisterPassword.text.toString(),
                    binding.tietRegisterConfirmPassword.text.toString()
            )
        }
        binding.tietRegisterEmail.afterTextChanged {
            viewModel.registerDataChanged(
                    binding.tietRegisterName.text.toString(),
                    binding.tietRegisterEmail.text.toString(),
                    binding.tietRegisterPassword.text.toString(),
                    binding.tietRegisterConfirmPassword.text.toString()
            )
        }

        binding.tietRegisterPassword.afterTextChanged {
            viewModel.registerDataChanged(
                    binding.tietRegisterName.text.toString(),
                    binding.tietRegisterEmail.text.toString(),
                    binding.tietRegisterPassword.text.toString(),
                    binding.tietRegisterConfirmPassword.text.toString()
            )
        }

        binding.tietRegisterConfirmPassword.apply {
            afterTextChanged {
                viewModel.registerDataChanged(
                        binding.tietRegisterName.text.toString(),
                        binding.tietRegisterEmail.text.toString(),
                        binding.tietRegisterPassword.text.toString(),
                        binding.tietRegisterConfirmPassword.text.toString()
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
                viewModel.registerUserFromAuthWithEmailAndPassword(
                        binding.tietRegisterName.text.toString(),
                        binding.tietRegisterEmail.text.toString(),
                        binding.tietRegisterPassword.text.toString()
                )
            }
        }

        viewModel.toast.observe(this, { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                viewModel.onToastShown()
            }
        })

        viewModel.spinner.observe(this, { value ->
            value.let { show ->
                binding.spinnerRegister.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }
}
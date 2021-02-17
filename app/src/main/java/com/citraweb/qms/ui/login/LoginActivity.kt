package com.citraweb.qms.ui.login

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.citraweb.qms.databinding.ActivityLoginBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.ui.dashboard.DashboardActivity
import com.citraweb.qms.ui.forgetpassword.ForgetPasswordActivity
import com.citraweb.qms.ui.register.RegisterActivity
import com.citraweb.qms.utils.afterTextChanged
import com.citraweb.qms.utils.startActivity
import com.citraweb.qms.utils.toas

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
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            binding.btnLogin.isEnabled = loginState.isDataValid

            binding.tilLoginEmail.error = null
            binding.tilLoginPassword.error = null

            loginState.emailError?.let { it1 ->
                binding.tilLoginEmail.error = getString(it1)
            }
            loginState.passwordError?.let { it1 ->
                binding.tilLoginPassword.error = getString(it1)
            }
        })

        viewModel.currentFireUserLD.observe(this@LoginActivity, Observer {
            val result = it ?: return@Observer

            binding.spinnerLogin.visibility = View.GONE
            result.message?.let { it1 -> toas(getString(it1)).show() }
            if (result.success != null) {
                startActivity<DashboardActivity>()
                finish()
            }
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
                        doLogin()
                }
                false
            }

            binding.btnLogin.setOnClickListener {
                binding.spinnerLogin.visibility = View.VISIBLE
                doLogin()
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
                binding.spinnerLogin.visibility = if (show) View.VISIBLE else View.GONE
            }
        })

        binding.tvForgetpassword.setOnClickListener {
            startActivity<ForgetPasswordActivity>()
        }

        binding.tvRegisternow.setOnClickListener {
            startActivity<RegisterActivity>()
        }

    }

    fun doLogin(){
        viewModel.loginUserFromAuthWithEmailAndPassword(
                binding.tietLoginEmail.text.toString(),
                binding.tietLoginPassword.text.toString()
        )
    }
}
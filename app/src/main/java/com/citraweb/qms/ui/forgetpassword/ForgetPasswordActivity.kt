package com.citraweb.qms.ui.forgetpassword

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.citraweb.qms.databinding.ActivityForegtpasswordBinding
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.ui.dashboard.DashboardActivity
import com.citraweb.qms.utils.afterTextChanged
import com.citraweb.qms.utils.startActivity
import com.citraweb.qms.utils.toas

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var viewModel: ForgetPasswordViewModel
    private lateinit var binding: ActivityForegtpasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForegtpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, MyViewModelFactory())
            .get(ForgetPasswordViewModel::class.java)

        viewModel.registerFormState.observe(this@ForgetPasswordActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            binding.btnLogin.isEnabled = loginState.isDataValid

            binding.tilLoginEmail.error = null

            loginState.emailError?.let { it1 ->
                binding.tilLoginEmail.error = getString(it1)
            }

        })

        viewModel.currentUserLD.observe(this@ForgetPasswordActivity, Observer {
            val result = it ?: return@Observer

            binding.spinnerLogin.visibility = View.GONE
            if (result.message != null) {
                toas(getString(result.message)).show()
            }
            if (result.success != null) {
                startActivity<DashboardActivity>()
                finish()
            }
        })

        binding.tietLoginEmail.afterTextChanged {
            viewModel.loginDataChanged(
                binding.tietLoginEmail.text.toString()
            )
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
    }
}
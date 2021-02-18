package com.citraweb.qms.ui.dashboard

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.citraweb.qms.R
import com.citraweb.qms.data.Data
import com.citraweb.qms.service.MyMessagingService.Companion.isDashboardForeGround
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.ui.login.LoginActivity
import com.citraweb.qms.utils.ACTION_BROADCAST_CALLED_QUEUE
import com.citraweb.qms.utils.KEY_EXTRA_BROADCAST
import com.citraweb.qms.utils.startActivity
import com.google.android.material.navigation.NavigationView
import timber.log.Timber


class DashboardActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var viewModel: DashboardViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val data = it.getParcelableExtra<Data>(KEY_EXTRA_BROADCAST)
                Timber.tag("terima").d(data.departmentName)
                navController.navigate(R.id.nav_gallery)
            }
        }
    }

    private lateinit var localBroadcastManager: LocalBroadcastManager

    private lateinit var filter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProvider(this, MyViewModelFactory())
            .get(DashboardViewModel::class.java)

        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        filter = IntentFilter()
        filter.addAction(ACTION_BROADCAST_CALLED_QUEUE)
        localBroadcastManager.registerReceiver(mReceiver, filter)
        viewModel.start()

        viewModel.currentUserLD.observe(this@DashboardActivity, Observer { resultData ->
            val result = resultData ?: return@Observer

            if (result.success == null) {
                startActivity<LoginActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                finish()
                return@Observer
            } else {
                drawerLayout = findViewById(R.id.drawer_layout)
                val navView: NavigationView = findViewById(R.id.nav_view)
                navController = findNavController(R.id.nav_host_fragment)
                // Passing each menu ID as a set of Ids because each
                // menu should be considered as top level destinations.
                appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.nav_gallery, R.id.nav_home, R.id.nav_slideshow
                    ), drawerLayout
                )

                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)

                intent?.getParcelableExtra<Data>("")?.let {
                    navController.navigate(R.id.nav_gallery)
                }
                val headerView = navView.getHeaderView(0)
                val navUsername = headerView.findViewById(R.id.header_tv_username) as TextView
                val navUserEmail = headerView.findViewById(R.id.header_tv_email) as TextView
                navUsername.text = result.success.name
                navUserEmail.text = result.success.email
            }
        })
    }

    override fun onResume() {
        super.onResume()
        isDashboardForeGround = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun logout(v: MenuItem): Boolean {
        viewModel.revoke()
        drawerLayout.closeDrawers()
        return true
    }

    override fun onPause() {
        isDashboardForeGround = false
        super.onPause()
    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(mReceiver)
        super.onDestroy()
    }
}
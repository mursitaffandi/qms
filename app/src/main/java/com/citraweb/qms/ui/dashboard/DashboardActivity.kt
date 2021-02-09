package com.citraweb.qms.ui.dashboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.citraweb.qms.R
import com.citraweb.qms.service.MyMessagingService.Companion.isDashboardForeGround
import com.citraweb.qms.ui.MyViewModelFactory
import com.citraweb.qms.ui.login.LoginActivity
import com.citraweb.qms.utils.BROADCAST_CALLED_QUEUE
import com.citraweb.qms.utils.startActivity
import com.google.android.material.navigation.NavigationView


class DashboardActivity : AppCompatActivity() {
    private lateinit var viewModel: DashboardViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            appBarConfiguration.topLevelDestinations
        }
    }

    private val filter = IntentFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProvider(this, MyViewModelFactory())
            .get(DashboardViewModel::class.java)

        filter.addAction(BROADCAST_CALLED_QUEUE)
        registerReceiver(mReceiver, filter)
        viewModel.start()

        viewModel.currentUserLD.observe(this@DashboardActivity, Observer { resultData ->
            val result = resultData ?: return@Observer

            if (result.success == null) {
                startActivity<LoginActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                finish()
                return@Observer
            } else
            {
                drawerLayout = findViewById(R.id.drawer_layout)
                val navView: NavigationView = findViewById(R.id.nav_view)
                val navController = findNavController(R.id.nav_host_fragment)
                // Passing each menu ID as a set of Ids because each
                // menu should be considered as top level destinations.
                appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
                    ), drawerLayout
                )
                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)
            }
        })

        /* val fab: FloatingActionButton = findViewById(R.id.fab)
         fab.setOnClickListener { view ->
             Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                     .setAction("Action", null).show()
         }*/

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

    fun logout(v: MenuItem) : Boolean {
        viewModel.revoke()
        drawerLayout.closeDrawers()
        return true
    }

    override fun onPause() {
        isDashboardForeGround = false
        super.onPause()
    }

    override fun onStop() {
        isDashboardForeGround = false
        super.onStop()
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }
}
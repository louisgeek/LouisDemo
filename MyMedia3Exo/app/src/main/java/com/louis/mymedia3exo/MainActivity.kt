package com.louis.mymedia3exo

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragment = PlayerFragmentPort.newInstance("", "")
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment).commitAllowingStateLoss()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (isLandscape) {
            val fragment = PlayerFragmentLand.newInstance("", "")
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment).commitAllowingStateLoss()
        } else {
            val fragment = PlayerFragmentPort.newInstance("", "")
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment).commitAllowingStateLoss()
        }
    }
}
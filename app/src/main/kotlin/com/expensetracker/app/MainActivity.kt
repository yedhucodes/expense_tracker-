package com.expensetracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.data.local.UserPreferencesManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashFile = java.io.File(filesDir, "last_crash.txt")
        if (crashFile.exists()) {
            val crashContent = crashFile.readText()
            if (crashContent.isNotBlank()) {
                val intent = android.content.Intent(this, CrashActivity::class.java).apply {
                    putExtra(CrashActivity.EXTRA_CRASH_INFO, crashContent)
                    putExtra(CrashActivity.EXTRA_ERROR_MESSAGE, "Crash log detected from previous launch")
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
                finish()
                return
            }
        }

        enableEdgeToEdge()
        setContent {
            val preferencesManager = remember { UserPreferencesManager(applicationContext) }
            val themeMode by preferencesManager.themeMode.collectAsState(initial = "SYSTEM")

            val isDark = when (themeMode.lowercase()) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            AppTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppTheme.colors.canvas
                ) {
                    MainNavigation()
                }
            }
        }
    }
}

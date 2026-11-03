package com.expensetracker.app

import android.app.Application
import android.content.Intent
import android.util.Log
import java.io.File

class ExpenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val stackTrace = Log.getStackTraceString(throwable)
            Log.e("EXPENSE_TRACKER_CRASH", "FATAL CRASH on thread ${thread.name}: ${throwable.message}", throwable)
            try {
                val crashFile = File(filesDir, "last_crash.txt")
                crashFile.writeText(stackTrace)
            } catch (e: Exception) {
                Log.e("EXPENSE_TRACKER_CRASH", "Failed to write crash log", e)
            }

            try {
                val intent = Intent(this, CrashActivity::class.java).apply {
                    putExtra(CrashActivity.EXTRA_CRASH_INFO, stackTrace)
                    putExtra(CrashActivity.EXTRA_ERROR_MESSAGE, throwable.localizedMessage ?: throwable.message ?: throwable.javaClass.simpleName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
                android.os.Process.killProcess(android.os.Process.myPid())
                System.exit(10)
            } catch (e: Exception) {
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }
}



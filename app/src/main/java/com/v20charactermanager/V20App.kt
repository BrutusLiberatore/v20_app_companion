package com.v20charactermanager

import android.app.Application
import com.v20charactermanager.data.di.AppContainer
import com.v20charactermanager.domain.engine.applyToEngines
import com.v20charactermanager.ui.settings.HouseRulesViewModel
import com.v20charactermanager.util.CrashHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class V20App : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        CrashHandler.init(this) { _ ->
            // Crash logged to file, can be viewed from Settings
        }
        applySavedHouseRules()
    }

    /**
     * Configures the dice/XP engines with the house rules of the last
     * configured chronicle, so custom rules survive an app restart.
     */
    private fun applySavedHouseRules() {
        appScope.launch {
            try {
                val prefs = getSharedPreferences(HouseRulesViewModel.PREFS_NAME, MODE_PRIVATE)
                val chronicleId = prefs.getString(HouseRulesViewModel.KEY_LAST_CHRONICLE, null)
                if (!chronicleId.isNullOrBlank()) {
                    AppContainer(applicationContext).houseRuleRepository
                        .getHouseRules(chronicleId)
                        .applyToEngines()
                }
            } catch (_: Exception) { }
        }
    }
}

package ai.iskra.mobile.platform

import android.content.Context
import android.content.SharedPreferences

actual object PlatformStorage {
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences("iskra_session_prefs", Context.MODE_PRIVATE)
    }

    actual fun readSessionJson(): String? =
        prefs?.getString("iskra_session", null)

    actual fun writeSessionJson(value: String?) {
        prefs?.edit()?.apply {
            if (value == null) remove("iskra_session") else putString("iskra_session", value)
            apply()
        }
    }
}

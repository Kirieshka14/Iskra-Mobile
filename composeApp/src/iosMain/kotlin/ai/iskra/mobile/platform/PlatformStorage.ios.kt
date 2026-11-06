package ai.iskra.mobile.platform

import platform.Foundation.NSUserDefaults

actual object PlatformStorage {
    private const val KEY = "iskra_session"

    actual fun readSessionJson(): String? =
        NSUserDefaults.standardUserDefaults.stringForKey(KEY)

    actual fun writeSessionJson(value: String?) {
        val defaults = NSUserDefaults.standardUserDefaults
        if (value == null) {
            defaults.removeObjectForKey(KEY)
        } else {
            defaults.setObject(value, forKey = KEY)
        }
    }
}

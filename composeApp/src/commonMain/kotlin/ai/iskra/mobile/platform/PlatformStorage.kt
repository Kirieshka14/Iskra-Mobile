package ai.iskra.mobile.platform

expect object PlatformStorage {
    fun readSessionJson(): String?
    fun writeSessionJson(value: String?)
}

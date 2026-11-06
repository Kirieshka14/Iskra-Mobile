package ai.iskra.mobile

import ai.iskra.mobile.platform.PlatformStorage
import ai.iskra.mobile.ui.IskraMobileApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlatformStorage.init(this)
        setContent {
            IskraMobileApp()
        }
    }
}

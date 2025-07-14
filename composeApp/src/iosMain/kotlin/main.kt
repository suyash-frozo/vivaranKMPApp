import androidx.compose.ui.window.ComposeUIViewController
import com.vivaran.app.App
import com.vivaran.shared.di.sharedModule
import com.vivaran.shared.di.platformModule
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    startKoin {
        modules(sharedModule, platformModule)
    }
    return ComposeUIViewController { App() }
}
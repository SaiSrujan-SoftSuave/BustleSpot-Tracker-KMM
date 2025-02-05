import androidx.compose.ui.window.ComposeUIViewController
import org.company.app.App
import org.company.app.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    initKoin()
    App()
}


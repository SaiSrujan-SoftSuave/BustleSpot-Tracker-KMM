import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window

import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension
import org.company.app.App
import org.company.app.di.initKoin

fun main() = application {
    initKoin()
    Window(
        title = "Compose Multiplatform App",
        state = rememberWindowState(size = DpSize(width = 420.dp, height = 800.dp)),
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(350, 600)
        App()
    }
}

@Preview
@Composable
fun AppPreview() { App() }
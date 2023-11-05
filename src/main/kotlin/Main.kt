import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
@Preview
fun App(scope: CoroutineScope) {

    var sliderPosition by remember { mutableStateOf(0f) }

    var connectState by remember { mutableStateOf(0) }
    var deviceName by remember { mutableStateOf("") }
    CmdManager.scope = scope
    scope.launch {
        CmdManager.getDevicesFlow().collect { devices ->
            connectState = when {
                devices.isEmpty() -> 0
                devices.size == 1 -> 1
                else -> 2
            }
            if (connectState == 1) {
                val split = devices.last().split("\t")
                deviceName = "${split[1]}-${split[0]}"
            }
        }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Cmd Panel") },
                    actions = {
                        Text(
                            modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp),
                            text = when (connectState) {
                                0 -> "No device connected"
                                1 -> "Device connected: $deviceName"
                                else -> "Multiple devices connected"
                            }
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Button(onClick = { scope.launch { sendCmd("adb reboot") } }) { Text("adb reboot") }
                Slider(
                    valueRange = 0f..10f,
                    value = sliderPosition,
                    onValueChange = {
                        sliderPosition = it
                    },
                    onValueChangeFinished = {

                    },
                    steps = 9,
                )
            }
        }
    }
}


suspend fun sendCmd(cmd: String) {
    withContext(Dispatchers.IO) {
        Runtime.getRuntime().exec("adb wait-for-device")
        Runtime.getRuntime().exec(cmd)
    }
}

fun main() = application {
    val infiniteLoopScope = rememberCoroutineScope()
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            size = DpSize(800.dp, 600.dp),
            position = WindowPosition(alignment = Alignment.Center)

        ),
        icon = painterResource("ic_cmd.png")
    ) {
        App(infiniteLoopScope)
    }
}

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Phone
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
import components.CmdGrid
import entity.Cmd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
@Preview
fun App(scope: CoroutineScope) {

    var connectState by remember { mutableStateOf(NO_DEVICE_STATE) }
    var deviceName by remember { mutableStateOf("") }
    val cmds = remember { mutableStateListOf<Cmd>() }
    CmdManager.scope = scope
    scope.launch {
        CmdManager.getDevicesFlow().collect { devices ->
            connectState = when {
                devices.isEmpty() -> NO_DEVICE_STATE
                devices.size == 1 -> DEVICE_CONNECTED_STATE
                else -> MULTIPLE_DEVICES_STATE
            }
            if (connectState == DEVICE_CONNECTED_STATE) {
                val split = devices.last().split("\t")
                deviceName = "${split[DEVICE_CONNECTED_STATE]}-${split[NO_DEVICE_STATE]}"
            }
        }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Cmd Panel") },
                    actions = {
                        DeviceConnectState(connectState, deviceName)
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    if (CmdManager.isLogcating())
                        CmdManager.stopLogcat()
                    else
                        CmdManager.startLogcat(scope)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) {
            CmdGrid(cmds.toList())
        }
    }
}

@Composable
private fun DeviceConnectState(connectState: Int, deviceName: String) {
    Text(
        modifier = Modifier.padding(NO_DEVICE_STATE.dp, NO_DEVICE_STATE.dp, 16.dp, NO_DEVICE_STATE.dp),
        text = when (connectState) {
            NO_DEVICE_STATE -> "No device connected"
            DEVICE_CONNECTED_STATE -> "Device connected: $deviceName"
            else -> "Multiple devices connected"
        }
    )
}

fun main() = application {
    val infiniteLoopScope = rememberCoroutineScope()
//    DataManager.loadData()
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

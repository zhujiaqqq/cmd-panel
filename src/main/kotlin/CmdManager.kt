import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Thread.sleep
import kotlin.concurrent.thread

object CmdManager {
    private val devicesFlow = MutableStateFlow<List<String>>(emptyList())
    var scope: CoroutineScope? = null
    fun getDevicesFlow() = devicesFlow
    private fun getCurrentDevices() {
        scope?.launch {
            val process = sendCmd("adb devices")
            val inputList = getInput(process)
            if (inputList.isEmpty() || inputList[0] != "List of devices attached") {
                println("no devices")
                devicesFlow.emit(emptyList())
                return@launch
            }
            devicesFlow.emit(inputList.drop(1))
        }
    }

    init {
        thread {
            while (true) {
                getCurrentDevices()
                sleep(1000)
            }
        }
    }

    private suspend fun sendCmd(cmd: String): Process =
        withContext(Dispatchers.IO) {
            Runtime.getRuntime().exec("adb wait-for-device")
            Runtime.getRuntime().exec(cmd)
        }

    private suspend fun getInput(process: Process): List<String> {
        return withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val list = mutableListOf<String>()
            var line: String?
            while (true) {
                line = reader.readLine()
                line?.apply {
                    if (this.isNotEmpty()) {
                        list.add(this)
                    }
                } ?: break
            }
            list
        }
    }
}
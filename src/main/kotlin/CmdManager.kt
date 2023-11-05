import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object CmdManager {

    fun getCurrentDevices(scope: CoroutineScope, callback: (List<String>) -> Unit) {
        scope.launch {
            val process = sendCmd("adb devices")
            val inputList = getInput(process)
            if (inputList.isEmpty() || inputList[0] != "List of devices attached") {
                println("no divices")
                callback(emptyList())
                return@launch
            }
            callback(inputList.drop(1))
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
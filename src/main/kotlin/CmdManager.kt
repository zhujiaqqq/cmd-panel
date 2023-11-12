import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

object CmdManager {
    private val devicesFlow = MutableStateFlow<List<String>>(emptyList())
    var scope: CoroutineScope? = null
    private var logcatProcess: Process? = null
    private var outputFile: File? = null
    private var reader: BufferedReader? = null
    fun isLogcating() = logcatProcess != null
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
            while (true) {
                reader.readLine()?.apply {
                    if (this.isNotEmpty()) {
                        list.add(this)
                    }
                } ?: break
            }
            list
        }
    }

    fun startLogcat(scope: CoroutineScope) {
        scope.launch {
            logcatProcess = null
            logcatProcess = sendCmd("adb logcat")
            getLogcat(logcatProcess!!)

        }
    }

    fun stopLogcat() {
        logcatProcess?.destroy()
        reader?.close()
        outputFile
        logcatProcess = null
    }

    private suspend fun getLogcat(process: Process) {
        withContext(Dispatchers.IO) {
            reader = BufferedReader(InputStreamReader(process.inputStream))
            val userHome = getUserHome()
            val outputDirectory = File(userHome, "output")
            if (outputDirectory.exists().not() && outputDirectory.mkdir().not()) {
                println("output directory create fail")
            }
            var lineCount = 0
            var fileCount = 1

            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault())
            try {
                reader?.forEachLine { line ->
                    // 每10000行创建一个新文件
                    if (lineCount % 10000 == 0) {
                        val timestamp = dateFormat.format(Date())
                        outputFile = File(outputDirectory, "$timestamp.log")
                        if (outputFile?.exists() == false) {
                            outputFile?.createNewFile()
                        }
                        fileCount++
                    }

                    // 将行写入文件
                    outputFile?.appendText("$line\n")
                    lineCount++
                }
            } catch (e: IOException) {
                println("e: $e")
            }
            println("log end")
        }
    }

    private fun getUserHome(): File {
        val userHomePath = System.getProperty("user.home") ?: throw RuntimeException("user.home is null")
        return File(userHomePath)
    }
}
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.ResourceLoader
import entity.Cmd
import org.yaml.snakeyaml.Yaml

object DataManager {
    @OptIn(ExperimentalComposeUiApi::class)
    fun loadData(path: String = "config.yaml") {
        ResourceLoader.Default.load(path).use {
//            val reader = YamlReader(FileReader(InputStreamReader(it).encoding))
//            while (true) {
//                val cmd: Cmd = reader.read(Cmd::class.java) ?: break
//                println("cmd: $cmd")
//            }
//            val yaml = Yaml()
//            yaml.loadAs(it,Cmd::class.java)
        }
    }
}
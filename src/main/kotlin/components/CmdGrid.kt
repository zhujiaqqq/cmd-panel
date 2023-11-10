package components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import entity.Cmd

@Composable
fun CmdGrid(cmds: List<Cmd>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        items(cmds) { cmd -> CmdItem(cmd) }
    }
}

@Composable
fun CmdItem(cmd: Cmd) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = cmd.description,
                style = MaterialTheme.typography.h6
            )
            Button(
                onClick = {},
                modifier = Modifier.align(alignment = Alignment.BottomEnd).padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Text(text = "Run")
            }
        }

    }
}
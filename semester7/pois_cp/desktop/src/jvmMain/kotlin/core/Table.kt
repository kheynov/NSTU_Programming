package core

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.awt.Toolkit

@Composable
fun Table(
    data: TableData,
    onEditing: (String) -> Unit = {},
    onDeleting: (String) -> Unit = {},
    onBook: (String) -> Unit = {},
    onInfo: (String) -> Unit = {},
    bookingEnabled: Boolean = false,
    infoEnabled: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val idWeight = 0.1f
    val colWeight = 0.8f
    BoxWithConstraints(modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // header
            Row(modifier = Modifier.height(70.dp)) {
                repeat(data.header.size) { idx ->
                    TableCell(data.header[idx], if (idx == 0) idWeight else colWeight)
                }
                Spacer(modifier = Modifier.weight(0.08f))
                Spacer(modifier = Modifier.weight(0.08f))
                if (bookingEnabled) Spacer(modifier = Modifier.weight(0.08f))
                if (infoEnabled) Spacer(modifier = Modifier.weight(0.08f))
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(data.data) {
                    val (row) = it
                    Row(modifier = Modifier.height(50.dp), verticalAlignment = Alignment.CenterVertically) {
                        repeat(row.size) { colIdx ->
                            TableCell(
                                text = row[colIdx],
                                weight = if (colIdx == 0) idWeight else colWeight,
                                type = CellType.DATA,
                                onClick = if (colIdx == 0) {
                                    {
                                        try {
                                            Toolkit.getDefaultToolkit().systemClipboard.setContents(
                                                java.awt.datatransfer.StringSelection(row[colIdx]),
                                                null
                                            )
                                        } catch (e: Exception) {
                                            println("Error: ${e.javaClass.simpleName}")
                                        }
                                    }
                                } else null
                            )
                        }

                        TableCell(
                            type = CellType.EDIT,
                            weight = 0.08f,
                            modifier = Modifier.widthIn(min = 50.dp),
                            onClick = {
                                onEditing(row[0])
                            },
                        )
                        TableCell(
                            type = CellType.DELETE,
                            weight = 0.08f,
                            modifier = Modifier.widthIn(50.dp),
                            onClick = {
                                onDeleting(row[0])
                            },
                        )
                        if (bookingEnabled) {
                            TableCell(
                                type = CellType.BOOK,
                                weight = 0.08f,
                                modifier = Modifier.widthIn(50.dp),
                                onClick = {
                                    onBook(row[0])
                                },
                            )
                        }

                        if (infoEnabled) {
                            TableCell(
                                type = CellType.INFO,
                                weight = 0.08f,
                                modifier = Modifier.widthIn(50.dp),
                                onClick = {
                                    onInfo(row[0])
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TablePreview() {
    MaterialTheme {
        Surface(modifier = Modifier.background(Color.White)) {
            Table(
                TableData(
                    header = listOf("id", "data1", "data2"), data = listOf(
                        DataRow(listOf("1", "2", "3")),
                        DataRow(listOf("1", "2", "3")),
                        DataRow(listOf("1", "2", "3")),
                        DataRow(listOf("1", "2", "3")),
                    )
                )
            )
        }
    }
}
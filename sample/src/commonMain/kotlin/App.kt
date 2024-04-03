import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinlang.compose.*
import kotlinlang.compose.TextInterval.TextPosition

val helloWorld = """
    fun main() {
        val x = 3
        val y = 2
        println("${'$'}x + ${'$'}y = ${'$'}{x + y}")
    }
    
""".trimIndent()

private enum class Theme {
    System, Light, Dark
}

@Composable
fun App(
    startMessage: String = helloWorld,
    onBackgroundColorChanged: (isDark: Boolean, light: Color, dark: Color) -> Unit = { _, _, _ -> }
) {
    val buttonColorScheme = ReplaceButtonsColorScheme(
        backgroundColor = MaterialTheme.colorScheme.primary,
        textColor = MaterialTheme.colorScheme.onPrimary
    )
    val darkColorScheme = makeDarkColorScheme(buttonColorScheme)
    val lightColorScheme = makeLightColorScheme(buttonColorScheme)
    var theme by remember { mutableStateOf(Theme.System) }
    val colorScheme = when (theme) {
        Theme.System -> if (isSystemInDarkTheme()) darkColorScheme else lightColorScheme
        Theme.Light -> lightColorScheme
        Theme.Dark -> darkColorScheme
    }
    LaunchedEffect(colorScheme) {
        onBackgroundColorChanged(
            colorScheme == darkColorScheme,
            lightColorScheme.backgroundColor,
            darkColorScheme.backgroundColor
        )
    }
    var textFieldValue by remember { mutableStateOf(TextFieldValue(startMessage)) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showFakeDiagnostics by remember { mutableStateOf(false) }
    val textColor = colorScheme.identifierColor
    val fakeDiagnostics = listOf(
        DiagnosticDescriptor("Meow", DiagnosticSeverity.ERROR, TextInterval(TextPosition(2, 5), TextPosition(3, 7))),
        DiagnosticDescriptor("Gav", DiagnosticSeverity.WARNING, TextInterval(TextPosition(2, 4), TextPosition(3, 6))),
        DiagnosticDescriptor("Moo", DiagnosticSeverity.INFO, TextInterval(TextPosition(2, 3), TextPosition(3, 5))),
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    shape = RoundedCornerShape(50),
                )
            }
        },
        containerColor = colorScheme.backgroundColor,
        contentColor = textColor,
    ) {
        Column(Modifier.padding(it).windowInsetsPadding(WindowInsets.ime)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Show fake diagnostics")
                Switch(showFakeDiagnostics, { showFakeDiagnostics = it })
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Color scheme")
                Row {
                    Button(
                        onClick = { theme = Theme.System },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (theme == Theme.System) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (theme == Theme.System) MaterialTheme.colorScheme.onPrimary else textColor,
                        ),
                    ) {
                        Text("System")
                    }
                    Spacer(Modifier.width(10.dp))
                    Button(
                        onClick = { theme = Theme.Light },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (theme == Theme.Light) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (theme == Theme.Light) MaterialTheme.colorScheme.onPrimary else textColor,
                        ),
                    ) {
                        Text("Light")
                    }
                    Spacer(Modifier.width(10.dp))
                    Button(
                        onClick = { theme = Theme.Dark },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (theme == Theme.Dark) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (theme == Theme.Dark) MaterialTheme.colorScheme.onPrimary else textColor,
                        ),
                    ) {
                        Text("Dark")
                    }
                }
            }
            KotlinSourceEditor(
                textFieldValue = textFieldValue,
                onTextFieldValueChange = { textFieldValue = it },
                modifier = Modifier.fillMaxSize(),
                colorScheme = colorScheme,
                sourceEditorFeaturesConfiguration = KotlinSourceEditorFeaturesConfiguration(),
                snackbarHostState = snackbarHostState,
                diagnostics = if (showFakeDiagnostics) fakeDiagnostics else emptyList(),
            )
        }
    }
}

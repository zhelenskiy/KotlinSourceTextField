import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import kotlinlang.compose.ExternalKeyboardEventModifiers

@Composable
fun KeyButtons(
    externalKeyboardEventModifiers: ExternalKeyboardEventModifiers,
    onEscPressed: () -> Unit,
    onTabPressed: () -> Unit,
    onModifierChanged: (ExternalKeyboardEventModifiers) -> Unit,
    textColor: Color,
) {
    Button(
        onClick = onEscPressed,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier.focusProperties { canFocus = false },
    ) {
        Text("Esc", color = textColor)
    }
    Button(
        onClick = onTabPressed,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier.focusProperties { canFocus = false },
    ) {
        Text("Tab", color = textColor)
    }
    Button(
        onClick = { onModifierChanged(externalKeyboardEventModifiers.copy(isShiftPressed = !externalKeyboardEventModifiers.isShiftPressed)) },
        colors = ButtonDefaults.buttonColors(containerColor = if (externalKeyboardEventModifiers.isShiftPressed) MaterialTheme.colorScheme.primary else Color.Transparent),
        modifier = Modifier.focusProperties { canFocus = false },
    ) {
        Text("Shift", color = if (externalKeyboardEventModifiers.isShiftPressed) MaterialTheme.colorScheme.onPrimary else textColor)
    }
    Button(
        onClick = { onModifierChanged(externalKeyboardEventModifiers.copy(isCtrlPressed = !externalKeyboardEventModifiers.isCtrlPressed)) },
        colors = ButtonDefaults.buttonColors(containerColor = if (externalKeyboardEventModifiers.isCtrlPressed) MaterialTheme.colorScheme.primary else Color.Transparent),
        modifier = Modifier.focusProperties { canFocus = false },
    ) {
        Text("Ctrl", color = if (externalKeyboardEventModifiers.isCtrlPressed) MaterialTheme.colorScheme.onPrimary else textColor)
    }
    Button(
        onClick = {  onModifierChanged(externalKeyboardEventModifiers.copy(isAltPressed = !externalKeyboardEventModifiers.isAltPressed))  },
        colors = ButtonDefaults.buttonColors(containerColor = if (externalKeyboardEventModifiers.isAltPressed) MaterialTheme.colorScheme.primary else Color.Transparent),
        modifier = Modifier.focusProperties { canFocus = false },
    ) {
        Text("Alt", color = if (externalKeyboardEventModifiers.isAltPressed) MaterialTheme.colorScheme.onPrimary else textColor)
    }
    Button(
        onClick = {  onModifierChanged(externalKeyboardEventModifiers.copy(isMetaPressed = !externalKeyboardEventModifiers.isMetaPressed))  },
        colors = ButtonDefaults.buttonColors(containerColor = if (externalKeyboardEventModifiers.isMetaPressed) MaterialTheme.colorScheme.primary else Color.Transparent),
        modifier = Modifier.focusProperties { canFocus = false },
    ) {
        Text("Meta", color = if (externalKeyboardEventModifiers.isMetaPressed) MaterialTheme.colorScheme.onPrimary else textColor)
    }
}

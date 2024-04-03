package kotlinlang.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import editor.basic.SourceCodePosition
import kotlinx.coroutines.delay
import kotlinlang.utils.VerticalScrollbar
import kotlinlang.utils.size
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

public data class DiagnosticsPopupSettings(
    val margin: PaddingValues = PaddingValues(4.dp),
    val headerSettings: DiagnosticsPopupHeaderSettings = DiagnosticsPopupHeaderSettings(),
    val diagnosticsListSettings: DiagnosticsPopupDiagnosticsListSettings = DiagnosticsPopupDiagnosticsListSettings(),
)

public data class DiagnosticsPopupHeaderSettings(
    val shape: Shape = RoundedCornerShape(8.dp),
    val labelTextStyle: TextStyle = TextStyle.Default.copy(fontSize = 14.sp),
    val iconSize: Dp? = 16.dp,
    val padding: PaddingValues = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
    val spaceBetweenDiagnosticSeverityIconAndCount: Dp = 2.dp,
    val spaceBetweenDiagnosticSeverities: Dp = 6.dp,
)

public data class DiagnosticsPopupDiagnosticsListSettings(
    val shape: Shape = RoundedCornerShape(8.dp),
    val scrollbarsVisibility: ScrollbarsVisibility = ScrollbarsVisibility.Both,
    val spanBetweenHeaderAndList: Dp = 4.dp,
    val maxHightRatio: Float = 0.7f,
    val maxWidthRatio: Float = 0.6f,
    val shadowElevation: Dp = 4.dp,
    val separatorHeight: Dp = 0.5.dp,
    val separatorHorizontalPadding: Dp = 8.dp,
    val diagnosticRowPadding: PaddingValues = PaddingValues(8.dp),
    val diagnosticRowSpaceBetweenIconAndMessage: Dp = 8.dp,
    val disgnosticRowSpaceBetweenMessageAndCopyButton: Dp = 8.dp,
    val copyButtonIconRecoverDelay: Duration = 800.milliseconds,
    val severityIconSize: Dp? = null,
    val copyDiagnosticContentIconSize: Dp? = null,
    val copiedDiagnosticContentIconSize: Dp? = null,
    val labelTextStyle: TextStyle = TextStyle.Default,
    val copyDiagnosticContentDescription: String = "Copy diagnostic message",
    val copiedDiagnosticContentDescription: String = "Copied diagnostic message successfully",
    val copiedDiagnosticContentNotificationMessage: String = "Copied successfully!",
) {
    init {
        require(maxHightRatio in 0.0f..1.0f) { "Invalid maxHightRatio: $maxHightRatio" }
        require(maxWidthRatio in 0.0f..1.0f) { "Invalid maxWidthRatio: $maxWidthRatio" }
    }
}

@Composable
internal fun BoxWithConstraintsScope.DiagnosticsPopup(
    diagnostics: List<DiagnosticDescriptor>,
    colorScheme: DiagnosticsPopupColorScheme,
    settings: DiagnosticsPopupSettings,
    modifier: Modifier = Modifier,
    onCopied: () -> Unit,
    onPositionChange: (SourceCodePosition) -> Unit,
) {
    val maxWidth = maxWidth
    val maxHeight = maxHeight
    Box(modifier = modifier.padding(settings.margin)) {
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Top) {
            val (collapsed, onCollapsedChanged) = rememberSaveable { mutableStateOf(true) }
            DiagnosticsHeader(
                collapsed = collapsed,
                onCollapsedChanged = onCollapsedChanged,
                diagnostics = diagnostics,
                colorScheme = colorScheme.diagnosticsHeaderColorScheme,
                settings = settings.headerSettings,
            )
            AnimatedVisibility(!collapsed && diagnostics.isNotEmpty()) {
                DiagnosticsList(
                    maxWidth = maxWidth,
                    maxHeight = maxHeight,
                    diagnostics = diagnostics,
                    colorScheme = colorScheme.diagnosticsListColorScheme,
                    onPositionChange = onPositionChange,
                    settings = settings.diagnosticsListSettings,
                    onCopied = onCopied,
                )
            }
        }
    }
}

@Composable
private fun DiagnosticsHeader(
    collapsed: Boolean,
    onCollapsedChanged: (Boolean) -> Unit,
    diagnostics: List<DiagnosticDescriptor>,
    colorScheme: DiagnosticsPopupHeaderColorScheme,
    settings: DiagnosticsPopupHeaderSettings,
) {
    val shape = settings.shape
    val lastNotEmptyDiagnosticsCount = remember { mutableStateMapOf<DiagnosticSeverity, Int>() }
    LaunchedEffect(diagnostics) {
        for (severity in DiagnosticSeverity.entries) {
            val count = diagnostics.count { it.severity == severity }
            if (count > 0) {
                lastNotEmptyDiagnosticsCount[severity] = count
            }
        }
    }
    AnimatedVisibility(diagnostics.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .clip(shape)
                .background(colorScheme.backgroundColor, shape)
                .focusProperties { canFocus = false }
                .clickable { onCollapsedChanged(!collapsed) }
                .padding(settings.padding),
        ) {
            for (severity in DiagnosticSeverity.entries) {
                val currentDiagnostics = diagnostics.filter { it.severity == severity }
                AnimatedVisibility(currentDiagnostics.isNotEmpty() || diagnostics.isEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        severity.icon(
                            contentDescription = severity.name,
                            modifier = Modifier.size(settings.iconSize),
                            colorScheme = colorScheme.iconColorScheme,
                        )
                        Spacer(Modifier.width(settings.spaceBetweenDiagnosticSeverityIconAndCount))
                        val countToShow =
                            if (currentDiagnostics.isNotEmpty()) currentDiagnostics.size
                            else lastNotEmptyDiagnosticsCount[severity] ?: 0
                        Text(
                            text = countToShow.toString(),
                            style = settings.labelTextStyle,
                            color = colorScheme.countLabelColor,
                        )
                    }
                }
                Spacer(Modifier.width(settings.spaceBetweenDiagnosticSeverities))
            }
        }
    }
}

@Composable
private fun DiagnosticsList(
    maxWidth: Dp,
    maxHeight: Dp,
    diagnostics: List<DiagnosticDescriptor>,
    colorScheme: DiagnosticsListColorScheme,
    onPositionChange: (SourceCodePosition) -> Unit,
    settings: DiagnosticsPopupDiagnosticsListSettings,
    onCopied: () -> Unit,
) {
    val shape = settings.shape
    Box(
        Modifier
            .widthIn(max = settings.maxWidthRatio * maxWidth)
            .heightIn(max = settings.maxHightRatio * maxHeight)
            .padding(top = settings.spanBetweenHeaderAndList)
            .shadow(
                elevation = settings.shadowElevation,
                ambientColor = colorScheme.shadowColor,
                spotColor = colorScheme.shadowColor,
                shape = shape,
            )
            .background(colorScheme.backgroundColor, shape)
            .clip(shape)
    ) {
        val verticalScrollState = rememberScrollState()
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .verticalScroll(verticalScrollState)
        ) {
            val rowWidths = remember(diagnostics) { mutableStateListOf(*Array(diagnostics.size) { 0 }) }
            val rowWidthsSet = remember(diagnostics) { mutableStateListOf(*Array(diagnostics.size) { false }) }
            val maxRowWidth = rowWidths.maxOrNull() ?: 0
            val maxRowWidthDp = LocalDensity.current.run { maxRowWidth.toDp() }
            for ((index, diagnostic) in diagnostics.withIndex()) {
                if (index > 0) {
                    HorizontalDivider(
                        thickness = settings.separatorHeight,
                        color = colorScheme.separatorColor,
                        modifier = Modifier
                            .padding(horizontal = settings.separatorHorizontalPadding)
                            .width(maxRowWidthDp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .run {
                            if (diagnostic.interval == null) return@run this
                            val sourceCodePosition = SourceCodePosition(
                                line = diagnostic.interval.start.line,
                                column = diagnostic.interval.start.ch
                            )
                            clickable { onPositionChange(sourceCodePosition) }
                        }
                        .padding(settings.diagnosticRowPadding)
                        .onSizeChanged {
                            if (!rowWidthsSet[index]) {
                                rowWidths[index] = it.width
                                rowWidthsSet[index] = true
                            }
                        },
                ) {
                    diagnostic.severity.icon(
                        contentDescription = null,
                        colorScheme = colorScheme.iconColorScheme,
                        modifier = Modifier.size(settings.severityIconSize),
                    )
                    Spacer(Modifier.width(settings.diagnosticRowSpaceBetweenIconAndMessage))
                    Text(
                        text = diagnostic.message,
                        color = colorScheme.messageColor,
                        style = settings.labelTextStyle,
                    )
                    Spacer(Modifier.width(maxRowWidthDp - LocalDensity.current.run { rowWidths[index].toDp() }))
                    Spacer(Modifier.width(settings.disgnosticRowSpaceBetweenMessageAndCopyButton))
                    ClipboardButton(diagnostic, colorScheme.copyButtonColorScheme, settings, onCopied)
                }
            }
        }

        AnimatedVisibility(
            visible = settings.scrollbarsVisibility.showVertical,
            modifier = Modifier.align(Alignment.CenterEnd),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            VerticalScrollbar(
                scrollState = verticalScrollState,
                hoverColor = colorScheme.scrollbarsColorScheme.hoveredColor,
                unhoverColor = colorScheme.scrollbarsColorScheme.notHoveredColor,
            )
        }
    }
}

@Composable
private fun ClipboardButton(
    diagnostic: DiagnosticDescriptor,
    colorScheme: CopyButtonColorScheme,
    settings: DiagnosticsPopupDiagnosticsListSettings,
    onCopied: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    var useDoneIcon by remember { mutableStateOf(false) }
    LaunchedEffect(useDoneIcon) {
        if (useDoneIcon) {
            delay(settings.copyButtonIconRecoverDelay)
            useDoneIcon = false
        }
    }

    AnimatedContent(
        targetState = useDoneIcon,
        transitionSpec = {
            if (useDoneIcon) scaleIn() togetherWith scaleOut()
            else fadeIn() togetherWith fadeOut()
        },
    ) {
        if (it) {
            Icon(
                imageVector = Icons.Default.Done,
                tint = colorScheme.doneColor,
                contentDescription = settings.copiedDiagnosticContentDescription,
                modifier = Modifier.size(settings.copiedDiagnosticContentIconSize),
            )
        } else {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                tint = colorScheme.defaultColor,
                contentDescription = settings.copyDiagnosticContentDescription,
                modifier = Modifier
                    .focusProperties { canFocus = false }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        clipboardManager.setText(AnnotatedString(diagnostic.message))
                        useDoneIcon = true
                        onCopied()
                    }
                    .size(settings.copyDiagnosticContentIconSize),
            )
        }
    }
}

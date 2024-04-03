package kotlinlang.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import editor.basic.*
import kotlinlang.tokens.KotlinToken
import kotlinlang.tokens.tokenizeKotlin
import kotlinlang.utils.*
import kotlinlang.utils.HorizontalScrollbar
import kotlinlang.utils.VerticalScrollbar
import kotlinlang.utils.onPointerEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Serializable
public data class KotlinSourceEditorFeaturesConfiguration(
    val useRainbowBrackets: Boolean = true,
    val highlightMatchingBracketsAtCurrentPosition: Boolean = true,
    val underlineCurrentIdentifierUsages: Boolean = true,
    val showIndentation: Boolean = true,
    val showStickyHeader: Boolean = true,
    val showLineNumbers: Boolean = true,
    val indentCode: Boolean = true,
    val reuseFollowingClosingBracket: Boolean = true,
    val reuseClosingCharQuote: Boolean = true,
    val reuseClosingStringQuote: Boolean = true,
    val addClosingBracket: Boolean = true,
    val addClosingCharQuote: Boolean = true,
    val addClosingSingleLineStringQuote: Boolean = true,
    val addClosingMultiLineStringQuote: Boolean = true,
    val indentClosingBracket: Boolean = true,
    val indentNewLine: Boolean = true,
    val removeIndentOnBackspace: Boolean = true,
    val removeFollowingClosingBracket: Boolean = true,
    val removeFollowingClosingCharQuote: Boolean = true,
    val removeFollowingClosingStringQuote: Boolean = true,
    val duplicateStringsAndLines: Boolean = true,
    val commentBlock: Boolean = true,
    val enableFindAndReplace: Boolean = true,
    val highlightDiagnostics: Boolean = true,
    val enableDiagnosticsPopup: Boolean = true,
    val enableDiagnosticsTooltip: Boolean = true,
)

public data class KotlinSourceEditorSettings(
    val sourceTextStyle: TextStyle,
    val horizontalThresholdEdgeChars: Int = 5,
    val verticalThresholdEdgeLines: Int = 1,
    val scrollbarsVisibility: ScrollbarsVisibility = ScrollbarsVisibility.Both,
    val scrollbarsThickness: Dp = 8.dp,
    val diagnosticsHighlightingSettings: DiagnosticsHighlightingSettings = DiagnosticsHighlightingSettings(),
    val diagnosticsTooltipSettings: DiagnosticsTooltipSettings = DiagnosticsTooltipSettings(
        scrollbarsVisibility = scrollbarsVisibility,
    ),
    val indentationLinesSettings: IndentationLinesSettings = IndentationLinesSettings(),
    val stickyHeaderSettings: SteakyHeaderSettings = SteakyHeaderSettings(),
    val diagnosticsPopupSettings: DiagnosticsPopupSettings = DiagnosticsPopupSettings(
        diagnosticsListSettings = DiagnosticsPopupDiagnosticsListSettings(
            scrollbarsVisibility = scrollbarsVisibility,
        ),
    ),
    val findAndReplaceSettings: FindAndReplaceSettings = FindAndReplaceSettings(textFieldTextStyle = sourceTextStyle),
    val keyBindings: KeyBindings = KeyBindings(),
)

@Composable
public fun KotlinSourceEditorSettings(
    horizontalThresholdEdgeChars: Int = 5,
    verticalThresholdEdgeLines: Int = 1,
    scrollbarsVisibility: ScrollbarsVisibility = ScrollbarsVisibility.Both,
    scrollbarsThickness: Dp = 8.dp,
    diagnosticsHighlightingSettings: DiagnosticsHighlightingSettings = DiagnosticsHighlightingSettings(),
    diagnosticsTooltipSettings: DiagnosticsTooltipSettings = DiagnosticsTooltipSettings(
        scrollbarsVisibility = scrollbarsVisibility,
    ),
    indentationLinesSettings: IndentationLinesSettings = IndentationLinesSettings(),
    stickyHeaderSettings: SteakyHeaderSettings = SteakyHeaderSettings(),
    diagnosticsPopupSettings: DiagnosticsPopupSettings = DiagnosticsPopupSettings(
        diagnosticsListSettings = DiagnosticsPopupDiagnosticsListSettings(
            scrollbarsVisibility = scrollbarsVisibility,
        )
    ),
    findAndReplaceSettings: FindAndReplaceSettings = FindAndReplaceSettings(
        textFieldTextStyle = kotlinSourceEditorDefaultTextStyle
    ),
    keyBindings: KeyBindings = KeyBindings(),
): KotlinSourceEditorSettings = KotlinSourceEditorSettings(
    sourceTextStyle = kotlinSourceEditorDefaultTextStyle,
    horizontalThresholdEdgeChars = horizontalThresholdEdgeChars,
    verticalThresholdEdgeLines = verticalThresholdEdgeLines,
    scrollbarsVisibility = scrollbarsVisibility,
    scrollbarsThickness = scrollbarsThickness,
    diagnosticsHighlightingSettings = diagnosticsHighlightingSettings,
    diagnosticsTooltipSettings = diagnosticsTooltipSettings,
    indentationLinesSettings = indentationLinesSettings,
    stickyHeaderSettings = stickyHeaderSettings,
    diagnosticsPopupSettings = diagnosticsPopupSettings,
    findAndReplaceSettings = findAndReplaceSettings,
    keyBindings = keyBindings,
)

public data class KeyBindings(
    val commentChar: Char = '/',
    val moveOffsetForward: KeyEventFilterHolder = KeyEventFilterHolder(key = Key.Tab),
    val moveOffsetBackward: KeyEventFilterHolder = KeyEventFilterHolder(key = Key.Tab, isShiftPressed = true),
    val duplicateStringsAndLines: KeyEventFilterHolder = KeyEventFilterHolder(key = Key.D, isCtrlPressed = true),
    val find: KeyEventFilterHolder = KeyEventFilterHolder(key = Key.F, isCtrlPressed = true),
    val replace: KeyEventFilterHolder = KeyEventFilterHolder(key = Key.R, isCtrlPressed = true),
    val exit: KeyEventFilterHolder = KeyEventFilterHolder(key = Key.Escape),
)

public data class KeyEventFilterHolder(
    val key: Key,
    val isShiftPressed: Boolean = false,
    val isCtrlPressed: Boolean = false,
    val isAltPressed: Boolean = false,
    val isWinPressed: Boolean = false,
    val areCtrlAndWinReversedOnMac: Boolean = true,
)

internal fun KeyEventFilterHolder.toKeyEventFilter(): KeyEventFilter = { event ->
    event.key == key && event.type == KeyEventType.KeyDown &&
            event.isShiftPressed == isShiftPressed && event.isAltPressed == isAltPressed &&
            if (areCtrlAndWinReversedOnMac && isApple) event.isCtrlPressed == isWinPressed && event.isMetaPressed == isCtrlPressed
            else event.isCtrlPressed == isCtrlPressed && event.isMetaPressed == isWinPressed
}

public data class DiagnosticsHighlightingSettings(
    val thickness: Dp = 3.dp,
)

public data class IndentationLinesSettings(
    val thickness: Dp = 1.dp,
)

public data class SteakyHeaderSettings(
    val maximumHeightRatio: Float = 0.33f,
    val separatorThickness: Dp = 1.dp,
) {
    init {
        require(maximumHeightRatio in 0f..1f) { "Illegal maximumHeightRatio: $maximumHeightRatio" }
    }
}

public data class DiagnosticsTooltipSettings(
    val labelTextStyle: TextStyle = TextStyle.Default,
    val severityIconSize: Dp? = null,
    val scrollbarsVisibility: ScrollbarsVisibility = ScrollbarsVisibility.Both,
    val appearanceDelay: Duration = 400.milliseconds,
    val updateDelay: Duration = 600.milliseconds,
    val disappearanceDelay: Duration = 600.milliseconds,
    val shape: Shape = RoundedCornerShape(10.dp),
    val hitBoxPadding: PaddingValues = PaddingValues(8.dp),
    val elevation: Dp = 4.dp,
    val maximumWidthRatio: Float = 0.7f,
    val paddng: PaddingValues = PaddingValues(8.dp),
    val separatorThickness: Dp = 0.5.dp,
    val separatorPadding: PaddingValues = PaddingValues(vertical = 3.dp),
    val spaceBetweenSeverityIconAndMessage: Dp = 4.dp,
) {
    init {
        require(maximumWidthRatio in 0f..1f) { "Illegal maximumWidthRatio: $maximumWidthRatio" }
    }
}

private fun tokenizationPipeline(
    textFieldState: TextFieldValue,
    colorScheme: KotlinColorScheme,
    sourceEditorFeaturesConfiguration: KotlinSourceEditorFeaturesConfiguration,
    originalTokenizer: (String) -> List<KotlinToken>,
    bracketMatcher: (List<Token>) -> Map<SingleStyleTokenChangingScope, SingleStyleTokenChangingScope>,
): BasicSourceCodeTextFieldState<KotlinComposeToken> {
    val originalTokens = originalTokenizer(textFieldState.text)
    val tokens = originalTokens.toCompose(colorScheme)
    val matchingBrackets = bracketMatcher(tokens)

    val rainbow = colorScheme.operatorColorScheme.rainbowBrackets
    if (sourceEditorFeaturesConfiguration.useRainbowBrackets && rainbow.isNotEmpty()) {
        updateMatchingBracketsStyle(matchingBrackets.filterKeys { it is KotlinComposeToken.Operator }) { _, depth, openingStyle, closingStyle ->
            openingStyle.copy(color = rainbow[depth % rainbow.size]) to closingStyle.copy(color = rainbow[depth % rainbow.size])
        }
    }

    if (sourceEditorFeaturesConfiguration.highlightMatchingBracketsAtCurrentPosition || sourceEditorFeaturesConfiguration.underlineCurrentIdentifierUsages) {
        val currentTokens = getCurrentPositionTokens(textFieldState.selection, tokens)
        if (sourceEditorFeaturesConfiguration.highlightMatchingBracketsAtCurrentPosition) {
            updateMatchingBracesAtCurrentPositionStyle(currentTokens, matchingBrackets) {
                it.copy(background = colorScheme.operatorColorScheme.matchedBracketsBackgroundColor)
            }
        }
        if (sourceEditorFeaturesConfiguration.underlineCurrentIdentifierUsages) {
            updateSameSymbolsWithOnesAtCurrentPosition<KotlinComposeToken.Identifier>(currentTokens, tokens) {
                it.copy(textDecoration = TextDecoration.Underline)
            }
        }
    }

    return BasicSourceCodeTextFieldState(tokens, textFieldState.selection, textFieldState.composition)
}

private fun <T, R : Any> reuseResult(function: (T) -> R): (T) -> R = object : (T) -> R {
    private val lastResult: MutableStateFlow<Pair<T, R>?> = MutableStateFlow(null)
    override fun invoke(arg: T): R {
        lastResult.value?.let { (oldArg, oldResult) -> if (oldArg == arg) return oldResult }
        val result = function(arg)
        lastResult.value = arg to result
        return result
    }
}

private inline fun <T : Any> takeIf(condition: Boolean, produce: () -> T): T? =
    if (condition) produce() else null

@Composable
internal expect fun TooltipBox(
    tooltip: @Composable () -> Unit,
    delayMillis: Int,
    offset: IntOffset,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
)

@Serializable
public enum class ScrollbarsVisibility {
    None,
    Horizontal,
    Vertical,
    Both;

    public val showHorizontal: Boolean
        get() = when (this) {
            None, Vertical -> false
            Horizontal, Both -> true
        }

    public val showVertical: Boolean
        get() = when (this) {
            None, Horizontal -> false
            Vertical, Both -> true
        }
}

private fun BasicSourceCodeTextFieldState<*>.toTextFieldValue() =
    TextFieldValue(annotatedString, selection, composition)

/**
 * Default source editor text style using Jetbrains Mono font
 */
public val kotlinSourceEditorDefaultTextStyle: TextStyle
    @Composable
    get() = TextStyle.Default.copy(fontFamily = jetbrainsMono)

/**
 * Kotlin Source Editor main entry point
 *
 * @param textFieldValue The state of the source code text field.
 * @param onTextFieldValueChange The updater for the source code text field state.
 * @param colorScheme The color scheme used to configure the visual appearance of the source editor.
 * @param sourceEditorFeaturesConfiguration The configuration of features in the Kotlin source editor.
 * @param snackbarHostState The state for displaying snackbars.
 * @param modifier The modifier to be applied to the whole source editor.
 * @param plainSourceEditorModifier The modifier for the plain source editor which includes the underlying [TextField] and line numbers.
 * @param basicTextFieldModifier The modifier for the underlying [TextField].
 * @param verticalScrollState The state for vertical scrolling.
 * @param horizontalScrollState The state for horizontal scrolling.
 * @param sourceCodeFocusRequester The requester for focus on source code.
 * @param diagnostics The list of diagnostic descriptors used for error highlighting, tooltip and popup.
 * @param externalScrollToFlow The shared mutable state flow to monitor for external scroll changes.
 * @param externalTextFieldChanges The shared mutable state flow to monitor for external changes in text field.
 * @param showFindAndReplaceState The mutable state for tracking the visibility of Find&Replace UI component.
 * @param findAndReplaceMutableState The mutable state for tracking the state of Find&Replace UI component.
 * @param visualSettings The settings for configuring the visual appearance of the Kotlin Source Editor.
 *
 */
@Composable
public fun KotlinSourceEditor(
    textFieldValue: TextFieldValue,
    onTextFieldValueChange: (TextFieldValue) -> Unit,
    colorScheme: KotlinColorScheme,
    sourceEditorFeaturesConfiguration: KotlinSourceEditorFeaturesConfiguration,
    snackbarHostState: SnackbarHostState?,
    modifier: Modifier = Modifier,
    plainSourceEditorModifier: Modifier = Modifier,
    basicTextFieldModifier: Modifier = Modifier,
    verticalScrollState: ScrollState = rememberScrollState(),
    horizontalScrollState: ScrollState = rememberScrollState(),
    sourceCodeFocusRequester: FocusRequester = remember { FocusRequester() },
    diagnostics: List<DiagnosticDescriptor> = emptyList(),
    externalScrollToFlow: MutableSharedFlow<SourceCodePosition> = remember { MutableSharedFlow() },
    externalTextFieldChanges: MutableSharedFlow<TextFieldValue> = remember { MutableSharedFlow() },
    showFindAndReplaceState: MutableState<Boolean> = remember { mutableStateOf(false) },
    findAndReplaceMutableState: MutableState<FindAndReplaceState> = remember {
        mutableStateOf(FindAndReplaceState())
    },
    visualSettings: KotlinSourceEditorSettings = KotlinSourceEditorSettings(),
) {
    var codeTextFieldState: BasicSourceCodeTextFieldState<KotlinComposeToken> by remember(
        colorScheme, sourceEditorFeaturesConfiguration
    ) {
        mutableStateOf(
            createKotlinSourceCodeTextFieldStateIgnoringKeyboardEvents(
                textFieldValue, colorScheme, sourceEditorFeaturesConfiguration
            )
        )
    }
    LaunchedEffect(textFieldValue) {
        val currentTextFieldValue = codeTextFieldState.toTextFieldValue()
        if (currentTextFieldValue != textFieldValue) {
            codeTextFieldState = createKotlinSourceCodeTextFieldStateIgnoringKeyboardEvents(
                textFieldValue, colorScheme, sourceEditorFeaturesConfiguration
            )
        }
    }

    KotlinSourceEditor(
        codeTextFieldState = codeTextFieldState,
        onCodeTextFieldStateChange = {
            codeTextFieldState = it
            onTextFieldValueChange(it.toTextFieldValue())
        },
        colorScheme = colorScheme,
        sourceEditorFeaturesConfiguration = sourceEditorFeaturesConfiguration,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        plainSourceEditorModifier = plainSourceEditorModifier,
        basicTextFieldModifier = basicTextFieldModifier,
        verticalScrollState = verticalScrollState,
        horizontalScrollState = horizontalScrollState,
        sourceCodeFocusRequester = sourceCodeFocusRequester,
        diagnostics = diagnostics,
        externalScrollToFlow = externalScrollToFlow,
        externalTextFieldChanges = externalTextFieldChanges,
        showFindAndReplaceState = showFindAndReplaceState,
        findAndReplaceMutableState = findAndReplaceMutableState,
        visualSettings = visualSettings,
    )
}

private fun createKotlinSourceCodeTextFieldStateIgnoringKeyboardEvents(
    textFieldState: TextFieldValue,
    colorScheme: KotlinColorScheme,
    sourceEditorFeaturesConfiguration: KotlinSourceEditorFeaturesConfiguration,
): BasicSourceCodeTextFieldState<KotlinComposeToken> = initializeBasicSourceCodeTextFieldState(
    textFieldState = textFieldState,
    preprocessors = preprocessors,
    tokenize = {
        tokenizationPipeline(
            textFieldState = it,
            colorScheme = colorScheme,
            sourceEditorFeaturesConfiguration = sourceEditorFeaturesConfiguration,
            originalTokenizer = ::tokenizeKotlin,
            bracketMatcher = ::matchBrackets,
        )
    },
    charEventHandler = { null },
)

private val preprocessors: List<Preprocessor> = listOf { replaceTabs(it) }


/**
 * Kotlin Source Editor main entry point
 *
 * @param codeTextFieldState The state of the source code text field.
 * @param onCodeTextFieldStateChange The updater for the source code text field state.
 * @param colorScheme The color scheme used to configure the visual appearance of the source editor.
 * @param sourceEditorFeaturesConfiguration The configuration of features in the Kotlin source editor.
 * @param snackbarHostState The state for displaying snackbars.
 * @param modifier The modifier to be applied to the whole source editor.
 * @param plainSourceEditorModifier The modifier for the plain source editor which includes the underlying [TextField] and line numbers.
 * @param basicTextFieldModifier The modifier for the underlying [TextField].
 * @param verticalScrollState The state for vertical scrolling.
 * @param horizontalScrollState The state for horizontal scrolling.
 * @param sourceCodeFocusRequester The requester for focus on source code.
 * @param diagnostics The list of diagnostic descriptors used for error highlighting, tooltip and popup.
 * @param externalScrollToFlow The shared mutable state flow to monitor for external scroll changes.
 * @param externalTextFieldChanges The shared mutable state flow to monitor for external changes in text field.
 * @param showFindAndReplaceState The mutable state for tracking the visibility of Find&Replace UI component.
 * @param findAndReplaceMutableState The mutable state for tracking the state of Find&Replace UI component.
 * @param visualSettings The settings for configuring the visual appearance of the Kotlin Source Editor.
 *
 */
@Composable
public fun KotlinSourceEditor(
    codeTextFieldState: BasicSourceCodeTextFieldState<KotlinComposeToken>,
    onCodeTextFieldStateChange: (BasicSourceCodeTextFieldState<KotlinComposeToken>) -> Unit,
    colorScheme: KotlinColorScheme,
    sourceEditorFeaturesConfiguration: KotlinSourceEditorFeaturesConfiguration,
    snackbarHostState: SnackbarHostState?,
    modifier: Modifier = Modifier,
    plainSourceEditorModifier: Modifier = Modifier,
    basicTextFieldModifier: Modifier = Modifier,
    verticalScrollState: ScrollState = rememberScrollState(),
    horizontalScrollState: ScrollState = rememberScrollState(),
    sourceCodeFocusRequester: FocusRequester = remember { FocusRequester() },
    diagnostics: List<DiagnosticDescriptor> = emptyList(),
    externalScrollToFlow: MutableSharedFlow<SourceCodePosition> = remember { MutableSharedFlow() },
    externalTextFieldChanges: MutableSharedFlow<TextFieldValue> = remember { MutableSharedFlow() },
    showFindAndReplaceState: MutableState<Boolean> = remember { mutableStateOf(false) },
    findAndReplaceMutableState: MutableState<FindAndReplaceState> = remember {
        mutableStateOf(FindAndReplaceState())
    },
    visualSettings: KotlinSourceEditorSettings = KotlinSourceEditorSettings(),
) {
    val matchBrackets = remember {
        reuseResult<List<Token>, _> { matchBrackets<SingleStyleTokenChangingScope>(it) }
    }
    val originalTokenizer = remember { reuseResult(::tokenizeKotlin) }
    val matchedBrackets = matchBrackets(codeTextFieldState.tokens)
    val indentationLines by lazy {
        getIndentationLines(codeTextFieldState, matchedBrackets, false)
    }
    val coroutineScope = rememberCoroutineScope()
    val showLineNumbers = sourceEditorFeaturesConfiguration.showLineNumbers
    val pinLines = sourceEditorFeaturesConfiguration.showStickyHeader
    val showIndentation = sourceEditorFeaturesConfiguration.showIndentation
    val textSize = measureText(visualSettings.sourceTextStyle)
    val density = LocalDensity.current
    val pinLinesChooser: (SingleStyleTokenChangingScope) -> IntRange? = { bracket ->
        choosePinnedLines(bracket, codeTextFieldState, matchedBrackets)
    }
    var maximumPinnedLinesHeight: Dp by remember { mutableStateOf(0.dp) }

    val (findAndReplaceState, onFindAndReplaceStateChange) = findAndReplaceMutableState
    var showFindAndReplace by showFindAndReplaceState
    val (foundMatches, onFoundMatches) = remember(codeTextFieldState.text) {
        mutableStateOf<FoundMatches?>(null)
    }
    var scrollToFound by remember { mutableStateOf(Any()) }

    LaunchedEffect(scrollToFound) {
        if (foundMatches != null) {
            val range = foundMatches.matches[foundMatches.index].range
            externalScrollToFlow.emit(codeTextFieldState.sourceCodePositions[range.first])
        }
    }
    val evaluateCurrentDiagnostics = remember(diagnostics, codeTextFieldState.text) {
        reuseResult { position: SourceCodePosition ->
            if (
                position.line !in codeTextFieldState.offsets.indices ||
                position.column !in codeTextFieldState.offsets[position.line].indices
            ) {
                return@reuseResult emptyList()
            }
            val textPosition = TextInterval.TextPosition(position.line, position.column)
            diagnostics
                .filter { it.interval != null && textPosition in it.interval.start..it.interval.end }
                .sortedWith(diagnosticsComparator)
        }
    }
    var cursorDiagnostics by remember(codeTextFieldState) { mutableStateOf(listOf<DiagnosticDescriptor>()) }
    var tooltipDiagnostics by remember { mutableStateOf(listOf<DiagnosticDescriptor>()) }
    var currentPointerOffset by remember { mutableStateOf(IntOffset(0, 0)) }
    var tooltipPointerOffset by remember { mutableStateOf(IntOffset(0, 0)) }
    LaunchedEffect(cursorDiagnostics) {
        val cursorDiagnosticsSet = cursorDiagnostics.toSet()
        if (tooltipDiagnostics.any { it !in cursorDiagnosticsSet }) {
            tooltipDiagnostics = emptyList()
        }
        delay(if (cursorDiagnostics.isEmpty()) visualSettings.diagnosticsTooltipSettings.appearanceDelay else visualSettings.diagnosticsTooltipSettings.updateDelay)
        tooltipDiagnostics = cursorDiagnostics
        tooltipPointerOffset = currentPointerOffset
    }

    Column(modifier = modifier.background(colorScheme.backgroundColor)) {
        AnimatedVisibility(showFindAndReplace) {
            FindAndReplacePopup(
                popupState = findAndReplaceState,
                codeTextFieldState = codeTextFieldState,
                foundMatches = foundMatches,
                onFoundMatches = onFoundMatches,
                scrollToSelected = { scrollToFound = Any() },
                onPopupStateChange = onFindAndReplaceStateChange,
                onPatternError = {
                    if (snackbarHostState != null) coroutineScope.launch {
                        snackbarHostState.showSnackbar(it, withDismissAction = true)
                    }
                },
                sourceCodeFocusRequester = sourceCodeFocusRequester,
                colorScheme = colorScheme.findAndReplaceColorScheme,
                onClose = {
                    showFindAndReplace = false
                    onFoundMatches(null)
                },
                onReplaced = { coroutineScope.launch { externalTextFieldChanges.emit(it) } },
                onKeyEvents = { event ->
                    listOfNotNull(
                        takeIf(sourceEditorFeaturesConfiguration.enableFindAndReplace) {
                            findAndReplaceSwitcher(
                                currentPopupState = findAndReplaceState,
                                onShowPopup = onFindAndReplaceStateChange,
                                findKeyEventFilter = visualSettings.keyBindings.find.toKeyEventFilter(),
                                replaceKeyEventFilter = visualSettings.keyBindings.replace.toKeyEventFilter(),
                            )
                        },
                    ).any { it(event) }
                },
                settings = visualSettings.findAndReplaceSettings,
                exitKeyEvent = visualSettings.keyBindings.exit.toKeyEventFilter(),
            )
        }
        val tooltipShape = visualSettings.diagnosticsTooltipSettings.shape

        var cancelTooltip by remember { mutableStateOf<Job?>(null) }
        fun onEnter() {
            cancelTooltip?.cancel()
            cancelTooltip = null
        }

        fun onExit() {
            if (cancelTooltip == null) {
                cancelTooltip = coroutineScope.launch {
                    delay(visualSettings.diagnosticsTooltipSettings.disappearanceDelay)
                    cursorDiagnostics = emptyList()
                    cancelTooltip = null
                }
            }
        }
        TooltipBox(
            tooltip = {
                AnimatedVisibility(sourceEditorFeaturesConfiguration.enableDiagnosticsTooltip) {
                    Box(
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Text)
                            .onPointerEvent(PointerEventType.Enter) { onEnter() }
                            .onPointerEvent(PointerEventType.Exit) { onExit() }
                            .padding(visualSettings.diagnosticsTooltipSettings.hitBoxPadding)
                    ) {
                        Tooltip(
                            diagnostics = tooltipDiagnostics,
                            colorScheme = colorScheme.tooltipsColorScheme,
                            shape = tooltipShape,
                            scrollbarsVisibility = visualSettings.scrollbarsVisibility,
                            settings = visualSettings.diagnosticsTooltipSettings,
                            modifier = Modifier
                                .pointerHoverIcon(PointerIcon.Default)
                                .shadow(
                                    elevation = visualSettings.diagnosticsTooltipSettings.elevation,
                                    shape = tooltipShape,
                                    ambientColor = colorScheme.tooltipsColorScheme.shadowColor,
                                    spotColor = colorScheme.tooltipsColorScheme.shadowColor,
                                ),
                        )
                    }
                }
            },
            delayMillis = 0, // because it is global TextField delay
            offset = tooltipPointerOffset,
            modifier = plainSourceEditorModifier.onPointerEvent(PointerEventType.Move) {
                val newOffset =
                    it.changes.first().position.let { (x, y) -> IntOffset(x.toInt(), y.toInt()) }
                currentPointerOffset = newOffset
            },
        ) {
            BasicSourceCodeTextField(
                state = codeTextFieldState,
                onStateUpdate = onCodeTextFieldStateChange,
                preprocessors = preprocessors,
                externalTextFieldChanges = externalTextFieldChanges,
                tokenize = {
                    tokenizationPipeline(
                        textFieldState = it,
                        colorScheme = colorScheme,
                        sourceEditorFeaturesConfiguration = sourceEditorFeaturesConfiguration,
                        originalTokenizer = originalTokenizer,
                        bracketMatcher = matchBrackets
                    )
                },
                basicTextFieldModifier = basicTextFieldModifier
                    .focusRequester(sourceCodeFocusRequester)
                    .onPointerEvent(PointerEventType.Enter) { onEnter() }
                    .onPointerEvent(PointerEventType.Exit) { onExit() },
                visualTransformation = foundMatches?.toVisualTransformation(
                    foundColor = colorScheme.findAndReplaceColorScheme.foundMatchColor,
                    currentFoundColor = colorScheme.findAndReplaceColorScheme.currentFoundMatchColor,
                ) ?: VisualTransformation.None,
                additionalInnerComposable = { _, _ ->
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showIndentation,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        IndentationLines(
                            indentationLines = indentationLines,
                            modifier = Modifier.background(color = colorScheme.indentationColor),
                            textStyle = visualSettings.sourceTextStyle,
                            width = visualSettings.indentationLinesSettings.thickness,
                        )
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = sourceEditorFeaturesConfiguration.highlightDiagnostics,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        CompilerDiagnostics(
                            codeTextFieldState = codeTextFieldState,
                            diagnostics = diagnostics,
                            colorScheme = colorScheme.diagnosticsColorScheme,
                            textSize = textSize,
                            settings = visualSettings.diagnosticsHighlightingSettings,
                        )
                    }
                },
                manualScrollToPosition = externalScrollToFlow,
                lineNumbersColor = colorScheme.lineNumbersColor,
                additionalOuterComposable = { _, inner ->
                    inner()

                    androidx.compose.animation.AnimatedVisibility(
                        visible = pinLines,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        PinnedLines(
                            state = codeTextFieldState,
                            textStyle = visualSettings.sourceTextStyle,
                            scrollState = verticalScrollState,
                            showLineNumbers = showLineNumbers,
                            matchedBrackets = matchedBrackets,
                            pinLinesChooser = pinLinesChooser,
                            lineNumbersColor = colorScheme.lineNumbersColor,
                            maximumPinnedLinesHeight = (visualSettings.stickyHeaderSettings.maximumHeightRatio * maxHeight).also {
                                maximumPinnedLinesHeight = it
                            },
                            onClick = {
                                coroutineScope.launch {
                                    externalScrollToFlow.emit(SourceCodePosition(it, 0))
                                }
                            },
                            backgroundColor = colorScheme.backgroundColor,
                            additionalInnerComposable = { linesToWrite, _ ->
                                val lineMapping = linesToWrite.keys.withIndex()
                                    .associate { (index, line) -> line to index }
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = showIndentation,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                ) {
                                    IndentationLines(
                                        indentationLines = indentationLines,
                                        modifier = Modifier.background(color = colorScheme.indentationColor),
                                        textStyle = visualSettings.sourceTextStyle,
                                        mapLineNumbers = lineMapping::get,
                                        width = visualSettings.indentationLinesSettings.thickness,
                                    )
                                }
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = sourceEditorFeaturesConfiguration.highlightDiagnostics,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                ) {
                                    CompilerDiagnostics(
                                        codeTextFieldState = codeTextFieldState,
                                        diagnostics = diagnostics,
                                        colorScheme = colorScheme.diagnosticsColorScheme,
                                        textSize = textSize,
                                        mapLineNumbers = lineMapping::get,
                                        settings = visualSettings.diagnosticsHighlightingSettings,
                                    )
                                }
                            },
                            onHoveredSourceCodePositionChange = { sourceCodePosition: SourceCodePosition ->
                                cursorDiagnostics = evaluateCurrentDiagnostics(sourceCodePosition)
                            },
                            divider = {
                                HorizontalDivider(
                                    color = colorScheme.stickyHeaderSeparatorColor,
                                    thickness = visualSettings.stickyHeaderSettings.separatorThickness,
                                )
                            },
                        )
                    }
                    val scrollbarThickness = visualSettings.scrollbarsThickness
                    val verticalScrollbarThicknessAnimated by animateIntAsState(
                        if (visualSettings.scrollbarsVisibility.showVertical && verticalScrollState.maxValue > 0) {
                            density.run { scrollbarThickness.toPx().roundToInt() }
                        } else {
                            0
                        }
                    )
                    androidx.compose.animation.AnimatedVisibility(
                        visible = sourceEditorFeaturesConfiguration.enableDiagnosticsPopup,
                        modifier = Modifier.offset {
                            IntOffset(-verticalScrollbarThicknessAnimated, 0)
                        }.align(Alignment.TopEnd),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        DiagnosticsPopup(
                            diagnostics = diagnostics,
                            colorScheme = colorScheme.diagnosticsPopupColorScheme,
                            settings = visualSettings.diagnosticsPopupSettings,
                            onCopied = {
                                if (snackbarHostState != null) {
                                    coroutineScope.launch {
                                        val message = visualSettings
                                            .diagnosticsPopupSettings
                                            .diagnosticsListSettings
                                            .copiedDiagnosticContentNotificationMessage
                                        snackbarHostState.showSnackbar(message, withDismissAction = true)
                                    }
                                }
                            },
                        ) {
                            val offset = codeTextFieldState.offsets
                                .getOrNull(it.line)
                                ?.getOrNull(it.column)
                            if (offset != null) {
                                val newCodeTextFieldState = codeTextFieldState.copy(
                                    selection = TextRange(offset)
                                )
                                onCodeTextFieldStateChange(newCodeTextFieldState)
                            }
                            sourceCodeFocusRequester.requestFocus()
                        }
                    }
                    val lineNumbersWidth =
                        if (showLineNumbers) density.run { (codeTextFieldState.offsets.size.toString().length * textSize.width).toDp() }
                        else 0.dp
                    val lineNumbersWidthAnimated by animateDpAsState(lineNumbersWidth)
                    val horizontalScrollBarEndOffset by animateDpAsState(
                        if (visualSettings.scrollbarsVisibility == ScrollbarsVisibility.Both && verticalScrollState.maxValue > 0)
                            scrollbarThickness
                        else
                            0.dp
                    )
                    androidx.compose.animation.AnimatedVisibility(
                        visible = horizontalScrollState.showScrollbar && visualSettings.scrollbarsVisibility.showHorizontal,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(
                            start = lineNumbersWidthAnimated,
                            end = horizontalScrollBarEndOffset,
                        ),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        HorizontalScrollbar(
                            horizontalScrollState,
                            hoverColor = colorScheme.scrollbarsColorScheme.hoveredColor,
                            unhoverColor = colorScheme.scrollbarsColorScheme.notHoveredColor,
                        )
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = visualSettings.scrollbarsVisibility.showVertical,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.align(Alignment.CenterEnd),
                    ) {
                        VerticalScrollbar(
                            verticalScrollState,
                            hoverColor = colorScheme.scrollbarsColorScheme.hoveredColor,
                            unhoverColor = colorScheme.scrollbarsColorScheme.notHoveredColor,
                        )
                    }
                },
                showLineNumbers = showLineNumbers,
                textStyle = visualSettings.sourceTextStyle,
                verticalScrollState = verticalScrollState,
                horizontalScrollState = horizontalScrollState,
                modifier = Modifier.background(color = colorScheme.backgroundColor),
                editorOffsetsForPosition = {
                    EditorOffsets(
                        top = getOffsetForLineToAppearOnTop(
                            line = it.line,
                            textSize = textSize,
                            density = density,
                            state = codeTextFieldState,
                            matchedBrackets = matchedBrackets,
                            dividerThickness = 0.dp, // do not include divider thickness in the calculation
                            maximumPinnedLinesHeight = maximumPinnedLinesHeight,
                            pinLinesChooser = pinLinesChooser,
                        )
                    )
                },
                keyEventHandler = combineKeyEventHandlers(
                    takeIf(sourceEditorFeaturesConfiguration.indentCode) {
                        handleMovingOffsets(
                            state = codeTextFieldState,
                            moveForwardFilter = visualSettings.keyBindings.moveOffsetForward.toKeyEventFilter(),
                            moveBackwardFilter = visualSettings.keyBindings.moveOffsetBackward.toKeyEventFilter(),
                        )
                    },
                    takeIf(sourceEditorFeaturesConfiguration.duplicateStringsAndLines) {
                        handleDuplicateLinesAndStrings(
                            state = codeTextFieldState,
                            keyEventFilter = visualSettings.keyBindings.duplicateStringsAndLines.toKeyEventFilter(),
                        )
                    },
                    takeIf(sourceEditorFeaturesConfiguration.enableFindAndReplace) {
                        findAndReplace(
                            state = codeTextFieldState,
                            currentPopupState = findAndReplaceState,
                            onShowPopup = {
                                showFindAndReplace =
                                    if (showFindAndReplace && it == findAndReplaceState) {
                                        false
                                    } else {
                                        onFindAndReplaceStateChange(it)
                                        true
                                    }
                            },
                            findKeyEventFilter = visualSettings.keyBindings.find.toKeyEventFilter(),
                            replaceKeyEventFilter = visualSettings.keyBindings.replace.toKeyEventFilter(),
                        )
                    },
                ),
                charEventHandler = makeCharEventHandler(
                    sourceEditorFeaturesConfiguration,
                    codeTextFieldState,
                    matchedBrackets,
                    visualSettings.keyBindings,
                ),
                cursorBrush = SolidColor(colorScheme.cursorColor),
                horizontalThresholdEdgeChars = visualSettings.horizontalThresholdEdgeChars,
                verticalThresholdEdgeLines = visualSettings.verticalThresholdEdgeLines,
                onHoveredSourceCodePositionChange = { sourceCodePosition: SourceCodePosition ->
                    cursorDiagnostics = evaluateCurrentDiagnostics(sourceCodePosition)
                },
            )
        }
    }
}

private fun makeCharEventHandler(
    sourceEditorFeaturesConfiguration: KotlinSourceEditorFeaturesConfiguration,
    codeTextFieldState: BasicSourceCodeTextFieldState<KotlinComposeToken>,
    matchedBrackets: Map<SingleStyleTokenChangingScope, SingleStyleTokenChangingScope>,
    keyBindings: KeyBindings
) = combineCharEventHandlers(
    takeIf(sourceEditorFeaturesConfiguration.reuseFollowingClosingBracket) {
        reusingCharsEventHandler(
            textFieldState = codeTextFieldState,
            chars = "])>}",
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.reuseClosingCharQuote) {
        reusingCharsEventHandler(
            textFieldState = codeTextFieldState,
            chars = "'",
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.reuseClosingStringQuote) {
        reusingCharsEventHandler(
            textFieldState = codeTextFieldState,
            chars = "\"",
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.addClosingBracket) {
        openingBracketCharEventHandler(
            textFieldState = codeTextFieldState,
            openingChar = '[',
            openingBracket = "[",
            closingBracket = "]",
            addNewLinesForSelection = { false },
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.indentClosingBracket) {
        closingBracketCharEventHandler(
            textFieldState = codeTextFieldState,
            openingBracket = "[",
            closingBracket = "]",
            closingChar = ']',
            matchedBrackets = matchedBrackets,
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.addClosingBracket) {
        openingBracketCharEventHandler(
            textFieldState = codeTextFieldState,
            openingChar = '(',
            openingBracket = "(",
            closingBracket = ")",
            addNewLinesForSelection = { false },
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.indentClosingBracket) {
        closingBracketCharEventHandler(
            textFieldState = codeTextFieldState,
            openingBracket = "(",
            closingBracket = ")",
            closingChar = ')',
            matchedBrackets = matchedBrackets,
        )
    },
    /*takeIf(configuration.addClosingBracket) {
        openingBracketCharEventHandler(
            textFieldState = codeTextFieldState,
            openingChar = '<',
            openingBracket = "<",
            closingBracket = ">",
            addNewLinesForSelection = { false },
        )
    },
    takeIf(configuration.indentClosingBracket) {
        closingBracketCharEventHandler(
            textFieldState = codeTextFieldState,
            openingBracket = "<",
            closingBracket = ">",
            closingChar = '>',
            matchedBrackets = matchedBrackets,
        )
    },*/ // 2 < 3; 3 > 2
    takeIf(sourceEditorFeaturesConfiguration.addClosingBracket) {
        openingBracketCharEventHandler(
            textFieldState = codeTextFieldState,
            openingChar = '{',
            openingBracket = "{",
            closingBracket = "}",
            addNewLinesForSelection = { true },
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.indentClosingBracket) {
        closingBracketCharEventHandler(
            textFieldState = codeTextFieldState,
            openingBracket = "{",
            closingBracket = "}",
            closingChar = '}',
            matchedBrackets = matchedBrackets,
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.addClosingCharQuote) {
        openingBracketCharEventHandler(
            textFieldState = codeTextFieldState,
            openingChar = '\'',
            openingBracket = "'",
            closingBracket = "'",
            addNewLinesForSelection = { false },
            indent = null,
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.addClosingMultiLineStringQuote) {
        openingMultiLineStringQuoteCharHandler(
            textFieldState = codeTextFieldState,
            indent = null,
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.addClosingSingleLineStringQuote) {
        openingBracketCharEventHandler(
            textFieldState = codeTextFieldState,
            openingChar = '"',
            openingBracket = "\"",
            closingBracket = "\"",
            addNewLinesForSelection = { false },
            indent = null,
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.indentNewLine) {
        newLineCharEventHandler(
            textFieldState = codeTextFieldState,
            matchedBrackets = matchedBrackets,
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.removeIndentOnBackspace) {
        removeIndentBackspaceCharEventHandler(
            textFieldState = codeTextFieldState,
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.removeFollowingClosingBracket) {
        removeEmptyBracesBackspaceCharEventHandler(
            textFieldState = codeTextFieldState,
            openingBracket = "[",
            closingBracket = "]",
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.removeFollowingClosingBracket) {
        removeEmptyBracesBackspaceCharEventHandler(
            textFieldState = codeTextFieldState,
            openingBracket = "(",
            closingBracket = ")",
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.removeFollowingClosingBracket) {
        removeEmptyBracesBackspaceCharEventHandler(
            textFieldState = codeTextFieldState,
            openingBracket = "<",
            closingBracket = ">",
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.removeFollowingClosingBracket) {
        removeEmptyBracesBackspaceCharEventHandler(
            textFieldState = codeTextFieldState,
            openingBracket = "{",
            closingBracket = "}",
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.removeFollowingClosingCharQuote) {
        removeEmptyBracesBackspaceCharEventHandler(
            textFieldState = codeTextFieldState,
            openingBracket = "'",
            closingBracket = "'",
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.removeFollowingClosingStringQuote) {
        removeEmptyBracesBackspaceCharEventHandler(
            textFieldState = codeTextFieldState,
            openingBracket = "\"",
            closingBracket = "\"",
        )
    },
    takeIf(sourceEditorFeaturesConfiguration.commentBlock) {
        commentBlock(
            state = codeTextFieldState,
            char = keyBindings.commentChar,
        )
    },
)

private val diagnosticsComparator = compareBy<DiagnosticDescriptor> { it.severity }
    .thenBy { it.interval!!.start }
    .thenBy { it.interval!!.end }

@Composable
private fun BoxScope.CompilerDiagnostics(
    codeTextFieldState: BasicSourceCodeTextFieldState<KotlinComposeToken>,
    diagnostics: List<DiagnosticDescriptor>,
    colorScheme: DiagnosticSeverityColorScheme,
    textSize: Size,
    mapLineNumbers: (Int) -> Int? = { it },
    settings: DiagnosticsHighlightingSettings,
) {
    if (diagnostics.isEmpty()) return
    val sortedDiagnostics = diagnostics
        .filter { it.interval != null }
        .sortedWith(diagnosticsComparator.reversed())
    for (diagnostic in sortedDiagnostics) {
        CompilerDiagnostic(codeTextFieldState, diagnostic, colorScheme, textSize, mapLineNumbers, settings)
    }
}

private val ScrollState.showScrollbar get() = maxValue in 1..<Int.MAX_VALUE

@Composable
private fun Tooltip(
    diagnostics: List<DiagnosticDescriptor>,
    colorScheme: TooltipsColorScheme,
    shape: Shape,
    scrollbarsVisibility: ScrollbarsVisibility,
    settings: DiagnosticsTooltipSettings,
    modifier: Modifier = Modifier,
) {
    if (diagnostics.isNotEmpty()) {
        Surface(
            modifier = modifier,
            color = colorScheme.backgroundColor,
            shape = shape,
        ) {
            BoxWithConstraints {
                val verticalScrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .widthIn(max = settings.maximumWidthRatio * maxWidth)
                        .verticalScroll(verticalScrollState)
                        .padding(settings.paddng)
                        .animateContentSize(spring(stiffness = Spring.StiffnessHigh))
                ) {
                    var maxRowWidth by remember { mutableStateOf(0) }
                    for ((index, diagnostic) in diagnostics.withIndex()) {
                        if (index > 0) {
                            HorizontalDivider(
                                thickness = settings.separatorThickness,
                                color = colorScheme.separatorColor,
                                modifier = Modifier
                                    .padding(settings.separatorPadding)
                                    .width(LocalDensity.current.run { maxRowWidth.toDp() })
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.onSizeChanged {
                                if (it.width > maxRowWidth) maxRowWidth = it.width
                            }
                        ) {
                            diagnostic.severity.icon(
                                colorScheme = colorScheme.iconColorScheme,
                                contentDescription = null,
                                modifier = Modifier.size(settings.severityIconSize),
                            )
                            Spacer(Modifier.width(settings.spaceBetweenSeverityIconAndMessage))
                            SelectionContainer {
                                Text(
                                    text = diagnostic.message,
                                    color = colorScheme.textColor,
                                    modifier = Modifier.weight(1f),
                                    style = settings.labelTextStyle,
                                )
                            }
                        }
                    }
                }
                AnimatedVisibility(
                    visible = verticalScrollState.showScrollbar && scrollbarsVisibility.showVertical,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    VerticalScrollbar(
                        scrollState = verticalScrollState,
                        hoverColor = colorScheme.scrollbarsColorScheme.hoveredColor,
                        unhoverColor = colorScheme.scrollbarsColorScheme.notHoveredColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxScope.CompilerDiagnostic(
    codeTextFieldState: BasicSourceCodeTextFieldState<KotlinComposeToken>,
    diagnosticDescriptor: DiagnosticDescriptor,
    colorScheme: DiagnosticSeverityColorScheme,
    textSize: Size,
    mapLineNumbers: (Int) -> Int? = { it },
    settings: DiagnosticsHighlightingSettings,
) {
    val (start, end) = diagnosticDescriptor.interval ?: return
    if (start.line > end.line) return
    if (start.line == end.line && start.ch > end.ch) return

    for (line in start.line..end.line) {
        if (line !in codeTextFieldState.offsets.indices) break
        val actualLineIndex = mapLineNumbers(line) ?: continue
        val lineIndices = codeTextFieldState.offsets[line].indices
        val lineStart = (if (line == start.line) start.ch else 0).coerceIn(lineIndices)
        val lineEnd = (if (line == end.line) end.ch else Int.MAX_VALUE).coerceIn(lineIndices)
            .let { if (it <= lineStart) lineStart + 1 else it }
        val color = diagnosticDescriptor.severity.getColor(colorScheme)
        if (color.isUnspecified) break
        with(LocalDensity.current) {
            val height = settings.thickness
            Spacer(
                Modifier
                    .offset(
                        x = (lineStart * textSize.width).toDp(),
                        y = ((actualLineIndex + 1) * textSize.height).toDp() - height,
                    )
                    .height(height)
                    .background(color, RoundedCornerShape(height))
                    .width(((lineEnd - lineStart) * textSize.width).toDp())
            )
        }
    }
}

private fun choosePinnedLines(
    bracket: SingleStyleTokenChangingScope,
    codeTextFieldState: BasicSourceCodeTextFieldState<KotlinComposeToken>,
    matchedBrackets: Map<SingleStyleTokenChangingScope, SingleStyleTokenChangingScope>,
): IntRange? {
    val openingBracket = when (bracket) {
        is KotlinComposeToken.Operator.OpeningBrace -> bracket
        is KotlinComposeToken.Operator.ClosingBrace -> matchedBrackets[bracket]
        is KotlinComposeToken.Operator.OpeningParenthesis -> bracket
        is KotlinComposeToken.Operator.ClosingParenthesis -> matchedBrackets[bracket]
        else -> null
    } ?: return null
    openingBracket as KotlinComposeToken
    val openingTokenIndex = codeTextFieldState.tokens
        .binarySearchBy(codeTextFieldState.tokenOffsets[openingBracket]!!.first) {
            codeTextFieldState.tokenOffsets[it]!!.first
        }
    val openingTokenLine = codeTextFieldState.tokenPositions[openingBracket]!!.first.line
    val tokensBeforeCurrentOnLine = generateSequence(openingTokenIndex) { it - 1 }
        .takeWhile { it >= 0 }
        .map { codeTextFieldState.tokens[it] }
        .takeWhile { codeTextFieldState.tokenPositions[it]!!.first.line == openingTokenLine }
    if (tokensBeforeCurrentOnLine.any { it is KotlinComposeToken.Identifier }) {
        return when (bracket) {
            is KotlinComposeToken.Operator.OpeningBrace -> codeTextFieldState.tokenLines[bracket]
            is KotlinComposeToken.Operator.ClosingBrace -> codeTextFieldState.tokenLines[bracket]
            is KotlinComposeToken.Operator.OpeningParenthesis -> codeTextFieldState.tokenLines[bracket]
            is KotlinComposeToken.Operator.ClosingParenthesis -> codeTextFieldState.tokenLines[bracket]
            else -> null
        }
    } else if (openingBracket is KotlinComposeToken.Operator.OpeningBrace) {
        val closingParenthesis = tokensBeforeCurrentOnLine
            .drop(1)
            .filterIsInstance<KotlinComposeToken.Operator.ClosingParenthesis>()
            .firstOrNull() ?: return null
        val openingParenthesis = matchedBrackets[closingParenthesis] ?: return null
        val recursiveParenthesis =
            if (bracket is KotlinComposeToken.Operator.OpeningBrace) openingParenthesis
            else closingParenthesis
        return choosePinnedLines(recursiveParenthesis, codeTextFieldState, matchedBrackets)
    }
    return null
}

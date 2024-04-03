package kotlinlang.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.zhelenskiy.library.generated.resources.Res
import com.zhelenskiy.library.generated.resources.match_case
import com.zhelenskiy.library.generated.resources.regular_expression
import editor.basic.BasicSourceCodeTextFieldState
import editor.basic.KeyEventFilter
import editor.basic.KeyEventHandler
import kotlinlang.utils.size
import kotlinx.coroutines.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

public data class FindAndReplaceState(
    val findString: String = "",
    val matchCase: Boolean = false,
    val isFullWord: Boolean = false,
    val isRegex: Boolean = false,
    val replaceString: String = "",
    val showReplace: Boolean = false,
)

public data class FindAndReplaceSettings(
    val textFieldTextStyle: TextStyle,
    val labelTextStyle: TextStyle = TextStyle.Default,
    val smallIconButtonDiameter: Dp = 30.dp,
    val smallIconDiameter: Dp? = null,
    val compactModeThreshold: Dp = 550.dp,
    val superCompactModeThreshold: Dp = 380.dp,
    val padding: PaddingValues = PaddingValues(4.dp),
    val rowMinHeight: Dp = 48.dp,
    val rowMaxHeight: Dp = 100.dp,
    val spacerBetweenSearchModeIconButtons: Dp = 4.dp,
    val findReplaceFieldsSeparatorThickness: Dp = 1.dp,
    val spaceBetweenTextFieldsAndControlBlock: Dp = 4.dp,
    val spaceBetweenFindResultsAndReplaceButtonsRatio: Float = 0.7f,
    val findReplaceSeparatorThickness: Dp = 1.dp,
    val replaceButtonsMargin: PaddingValues = PaddingValues(horizontal = 4.dp),
    val replaceButtonShape: Shape = RoundedCornerShape(50, 0, 0, 50),
    val replaceAllButtonShape: Shape = RoundedCornerShape(0, 50, 50, 0),
    val spaceBetweenReplaceAndReplaceAllButtons: Dp = 1.dp,
    val findResultsHorizontalPadding: PaddingValues = PaddingValues(horizontal = 6.dp),
    val replaceButtonText: String = "Replace",
    val replaceAllButtonText: String = "Replace all",
    val findTextFieldButtonText: String = "Find",
    val replaceTextFieldButtonText: String = "Replace",
    val matchCaseButtonContentDescription: String = "Match case",
    val fullWordButtonContentDescription: String = "Full word",
    val regexButtonContentDescription: String = "Regex",
    val closeButtonContentDescription: String = "Close",
    val previousFoundButtonContentDescription: String = "Previous",
    val nextFoundButtonContentDescription: String = "Next",
    val hideReplaceButtonContentDescription: String = "Hide replace",
    val showReplaceButtonContentDescription: String = "Show replace",
    val defaultRegexErrorMessage: String = "A pattern error occurred",
    val badPatternMessage: String = "Bad pattern",
    val notFoundMessage: String = "Not found",
) {
    init {
        require(superCompactModeThreshold <= compactModeThreshold) {
            "Invalid thresholds: superCompactModeThreshold ($superCompactModeThreshold) must be compactModeThreshold ($compactModeThreshold)"
        }
        require(spaceBetweenFindResultsAndReplaceButtonsRatio >= 0f) {
            "Invalid spaceBetweenFindResultsAndReplaceButtonsRatio: $spaceBetweenFindResultsAndReplaceButtonsRatio"
        }
    }
}

@Composable
private fun SmallIconButton(
    onClick: () -> Unit,
    size: Dp,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.clip(CircleShape).clickable(enabled) { onClick() }.size(size),
    ) {
        content()
    }
}

internal data class FoundMatches(val matches: List<MatchResult>, val index: Int) {
    init {
        require(matches.isNotEmpty()) { "Matches must be empty" }
        require(index in matches.indices) { "$index is out of ${matches.indices}" }
    }

    override fun toString(): String =
        "FoundMatches(matches={${matches.joinToString { "${it.range}: ${it.value}" }}}, index=$index)"

}

internal fun FoundMatches.toVisualTransformation(
    foundColor: Color,
    currentFoundColor: Color
): VisualTransformation = VisualTransformation { old ->
    val newAnnotatedString = buildAnnotatedString {
        fun safeAppend(startIndex: Int, endIndex: Int) {
            if (startIndex in old.text.indices) {
                append(old, startIndex, minOf(old.text.length, endIndex))
            }
        }
        safeAppend(0, matches.first().range.first)
        for ((i, match) in matches.withIndex()) {
            withStyle(SpanStyle(background = if (i == index) currentFoundColor else foundColor)) {
                safeAppend(match.range.first, match.range.last + 1)
            }
            safeAppend(match.range.last + 1, matches.getOrNull(i + 1)?.range?.first ?: old.length)
        }
    }
    TransformedText(newAnnotatedString, OffsetMapping.Identity)
}

private fun getRegexReplacementStringErrorMessage(s: String) =
    runCatching { Regex("a").replace("a", s) }.exceptionOrNull()?.message

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun FindAndReplacePopup(
    popupState: FindAndReplaceState,
    codeTextFieldState: BasicSourceCodeTextFieldState<*>,
    foundMatches: FoundMatches?,
    onFoundMatches: (FoundMatches?) -> Unit,
    onPopupStateChange: (FindAndReplaceState) -> Unit,
    onPatternError: (String) -> Unit,
    scrollToSelected: () -> Unit,
    sourceCodeFocusRequester: FocusRequester,
    colorScheme: FindAndReplaceColorScheme,
    onKeyEvents: (KeyEvent) -> Boolean = { false },
    onClose: () -> Unit,
    onReplaced: (TextFieldValue) -> Unit,
    settings: FindAndReplaceSettings,
    exitKeyEvent: KeyEventFilter = KeyEventFilterHolder(key = Key.Escape).toKeyEventFilter(),
) {
    var lastErrorMessage by remember { mutableStateOf("") }
    var isFindRegexError by remember { mutableStateOf(false) }
    var isReplaceRegexError by remember { mutableStateOf(false) }

    LaunchedEffect(isFindRegexError, lastErrorMessage) {
        if (isFindRegexError) {
            onPatternError(lastErrorMessage)
        }
    }
    val (matchResults, onMatchedResultChange) = remember { mutableStateOf<List<MatchResult>?>(null) }
    LaunchedEffect(
        codeTextFieldState.text,
        popupState.findString,
        popupState.isRegex,
        popupState.isFullWord,
        popupState.matchCase,
    ) {
        if (popupState.findString.isEmpty()) {
            isFindRegexError = false
            onMatchedResultChange(null)
            return@LaunchedEffect
        }

        val regex = makeRegexSafely(popupState)
            .also { isFindRegexError = it.isFailure }
            .onFailure { lastErrorMessage = it.message ?: settings.defaultRegexErrorMessage }
            .getOrNull() ?: return@LaunchedEffect

        yield()

        onMatchedResultChange(
            withContext(Dispatchers.IO) {
                regex.findAll(codeTextFieldState.text).toList().also { yield() }
            }
        )
    }

    LaunchedEffect(popupState.isRegex, popupState.replaceString) {
        val errorMessage = if (popupState.isRegex) getRegexReplacementStringErrorMessage(popupState.replaceString) else null
        isReplaceRegexError = errorMessage != null
        if (errorMessage != null) onPatternError(errorMessage)
    }

    LaunchedEffect(matchResults, codeTextFieldState.selection) {
        if (matchResults.isNullOrEmpty()) {
            onFoundMatches(null)
        } else {
            val index = matchResults
                .indexOfFirst {
                    it.range.last + 1 >= codeTextFieldState.selection.max &&
                            it.range.first <= codeTextFieldState.selection.min
                }
                .takeIf { it >= 0 }
                ?: matchResults
                    .indexOfFirst { it.range.first >= codeTextFieldState.selection.min }
                    .takeIf { it >= 0 }
                ?: 0
            onFoundMatches(FoundMatches(matchResults, index))
        }
    }
    val findFocusRequester: FocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        findFocusRequester.requestFocus()
    }
    val arrowRotationAngle by animateFloatAsState(
        targetValue = if (popupState.showReplace) 0f else -90f,
    )
    val replaceFocusRequester = remember { FocusRequester() }
    val findColor by animateColorAsState(
        if (isFindRegexError) colorScheme.errorColor else colorScheme.textColor
    )
    val replaceColor by animateColorAsState(
        if (isReplaceRegexError) colorScheme.errorColor else colorScheme.textColor
    )

    Column {
        BoxWithConstraints(
            modifier = Modifier.background(colorScheme.backgroundColor).padding(settings.padding)
        ) {
            val useCompactMode = maxWidth < settings.compactModeThreshold
            val useExtremeCompactMode = maxWidth < settings.superCompactModeThreshold
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                    Box(
                        modifier = Modifier
                            .height(settings.rowMinHeight)
                            .padding(end = settings.padding.calculateStartPadding(LocalLayoutDirection.current)),
                        contentAlignment = Alignment.Center
                    ) {
                        SmallIconButton(
                            onClick = {
                                onPopupStateChange(popupState.copy(showReplace = !popupState.showReplace))
                            },
                            size = settings.smallIconButtonDiameter,
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = if (popupState.showReplace) settings.hideReplaceButtonContentDescription else settings.showReplaceButtonContentDescription,
                                modifier = Modifier.rotate(arrowRotationAngle).size(settings.smallIconDiameter),
                                tint = colorScheme.textColor,
                            )
                        }
                    }

                    fun Modifier.exitOnEscape() = onPreviewKeyEvent {
                        if (exitKeyEvent(it)) {
                            onClose()
                            sourceCodeFocusRequester.requestFocus()
                            true
                        } else {
                            false
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        BasicTextField(
                            value = popupState.findString,
                            onValueChange = { onPopupStateChange(popupState.copy(findString = it)); scrollToSelected() },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Ascii,
                                imeAction = if (popupState.showReplace) ImeAction.Next else ImeAction.Search
                            ),
                            cursorBrush = SolidColor(colorScheme.cursorColor),
                            keyboardActions = KeyboardActions {
                                if (popupState.showReplace) {
                                    replaceFocusRequester.requestFocus()
                                }
                            },
                            textStyle = settings.textFieldTextStyle.merge(color = findColor),
                            modifier = Modifier
                                .focusRequester(findFocusRequester)
                                .fillMaxWidth()
                                .exitOnEscape()
                                .onPreviewKeyEvent { onKeyEvents(it) },
                            decorationBox = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = settings.rowMinHeight, max = settings.rowMaxHeight)
                                ) {
                                    Box(Modifier.weight(1f), Alignment.CenterStart) {
                                        if (popupState.findString.isEmpty()) {
                                            Text(
                                                text = settings.findTextFieldButtonText,
                                                color = colorScheme.placeHolderTextColor,
                                                style = settings.labelTextStyle,
                                            )
                                        }
                                        it()
                                    }
                                    val caseColor by animateColorAsState(
                                        if (popupState.matchCase) colorScheme.selectedSearchModeColor
                                        else Color.Transparent
                                    )
                                    SmallIconButton(
                                        onClick = {
                                            onPopupStateChange(popupState.copy(matchCase = !popupState.matchCase))
                                            scrollToSelected()
                                            sourceCodeFocusRequester.requestFocus()
                                        },
                                        modifier = Modifier.background(caseColor, CircleShape),
                                        size = settings.smallIconButtonDiameter,
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.match_case),
                                            contentDescription = settings.matchCaseButtonContentDescription,
                                            tint = colorScheme.textColor,
                                            modifier = Modifier.size(settings.smallIconDiameter),
                                        )
                                    }

                                    Spacer(Modifier.width(settings.spacerBetweenSearchModeIconButtons))

                                    val fullWordColor by animateColorAsState(
                                        if (popupState.isFullWord) colorScheme.selectedSearchModeColor
                                        else Color.Transparent
                                    )
                                    SmallIconButton(
                                        onClick = {
                                            onPopupStateChange(
                                                popupState.copy(
                                                    isFullWord = !popupState.isFullWord,
                                                    isRegex = false
                                                )
                                            )
                                            scrollToSelected()
                                            sourceCodeFocusRequester.requestFocus()
                                        },
                                        modifier = Modifier.background(fullWordColor, CircleShape),
                                        size = settings.smallIconButtonDiameter,
                                    ) {
                                        val fontSize = when (val size = settings.smallIconDiameter) {
                                            null -> TextUnit.Unspecified
                                            else -> LocalDensity.current.run { size.toSp() }
                                        }
                                        Text(
                                            text = "W",
                                            fontWeight = FontWeight.W600,
                                            fontSize = fontSize,
                                            color = colorScheme.textColor,
                                            modifier = Modifier.semantics {
                                                contentDescription = settings.fullWordButtonContentDescription
                                            },
                                        )
                                    }

                                    Spacer(Modifier.width(settings.spacerBetweenSearchModeIconButtons))

                                    val regexColor by animateColorAsState(
                                        if (popupState.isRegex) colorScheme.selectedSearchModeColor
                                        else Color.Transparent
                                    )
                                    SmallIconButton(
                                        onClick = {
                                            onPopupStateChange(
                                                popupState.copy(
                                                    isRegex = !popupState.isRegex,
                                                    isFullWord = false,
                                                )
                                            )
                                            scrollToSelected()
                                            sourceCodeFocusRequester.requestFocus()
                                        },
                                        modifier = Modifier.background(regexColor, CircleShape),
                                        size = settings.smallIconButtonDiameter,
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.regular_expression),
                                            contentDescription = settings.regexButtonContentDescription,
                                            tint = colorScheme.textColor,
                                            modifier = Modifier.size(settings.smallIconDiameter),
                                        )
                                    }
                                }
                            },
                        )
                        AnimatedVisibility(popupState.showReplace) {
                            HorizontalDivider(
                                thickness = settings.findReplaceFieldsSeparatorThickness,
                                color = colorScheme.findReplaceFieldsSeparatorColor,
                            )
                        }
                        AnimatedVisibility(popupState.showReplace) {
                            BasicTextField(
                                value = popupState.replaceString,
                                onValueChange = { onPopupStateChange(popupState.copy(replaceString = it)) },
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.None,
                                    autoCorrect = false,
                                    keyboardType = KeyboardType.Ascii,
                                    imeAction = ImeAction.Go,
                                ),
                                cursorBrush = SolidColor(colorScheme.cursorColor),
                                textStyle = settings.textFieldTextStyle.merge(color = replaceColor),
                                modifier = Modifier
                                    .focusRequester(replaceFocusRequester)
                                    .fillMaxWidth()
                                    .exitOnEscape()
                                    .onPreviewKeyEvent { onKeyEvents(it) },
                                decorationBox = {
                                    Box(
                                        modifier = Modifier.heightIn(
                                            min = settings.rowMinHeight,
                                            max = settings.rowMaxHeight,
                                        ),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (popupState.replaceString.isEmpty()) {
                                            Text(
                                                text = settings.replaceTextFieldButtonText,
                                                color = colorScheme.placeHolderTextColor,
                                                style = settings.labelTextStyle,
                                            )
                                        }
                                        it()
                                    }
                                },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(settings.spaceBetweenTextFieldsAndControlBlock))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.animateContentSize()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.height(settings.rowMinHeight),
                        ) {
                            AnimatedVisibility(!useCompactMode) {
                                FindResults(
                                    foundMatches = foundMatches,
                                    isError = isFindRegexError,
                                    errorMessage = lastErrorMessage,
                                    onPatternError = onPatternError,
                                    onFoundMatches = onFoundMatches,
                                    onScrollToSelected = scrollToSelected,
                                    sourceCodeFocusRequester = sourceCodeFocusRequester,
                                    colorScheme = colorScheme,
                                    settings = settings,
                                )
                            }
                            SmallIconButton(
                                onClick = {
                                    onClose()
                                    sourceCodeFocusRequester.requestFocus()
                                },
                                size = settings.smallIconButtonDiameter,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = settings.closeButtonContentDescription,
                                    tint = colorScheme.textColor,
                                    modifier = Modifier.size(settings.smallIconDiameter),
                                )
                            }
                        }
                        AnimatedVisibility(
                            visible = popupState.showReplace && !useCompactMode,
                            modifier = Modifier.height(settings.rowMinHeight)
                        ) {
                            ReplaceButtons(
                                codeTextFieldState = codeTextFieldState,
                                popupState = popupState,
                                sourceCodeFocusRequester = sourceCodeFocusRequester,
                                index = foundMatches?.index,
                                onPatternError = onPatternError,
                                onReplaced = onReplaced,
                                colorScheme = colorScheme.replaceButtonsColorScheme,
                                settings = settings,
                            )
                        }
                    }
                }
                AnimatedVisibility(useCompactMode, modifier = Modifier.height(settings.rowMinHeight)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.weight(1f))
                        FindResults(
                            foundMatches = foundMatches,
                            isError = isFindRegexError,
                            errorMessage = lastErrorMessage,
                            onPatternError = onPatternError,
                            onFoundMatches = onFoundMatches,
                            onScrollToSelected = scrollToSelected,
                            sourceCodeFocusRequester = sourceCodeFocusRequester,
                            colorScheme = colorScheme,
                            settings = settings,
                        )
                        AnimatedVisibility(
                            visible = useCompactMode && !useExtremeCompactMode && popupState.showReplace,
                            modifier = Modifier.weight(settings.spaceBetweenFindResultsAndReplaceButtonsRatio),
                        ) {}
                        AnimatedVisibility(useCompactMode && !useExtremeCompactMode && popupState.showReplace) {
                            ReplaceButtons(
                                codeTextFieldState = codeTextFieldState,
                                popupState = popupState,
                                sourceCodeFocusRequester = sourceCodeFocusRequester,
                                index = foundMatches?.index,
                                onPatternError = onPatternError,
                                onReplaced = onReplaced,
                                colorScheme = colorScheme.replaceButtonsColorScheme,
                                settings = settings,
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }
                }
                AnimatedVisibility(
                    visible = useExtremeCompactMode && popupState.showReplace,
                    modifier = Modifier.height(settings.rowMinHeight)
                ) {
                    ReplaceButtons(
                        codeTextFieldState = codeTextFieldState,
                        popupState = popupState,
                        sourceCodeFocusRequester = sourceCodeFocusRequester,
                        index = foundMatches?.index,
                        onPatternError = onPatternError,
                        onReplaced = onReplaced,
                        colorScheme = colorScheme.replaceButtonsColorScheme,
                        settings = settings,
                    )
                }
            }
        }
        HorizontalDivider(
            thickness = settings.findReplaceSeparatorThickness,
            color = colorScheme.findReplaceSeparatorColor,
        )
    }
}

private suspend fun makeRegexSafely(popupState: FindAndReplaceState) =
    withContext(Dispatchers.IO) {
        runCatching {
            val string = when {
                popupState.isRegex -> popupState.findString
                popupState.isFullWord -> "\\b${Regex.escape(popupState.findString)}\\b"
                else -> Regex.escape(popupState.findString)
            }
            if (popupState.matchCase) Regex(string)
            else Regex(string, RegexOption.IGNORE_CASE)
        }
    }

@Composable
private fun ReplaceButtons(
    codeTextFieldState: BasicSourceCodeTextFieldState<*>,
    popupState: FindAndReplaceState,
    sourceCodeFocusRequester: FocusRequester,
    index: Int?,
    onPatternError: (String) -> Unit,
    onReplaced: (TextFieldValue) -> Unit,
    colorScheme: ReplaceButtonsColorScheme,
    settings: FindAndReplaceSettings,
) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(settings.replaceButtonsMargin),
    ) {
        Button(
            onClick = {
                if (index != null) {
                    coroutineScope.launch {
                        val newState = makeReplacement(
                            codeTextFieldState = codeTextFieldState,
                            popupState = popupState,
                            index = index,
                            onPatternError = onPatternError,
                            settings = settings,
                        )
                        newState?.let(onReplaced)
                    }
                }
                sourceCodeFocusRequester.requestFocus()
            },
            shape = settings.replaceButtonShape,
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.backgroundColor),
        ) {
            Text(
                text = settings.replaceButtonText,
                color = colorScheme.textColor,
                style = settings.labelTextStyle,
            )
        }
        Spacer(Modifier.width(settings.spaceBetweenReplaceAndReplaceAllButtons))
        Button(
            onClick = {
                coroutineScope.launch {
                    val newState = makeReplacement(
                        codeTextFieldState = codeTextFieldState,
                        popupState = popupState,
                        index = null,
                        onPatternError = onPatternError,
                        settings = settings,
                    )
                    newState?.let(onReplaced)
                }
                sourceCodeFocusRequester.requestFocus()
            },
            shape = settings.replaceAllButtonShape,
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.backgroundColor),
        ) {
            Text(
                text = settings.replaceAllButtonText,
                color = colorScheme.textColor,
                style = settings.labelTextStyle,
            )
        }
    }
}

private suspend fun makeReplacement(
    codeTextFieldState: BasicSourceCodeTextFieldState<*>,
    popupState: FindAndReplaceState,
    settings: FindAndReplaceSettings,
    index: Int?,
    onPatternError: (String) -> Unit,
): TextFieldValue? {
    val regex = makeRegexSafely(popupState).getOrNull() ?: return null
    var uniqueString: String
    val originalReplaceString =
        if (popupState.isRegex) popupState.replaceString else Regex.escapeReplacement(popupState.replaceString)
    val pureReplaced = try {
        regex.replace(codeTextFieldState.text, originalReplaceString)
    } catch (e: Throwable) {
        onPatternError(e.message ?: settings.defaultRegexErrorMessage)
        return null
    }
    do {
        uniqueString = CharArray(20) { ('a'..'z').random() }.concatToString()
    } while (uniqueString in codeTextFieldState.text || uniqueString in pureReplaced)
    val startTag = "<$uniqueString>"
    val endTag = "</$uniqueString>"
    val rawReplaced =
        regex.replace(codeTextFieldState.text, "$startTag$originalReplaceString$endTag")
    val allReplacements = Regex("$startTag(([^a]|a)*?)$endTag").findAll(rawReplaced)
        .map { it.groups[1]!!.value }.toList()
    val allMatches = regex.findAll(codeTextFieldState.text).toList()
    if (allMatches.size != allReplacements.size) error("Incorrect replacement for: $popupState:\n${codeTextFieldState.text}")
    if (allMatches.isEmpty() || index != null && index !in allMatches.indices) return null
    val (matches, replacements) =
        if (index == null) allMatches to allReplacements
        else listOf(allMatches[index]) to listOf(allReplacements[index])

    val matchesAndReplacements = matches zip replacements
    val oldSelection = codeTextFieldState.selection
    var newSelectionStart = oldSelection.start
    var newSelectionEnd = oldSelection.end
    val oldComposition = codeTextFieldState.composition
    var newCompositionStartAddition = 0
    var newCompositionEndAddition = 0

    fun remap(oldIndex: Int, oldLength: Int, newLength: Int) {
        val diff = newLength - oldLength
        if (diff == 0) return

        if (oldSelection.start >= oldIndex + oldLength)
            newSelectionStart += diff
        else if (oldSelection.start > oldIndex + newLength)
            newSelectionStart -= oldSelection.start - oldIndex - newLength

        if (oldSelection.end >= oldIndex + oldLength)
            newSelectionEnd += diff
        else if (oldSelection.end > oldIndex + newLength)
            newSelectionEnd -= oldSelection.end - oldIndex - newLength

        if (oldComposition != null) {
            if (oldComposition.start >= oldIndex + oldLength)
                newCompositionStartAddition += diff
            else if (oldComposition.start > oldIndex + newLength)
                newCompositionStartAddition -= oldComposition.start - oldIndex - newLength

            if (oldComposition.end >= oldIndex + oldLength)
                newCompositionEndAddition += diff
            else if (oldComposition.end > oldIndex + newLength)
                newCompositionEndAddition -= oldComposition.end - oldIndex - newLength
        }
    }

    val newText = buildAnnotatedString {
        fun safeAppend(startIndex: Int, endIndex: Int) {
            if (startIndex < codeTextFieldState.text.length && startIndex <= endIndex) {
                append(
                    text = codeTextFieldState.annotatedString,
                    start = startIndex,
                    end = minOf(endIndex, codeTextFieldState.text.length)
                )
            }
        }
        safeAppend(0, matches.first().range.first)
        for (i in matchesAndReplacements.indices) {
            val (match, replacement) = matchesAndReplacements[i]
            append(replacement)
            remap(match.range.first, match.range.last - match.range.first + 1, replacement.length)
            safeAppend(
                startIndex = match.range.last + 1,
                endIndex = matches.getOrNull(i + 1)?.range?.first ?: codeTextFieldState.text.length,
            )
        }
    }
    return TextFieldValue(
        annotatedString = newText,
        selection = TextRange(newSelectionStart, newSelectionEnd),
        composition = oldComposition?.let {
            TextRange(it.start + newCompositionStartAddition, it.end + newCompositionEndAddition)
        },
    )
}

@Composable
private fun FindResults(
    foundMatches: FoundMatches?,
    isError: Boolean,
    errorMessage: String,
    onPatternError: (String) -> Unit,
    onFoundMatches: (FoundMatches) -> Unit,
    onScrollToSelected: () -> Unit,
    sourceCodeFocusRequester: FocusRequester,
    colorScheme: FindAndReplaceColorScheme,
    settings: FindAndReplaceSettings,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val resultsTextColor by animateColorAsState(
            if (isError) colorScheme.errorColor else colorScheme.textColor
        )
        val textMeasurer = rememberTextMeasurer()
        val density = LocalDensity.current
        Row(Modifier.padding(settings.findResultsHorizontalPadding)) {
            if (!isError && foundMatches != null) {
                val width = (1..foundMatches.matches.size)
                    .maxOf { textMeasurer.measure(it.toString()).size.width }
                Text(
                    text = "${foundMatches.index + 1}",
                    modifier = Modifier
                        .sizeIn(minWidth = density.run { width.toDp() }),
                    color = resultsTextColor,
                    textAlign = TextAlign.End,
                    style = settings.labelTextStyle,
                )
            }
            val text = when {
                isError -> settings.badPatternMessage
                foundMatches == null -> settings.notFoundMessage
                else -> "/${foundMatches.matches.size}"
            }
            Text(
                text = text,
                modifier = Modifier
                    .run {
                        if (isError) {
                            clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { onPatternError(errorMessage) }
                            )
                        } else {
                            this
                        }
                    },
                color = resultsTextColor,
                textAlign = TextAlign.Start,
                style = settings.labelTextStyle,
            )
        }
        SmallIconButton(
            onClick = {
                val index =
                    if (foundMatches!!.index > 0) foundMatches.index - 1 else foundMatches.matches.lastIndex
                onFoundMatches(foundMatches.copy(index = index))
                onScrollToSelected()
                sourceCodeFocusRequester.requestFocus()
            },
            size = settings.smallIconButtonDiameter,
            enabled = foundMatches != null,
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = settings.previousFoundButtonContentDescription,
                tint = colorScheme.textColor,
                modifier = Modifier.size(settings.smallIconDiameter),
            )
        }
        SmallIconButton(
            onClick = {
                val newIndex =
                    if (foundMatches!!.index < foundMatches.matches.lastIndex) foundMatches.index + 1 else 0
                onFoundMatches(foundMatches.copy(index = newIndex))
                onScrollToSelected()
                sourceCodeFocusRequester.requestFocus()
            },
            size = settings.smallIconButtonDiameter,
            enabled = foundMatches != null,
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = settings.nextFoundButtonContentDescription,
                tint = colorScheme.textColor,
                modifier = Modifier.size(settings.smallIconDiameter),
            )
        }
    }
}

private val defaultfFindKeyEventFilter: KeyEventFilter =
    KeyEventFilterHolder(key = Key.F, isCtrlPressed = true).toKeyEventFilter()

private val defaultfReplaceKeyEventFilter: KeyEventFilter =
    KeyEventFilterHolder(key = Key.R, isCtrlPressed = true).toKeyEventFilter()

internal fun findAndReplace(
    state: BasicSourceCodeTextFieldState<KotlinComposeToken>,
    currentPopupState: FindAndReplaceState,
    findKeyEventFilter: KeyEventFilter = defaultfFindKeyEventFilter,
    replaceKeyEventFilter: KeyEventFilter = defaultfReplaceKeyEventFilter,
    onShowPopup: (FindAndReplaceState) -> Unit,
): KeyEventHandler = f@{ keyEvent ->
    val isFind = findKeyEventFilter(keyEvent)
    val isReplace = replaceKeyEventFilter(keyEvent)
    if (isFind == isReplace) return@f null
    val newFindString =
        if (state.selection.collapsed) currentPopupState.findString
        else state.text.substring(state.selection.min, state.selection.max)
    onShowPopup(currentPopupState.copy(findString = newFindString, showReplace = isReplace))
    null
}

internal fun findAndReplaceSwitcher(
    currentPopupState: FindAndReplaceState,
    onShowPopup: (FindAndReplaceState) -> Unit,
    findKeyEventFilter: KeyEventFilter = defaultfFindKeyEventFilter,
    replaceKeyEventFilter: KeyEventFilter = defaultfReplaceKeyEventFilter,
): (KeyEvent) -> Boolean = f@{ keyEvent ->
    val isFind = findKeyEventFilter(keyEvent)
    val isReplace = replaceKeyEventFilter(keyEvent)
    if (isFind == isReplace) return@f false
    onShowPopup(currentPopupState.copy(showReplace = isReplace))
    true
}

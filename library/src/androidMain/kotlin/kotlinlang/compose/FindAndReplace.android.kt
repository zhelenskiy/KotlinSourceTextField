package kotlinlang.compose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val regexDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO
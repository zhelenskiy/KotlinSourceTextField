package kotlinlang.compose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual val regexDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

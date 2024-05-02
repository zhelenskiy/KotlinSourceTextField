package kotlinlang.utils

import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs

internal actual val isApple: Boolean = hostOs == OS.Ios || hostOs == OS.MacOS

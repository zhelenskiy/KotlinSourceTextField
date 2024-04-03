package kotlinlang.utils

internal actual val isApple: Boolean = when (System.getProperty("os.name")) {
    "Mac OS X" -> true
    else -> false
}

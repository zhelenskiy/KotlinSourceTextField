package com.zhelenskiy.kotlinsourcetextfieldsample

import App
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    
        setContent {
            App { isDark, light, dark ->
                val style = if (isDark) SystemBarStyle.dark(dark.toArgb()) else SystemBarStyle.light(light.toArgb(), dark.toArgb())
                enableEdgeToEdge(style, style)
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

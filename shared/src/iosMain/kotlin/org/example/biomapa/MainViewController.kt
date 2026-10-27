package org.example.biomapa

import androidx.compose.ui.window.ComposeUIViewController
import org.example.biomapa.di.iniciarKoin
import org.example.biomapa.presentation.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    iniciarKoin()
    return ComposeUIViewController { App() }
}

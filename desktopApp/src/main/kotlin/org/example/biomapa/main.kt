package org.example.biomapa

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.biomapa.di.iniciarKoin
import org.example.biomapa.presentation.App

fun main() {
    iniciarKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "BioMapa",
        ) {
            App()
        }
    }
}
package org.example.biomapa

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
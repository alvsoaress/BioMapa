package org.example.biomapa.domain.validacao

fun isEmailValid(email: String): Boolean = email.isNotBlank() && email.contains("@") && email.contains(".")

fun isPasswordValid(password: String): Boolean = password.length >= 6

@file:Suppress("SpellCheckingInspection")

package org.example.biomapa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import supabase

fun isEmailValid(email: String): Boolean {
    return email.isNotBlank() && email.contains("@") && email.contains(".")
}

fun isPasswordValid(password: String): Boolean {
    return password.length >= 6
}

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var nomeText by remember { mutableStateOf("") }
    var sobrenomeText by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    val emailError = emailText.isNotBlank() && !isEmailValid(emailText)
    val passwordError = passwordText.isNotBlank() && !isPasswordValid(passwordText)

    val isFormValid = if (isLoginMode) {
        isEmailValid(emailText) && isPasswordValid(passwordText)
    } else {
        nomeText.isNotBlank() && sobrenomeText.isNotBlank() && isEmailValid(emailText) && isPasswordValid(passwordText)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Logo",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "BioMapa",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (isLoginMode) "Que bom ter você de volta!" else "Junte-se à nossa comunidade",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                if (!isLoginMode) {
                    BioMapaTextField(
                        value = nomeText,
                        onValueChange = { nomeText = it },
                        label = "Nome",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    BioMapaTextField(
                        value = sobrenomeText,
                        onValueChange = { sobrenomeText = it },
                        label = "Sobrenome",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                BioMapaTextField(
                    value = emailText,
                    onValueChange = { emailText = it },
                    label = "E-mail acadêmico ou pessoal",
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError
                )
                if (emailError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Insira um e-mail válido (ex: teste@email.com)",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                BioMapaTextField(
                    value = passwordText,
                    onValueChange = { passwordText = it },
                    label = "Senha (mínimo 6 caracteres)",
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Senha") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = passwordError
                )
                if (passwordError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "A senha deve ter pelo menos 6 caracteres",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            try {
                                if (isLoginMode) {
                                    supabase.auth.signInWith(Email) {
                                        email = emailText
                                        password = passwordText
                                    }
                                } else {
                                    supabase.auth.signUpWith(Email) {
                                        email = emailText
                                        password = passwordText
                                        data = buildJsonObject {
                                            put("nome", nomeText)
                                            put("sobrenome", sobrenomeText)
                                        }
                                    }
                                }
                                onAuthSuccess()
                            } catch (e: Exception) {
                                errorMessage = "Falha: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading && isFormValid
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            if (isLoginMode) "Entrar no BioMapa" else "Cadastrar",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            isLoginMode = !isLoginMode
            errorMessage = null
        }) {
            Text(if (isLoginMode) "Ainda não tem conta? Crie uma agora" else "Já faz parte? Faça login")
        }
    }
}

@Preview
@Composable
fun AuthScreenPreview() {
    BioMapaTheme {
        Surface {
            AuthScreen(onAuthSuccess = {})
        }
    }
}
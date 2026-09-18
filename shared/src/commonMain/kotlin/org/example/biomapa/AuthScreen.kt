package org.example.biomapa

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import supabase

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var nomeText by remember { mutableStateOf("") }
    var sobrenomeText by remember { mutableStateOf("") }
    var sexoText by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLoginMode) "Entrar no BioMapa" else "Criar Conta",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isLoginMode) {
            OutlinedTextField(
                value = nomeText,
                onValueChange = { nomeText = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = sobrenomeText,
                onValueChange = { sobrenomeText = it },
                label = { Text("Sobrenome") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = sexoText,
                onValueChange = { sexoText = it },
                label = { Text("Sexo (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Campos sempre exibidos (Login e Cadastro)
        OutlinedTextField(
            value = emailText,
            onValueChange = { emailText = it },
            label = { Text("E-mail (ex: aluno@ufrn.edu.br)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

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
                                    put("sexo", sexoText)
                                }
                            }
                        }
                        onAuthSuccess()
                    } catch (e: Exception) {
                        errorMessage = "Falha na operação: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(if (isLoginMode) "Entrar" else "Cadastrar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            isLoginMode = !isLoginMode
            errorMessage = null
        }) {
            Text(if (isLoginMode) "Novo por aqui? Crie sua conta" else "Já tem conta? Faça login")
        }
    }
}
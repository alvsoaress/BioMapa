@file:Suppress("SpellCheckingInspection")

package org.example.biomapa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import supabase

@Composable
fun CreatePlantScreen(onPlantCreated: () -> Unit, onBack: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var localizacao by remember { mutableStateOf("") }
    var propriedades by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Nova Descoberta \uD83C\uDF3F",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Preencha os dados da planta que você encontrou.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                BioMapaTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = "Nome da Planta (Popular ou Científico)"
                )

                Spacer(modifier = Modifier.height(16.dp))

                BioMapaTextField(
                    value = localizacao,
                    onValueChange = { localizacao = it },
                    label = "Localização (Ex: Perto da Biblioteca)",
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Localização") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                BioMapaTextField(
                    value = propriedades,
                    onValueChange = { propriedades = it },
                    label = "Propriedades Médicas e Usos",
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Informação") },
                    singleLine = false,
                    minLines = 4,
                    maxLines = 6
                )
            }
        }

        if (message != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message!!,
                color = if (message!!.startsWith("Erro")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                enabled = !isLoading,
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        message = null
                        try {
                            val currentUser = supabase.auth.currentUserOrNull()
                            if (currentUser == null) {
                                message = "Erro: Usuário não autenticado."
                                return@launch
                            }

                            val novaPlanta = Planta(
                                userId = currentUser.id,
                                nome = nome,
                                localizacao = localizacao,
                                propriedadesMedicas = propriedades
                            )

                            supabase.postgrest["plantas"].insert(novaPlanta)
                            message = "Planta cadastrada com sucesso!"
                            onPlantCreated()
                        } catch (e: Exception) {
                            message = "Erro ao salvar: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.weight(1f).height(50.dp),
                enabled = !isLoading && nome.isNotBlank() && localizacao.isNotBlank() && propriedades.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Salvar")
                }
            }
        }
    }
}

@Preview
@Composable
fun CreatePlantScreenPreview() {
    BioMapaTheme {
        Surface {
            CreatePlantScreen(onPlantCreated = {}, onBack = {})
        }
    }
}
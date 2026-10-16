package br.ufrn.biomapa.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.ufrn.biomapa.domain.Planta

// Função PURA de validação (fácil de testar na Sprint 2)
fun isFormularioValido(
    nome: String,
    descricao: String,
    latitude: String,
    longitude: String
): Boolean {
    return nome.isNotBlank() &&
           descricao.isNotBlank() &&
           latitude.toDoubleOrNull() != null &&
           longitude.toDoubleOrNull() != null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastro(
    // Estado vindo de fora
    nome: String,
    descricao: String,
    propriedades: String,
    latitude: String,
    longitude: String,
    // Eventos
    onNomeChange: (String) -> Unit,
    onDescricaoChange: (String) -> Unit,
    onPropriedadesChange: (String) -> Unit,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onSalvar: () -> Unit,
    onVoltar: () -> Unit
) {
    val isValido = isFormularioValido(nome, descricao, latitude, longitude)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cadastrar Planta") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
		    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
    contentDescription = "Voltar"
)                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nome,
                onValueChange = onNomeChange,
                label = { Text("Nome da planta *") },
                isError = nome.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = descricao,
                onValueChange = onDescricaoChange,
                label = { Text("Descrição *") },
                isError = descricao.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            OutlinedTextField(
                value = propriedades,
                onValueChange = onPropriedadesChange,
                label = { Text("Propriedades medicinais") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            OutlinedTextField(
                value = latitude,
                onValueChange = onLatitudeChange,
                label = { Text("Latitude *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = latitude.toDoubleOrNull() == null,
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = longitude,
                onValueChange = onLongitudeChange,
                label = { Text("Longitude *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = longitude.toDoubleOrNull() == null,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Aqui depois entra o botão de upload de foto
            
            Button(
                onClick = onSalvar,
                enabled = isValido,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar planta")
            }
        }
    }
}

@Preview
@Composable
fun TelaCadastroPreview() {
    MaterialTheme {
        TelaCadastro(
            nome = "Samambaia",
            descricao = "Planta de sombra",
            propriedades = "Calmante",
            latitude = "-5.84",
            longitude = "-35.20",
            onNomeChange = {}, onDescricaoChange = {}, onPropriedadesChange = {},
            onLatitudeChange = {}, onLongitudeChange = {},
            onSalvar = {}, onVoltar = {}
        )
    }
}

@Preview
@Composable
fun TelaCadastroVazioPreview() {
    MaterialTheme {
        TelaCadastro(
            nome = "", descricao = "", propriedades = "",
            latitude = "", longitude = "",
            onNomeChange = {}, onDescricaoChange = {}, onPropriedadesChange = {},
            onLatitudeChange = {}, onLongitudeChange = {},
            onSalvar = {}, onVoltar = {}
        )
    }
}

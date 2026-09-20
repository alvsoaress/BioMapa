@file:Suppress("SpellCheckingInspection")

package org.example.biomapa

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Planta(
    val id: Int? = null,

    @SerialName("user_id")
    val userId: String,

    val nome: String,
    val localizacao: String,

    @SerialName("propriedades_medicas")
    val propriedadesMedicas: String,

    @SerialName("foto_url")
    val fotoUrl: String? = null 
)
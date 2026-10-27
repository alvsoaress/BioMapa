package org.example.biomapa.domain.validacao

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidacaoPlantaTest {
    @Test
    fun formularioCompletoEhValido() {
        val erros = validarPlanta("Boldo", "Perto da Biblioteca", -5.84, -35.2, "Digestivo")

        assertTrue(erros.valido)
        assertEquals(ErrosPlanta(), erros)
    }

    @Test
    fun formularioVazioTemErroEmTodosOsCampos() {
        val erros = validarPlanta("", "", null, null, "")

        assertFalse(erros.valido)
        assertNotNull(erros.nome)
        assertNotNull(erros.localizacao)
        assertNotNull(erros.coordenadas)
        assertNotNull(erros.propriedades)
    }

    @Test
    fun textoSoComEspacosContaComoVazio() {
        val erros = validarPlanta("   ", "Perto da Biblioteca", -5.84, -35.2, "Digestivo")

        assertFalse(erros.valido)
        assertNotNull(erros.nome)
        assertNull(erros.localizacao)
    }

    @Test
    fun faltarSoUmaCoordenadaEhErro() {
        val erros = validarPlanta("Boldo", "Perto da Biblioteca", -5.84, null, "Digestivo")

        assertFalse(erros.valido)
        assertNotNull(erros.coordenadas)
    }
}

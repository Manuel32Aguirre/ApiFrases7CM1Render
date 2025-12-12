package com.ipn.mx.frases7cm1;

import com.ipn.mx.frases7cm1.frase.aplicacion.FraseServicio;
import com.ipn.mx.frases7cm1.frase.dominio.entidades.Frase;
import com.ipn.mx.frases7cm1.frase.dominio.repositorio.FraseRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class Frases7Cm1ApplicationTests {

    @Autowired
    private FraseServicio fraseServicio;

    @Autowired
    private FraseRepositorio fraseRepositorio;

    private Frase fraseEjemplo;

    @BeforeEach
    void setUp() {
        // Limpia la base de datos y crea una frase de prueba
        fraseRepositorio.deleteAll();
        fraseEjemplo = Frase.builder()
                .autor("Albert Einstein")
                .textoFrase("La imaginación es más importante que el conocimiento")
                .build();
    }

    @Test
    @DisplayName("El contexto de Spring debe cargar correctamente")
    void contextLoads() {
        assertNotNull(fraseServicio);
        assertNotNull(fraseRepositorio);
    }

    @Test
    @DisplayName("Debe guardar una frase correctamente")
    void debeGuardarFraseCorrectamente() {
        // When - Cuando guardamos la frase
        Frase fraseGuardada = fraseServicio.guardarFrase(fraseEjemplo);

        // Then - Entonces debe tener ID y los datos correctos
        assertNotNull(fraseGuardada.getIdFrase());
        assertEquals("Albert Einstein", fraseGuardada.getAutor());
        assertEquals("La imaginación es más importante que el conocimiento", fraseGuardada.getTextoFrase());
        assertNotNull(fraseGuardada.getFechaCreacion());
    }

    @Test
    @DisplayName("Debe obtener una frase por ID")
    void debeObtenerFrasePorId() {
        // Given - Dada una frase guardada
        Frase guardada = fraseServicio.guardarFrase(fraseEjemplo);

        // When - Cuando la buscamos por ID
        Frase encontrada = fraseServicio.obtenerFrasePorId(guardada.getIdFrase());

        // Then - Entonces debe encontrarla
        assertNotNull(encontrada);
        assertEquals(guardada.getIdFrase(), encontrada.getIdFrase());
        assertEquals("Albert Einstein", encontrada.getAutor());
    }

    @Test
    @DisplayName("Debe retornar null cuando el ID no existe")
    void debeRetornarNullCuandoIdNoExiste() {
        // When - Cuando buscamos un ID que no existe
        Frase noEncontrada = fraseServicio.obtenerFrasePorId(999L);

        // Then - Entonces debe retornar null
        assertNull(noEncontrada);
    }

    @Test
    @DisplayName("Debe obtener todas las frases")
    void debeObtenerTodasLasFrases() {
        // Given - Dadas varias frases guardadas
        fraseServicio.guardarFrase(fraseEjemplo);
        Frase frase2 = Frase.builder()
                .autor("Steve Jobs")
                .textoFrase("La innovación distingue a los líderes de los seguidores")
                .build();
        fraseServicio.guardarFrase(frase2);

        // When - Cuando obtenemos todas
        List<Frase> frases = fraseServicio.obtenerFrases();

        // Then - Entonces debe haber 2
        assertEquals(2, frases.size());
    }

    @Test
    @DisplayName("Debe obtener una frase aleatoria")
    void debeObtenerFraseAleatoria() {
        // Given - Dada al menos una frase
        fraseServicio.guardarFrase(fraseEjemplo);

        // When - Cuando pedimos una frase aleatoria
        Frase aleatoria = fraseServicio.obtenerFraseAleatoria();

        // Then - Entonces debe retornar una frase válida
        assertNotNull(aleatoria);
        assertNotNull(aleatoria.getIdFrase());
        assertNotNull(aleatoria.getTextoFrase());
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener frase aleatoria sin datos")
    void debeLanzarExcepcionAlObtenerFraseAleatoriaVacia() {
        // Given - Sin frases en la base de datos (ya limpiada en setUp)

        // When/Then - Cuando intentamos obtener aleatoria, debe lanzar excepción
        assertThrows(IndexOutOfBoundsException.class, () -> {
            fraseServicio.obtenerFraseAleatoria();
        });
    }

    @Test
    @DisplayName("Debe eliminar una frase correctamente")
    void debeEliminarFraseCorrectamente() {
        // Given - Dada una frase guardada
        Frase guardada = fraseServicio.guardarFrase(fraseEjemplo);
        Long id = guardada.getIdFrase();

        // When - Cuando la eliminamos
        fraseServicio.eliminarFrase(id);

        // Then - Entonces no debe existir
        Frase eliminada = fraseServicio.obtenerFrasePorId(id);
        assertNull(eliminada);
    }

    @Test
    @DisplayName("Debe actualizar una frase manteniendo la fecha de creación")
    void debeActualizarFraseManteniendoFechaCreacion() {
        // Given - Dada una frase guardada
        Frase guardada = fraseServicio.guardarFrase(fraseEjemplo);
        Long id = guardada.getIdFrase();
        var fechaOriginal = guardada.getFechaCreacion();

        // When - Cuando la actualizamos
        Frase paraActualizar = fraseServicio.obtenerFrasePorId(id);
        paraActualizar.setAutor("Einstein (actualizado)");
        paraActualizar.setTextoFrase("Nueva frase actualizada para la prueba");
        Frase actualizada = fraseServicio.guardarFrase(paraActualizar);

        // Then - Entonces debe tener los nuevos datos pero misma fecha
        assertEquals("Einstein (actualizado)", actualizada.getAutor());
        assertEquals("Nueva frase actualizada para la prueba", actualizada.getTextoFrase());
        assertEquals(fechaOriginal, actualizada.getFechaCreacion());
    }

    @Test
    @DisplayName("Debe generar reporte PDF sin errores")
    void debeGenerarReportePdfSinErrores() {
        // Given - Dadas algunas frases
        fraseServicio.guardarFrase(fraseEjemplo);
        List<Frase> frases = fraseServicio.obtenerFrases();

        // When - Cuando generamos el PDF
        var pdfStream = fraseServicio.reporteDeFrasesPdf(frases);

        // Then - Entonces debe generar contenido
        assertNotNull(pdfStream);
        assertTrue(pdfStream.available() > 0);
    }

}

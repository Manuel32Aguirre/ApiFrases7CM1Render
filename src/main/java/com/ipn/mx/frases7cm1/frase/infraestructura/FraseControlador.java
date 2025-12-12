package com.ipn.mx.frases7cm1.frase.infraestructura;

import com.ipn.mx.frases7cm1.frase.aplicacion.FraseServicio;
import com.ipn.mx.frases7cm1.frase.dominio.entidades.Frase;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/frases")
public class FraseControlador {
    @Autowired
    private FraseServicio servicio;

    @GetMapping("/frase/aleatorio")
    @ResponseStatus(HttpStatus.OK)
    public String obtenerFraseAleatoria() {
        return servicio.obtenerFraseAleatoria().getTextoFrase();
    }

    @GetMapping("/frase")
    @ResponseStatus(HttpStatus.OK)
    public List<Frase> obtenerFrases() {
        return servicio.obtenerFrases();
    }

    @GetMapping("/frase/{id}")
    public ResponseEntity<Frase> obtenerFrasePorId(@PathVariable Long id) {
        Frase frase = servicio.obtenerFrasePorId(id);
        if (frase == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(frase);
    }

    @PostMapping("frase")
    @ResponseStatus(HttpStatus.CREATED)
    public Frase guardarFrase(@Valid @RequestBody Frase frase) {
        return servicio.guardarFrase(frase);
    }

    @DeleteMapping("/frase/{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Long id) {
        Frase frase = servicio.obtenerFrasePorId(id);
        if (frase == null) {
            return ResponseEntity.notFound().build();
        }
        servicio.eliminarFrase(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/frase/{id}")
    public ResponseEntity<Frase> actualizarFrase(@Valid @RequestBody Frase frase, @PathVariable Long id) {
        Frase f = servicio.obtenerFrasePorId(id);
        if (f == null) {
            return ResponseEntity.notFound().build();
        }
        f.setTextoFrase(frase.getTextoFrase());
        f.setAutor(frase.getAutor());
        return ResponseEntity.ok(servicio.guardarFrase(f));
    }

    @GetMapping("/frase/reporte/pdf")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<InputStreamResource> generarReporteDeFrasesPdf() {
        List<Frase> listaDeFrases = servicio.obtenerFrases();
        ByteArrayInputStream bis = servicio.reporteDeFrasesPdf((listaDeFrases));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reporte_de_frases.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

}
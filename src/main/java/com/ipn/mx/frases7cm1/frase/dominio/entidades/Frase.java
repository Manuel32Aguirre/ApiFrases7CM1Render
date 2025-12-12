package com.ipn.mx.frases7cm1.frase.dominio.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Builder
@Entity

@Table(name = "Frase")
public class Frase implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long idFrase;

    @NotBlank(message = "El autor no puede estar vacío")
    @Size(min = 2, max = 100, message = "El autor debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String autor;

    @NotBlank(message = "El texto de la frase no puede estar vacío")
    @Size(min = 10, max = 500, message = "La frase debe tener entre 10 y 500 caracteres")
    @Column(nullable = false, length = 500)
    private String textoFrase;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date fechaCreacion;
}

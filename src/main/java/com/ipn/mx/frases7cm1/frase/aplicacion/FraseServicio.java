package com.ipn.mx.frases7cm1.frase.aplicacion;

import com.ipn.mx.frases7cm1.frase.dominio.entidades.Frase;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface FraseServicio {
    public Frase obtenerFraseAleatoria();
    public Frase guardarFrase(Frase frase);
    public List<Frase> obtenerFrases();
    public Frase obtenerFrasePorId(Long id);
    public void eliminarFrase(Long id);
    public ByteArrayInputStream reporteDeFrasesPdf(List<Frase> listaDeFrases);
}

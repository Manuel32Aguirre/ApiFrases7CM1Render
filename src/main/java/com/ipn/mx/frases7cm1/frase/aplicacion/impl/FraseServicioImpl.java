package com.ipn.mx.frases7cm1.frase.aplicacion.impl;

import com.ipn.mx.frases7cm1.frase.aplicacion.FraseServicio;
import com.ipn.mx.frases7cm1.frase.dominio.entidades.Frase;
import com.ipn.mx.frases7cm1.frase.dominio.repositorio.FraseRepositorio;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FraseServicioImpl implements FraseServicio {
    @Autowired
    private FraseRepositorio repository;

    @Override
    public Frase obtenerFraseAleatoria() {
        List<Frase> frases = (List<Frase>) repository.findAll();
        if (frases.isEmpty()) {
            throw new IndexOutOfBoundsException("No hay frases disponibles en el sistema");
        }
        int indiceAleatorio = (int) (Math.random() * frases.size());
        return frases.get(indiceAleatorio);
    }

    @Override
    public Frase guardarFrase(Frase frase) {
        return repository.save(frase);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Frase> obtenerFrases() {
        return (List<Frase>) repository.findAll();
    }

    @Override
    @Transactional
    public Frase obtenerFrasePorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void eliminarFrase(Long id) {
        repository.deleteById(id);
    }
    @Override
    public ByteArrayInputStream reporteDeFrasesPdf(List<Frase> listaDeFrases) {
        Document documento = new Document();
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try{
            PdfWriter.getInstance(documento, salida);
            documento.open();
            Font tipoLetra = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLUE);
            Paragraph parrafo = new Paragraph("Lista de frases", tipoLetra);
            parrafo.setAlignment(Element.ALIGN_CENTER);
            documento.add(parrafo);
            documento.add(Chunk.NEWLINE);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLUE);
            PdfPTable tabla = new PdfPTable(4);
            Stream.of("Id Frase", "Frase", "Autor", "Fecha de Creacion")
                    .forEach(headerTitle -> {
                        PdfPCell encabezadoTabla = new PdfPCell();
                        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
                        encabezadoTabla.setBackgroundColor(BaseColor.CYAN);
                        encabezadoTabla.setHorizontalAlignment(Element.ALIGN_CENTER);
                        encabezadoTabla.setVerticalAlignment(Element.ALIGN_CENTER);
                        encabezadoTabla.setBorderWidth(2);
                        encabezadoTabla.setPhrase((new Phrase(headerTitle,headFont)));
                        tabla.addCell(encabezadoTabla);
                            });
            for(Frase frase: listaDeFrases){
                PdfPCell celdaIdFrase = new PdfPCell(
                        new Phrase(String.valueOf(frase.getIdFrase()),
                                textFont));
                celdaIdFrase.setPaddingLeft(4);
                celdaIdFrase.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaIdFrase.setHorizontalAlignment(Element.ALIGN_LEFT);
                tabla.addCell(celdaIdFrase);

                PdfPCell celdaAutor = new PdfPCell(
                        new Phrase(frase.getAutor(), textFont));
                celdaAutor.setPaddingLeft(4);
                celdaAutor.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaAutor.setHorizontalAlignment(Element.ALIGN_LEFT);
                tabla.addCell(celdaAutor);

                PdfPCell celdaFrase = new PdfPCell(
                        new Phrase(frase.getTextoFrase(), textFont));
                celdaFrase.setPaddingLeft(4);
                celdaFrase.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaFrase.setHorizontalAlignment(Element.ALIGN_LEFT);
                tabla.addCell(celdaFrase);

                PdfPCell celdaFecha = new PdfPCell(
                        new Phrase(String.valueOf(frase.getFechaCreacion()), textFont));
                celdaFecha.setPaddingLeft(4);
                celdaFecha.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaFecha.setHorizontalAlignment(Element.ALIGN_LEFT);
                tabla.addCell(celdaFecha);
            }
            documento.add(tabla);
            documento.close();

        }catch(DocumentException e){
            e.printStackTrace();
        }
        return new ByteArrayInputStream(salida.toByteArray());
    }
}

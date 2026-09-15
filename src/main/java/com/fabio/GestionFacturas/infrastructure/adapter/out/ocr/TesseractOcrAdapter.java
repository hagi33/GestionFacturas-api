package com.fabio.GestionFacturas.infrastructure.adapter.out.ocr;

import com.fabio.GestionFacturas.application.shared.port.out.OcrPort;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
@Profile("ocr")
public class TesseractOcrAdapter implements OcrPort {

    private final ITesseract tesseract;

    public TesseractOcrAdapter(@Value("${app.ocr.tesseract.datapath}") String datapath,
                               @Value("${app.ocr.tesseract.language}") String language) {
        Tesseract instance = new Tesseract();
        instance.setDatapath(datapath);
        instance.setLanguage(language);
        this.tesseract = instance;
    }

    @Override
    public String extraerTexto(byte[] contenido) {
        BufferedImage imagen;
        try {
            imagen = ImageIO.read(new ByteArrayInputStream(contenido));
        } catch (IOException e) {
            throw new OcrException("No se pudo leer el archivo para aplicar OCR", e);
        }

        if (imagen == null) {
            throw new OcrException("Formato de archivo no soportado por OCR todavía (ej. PDF); solo imágenes por ahora");
        }

        try {
            return tesseract.doOCR(imagen);
        } catch (TesseractException e) {
            throw new OcrException("Fallo al ejecutar OCR con Tesseract", e);
        }
    }
}

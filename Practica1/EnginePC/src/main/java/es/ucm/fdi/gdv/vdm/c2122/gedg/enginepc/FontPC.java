package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;

/**
 * Clase que representa fuentes en la plataforma de PC.
 */
public class FontPC implements Font {

    int originalSize_; //Tamaño original de la fuente, NO modificado por el redimensionado de la pantalla
    private java.awt.Font font; //Fuente de Java
    private Color color_;

    public FontPC(String name, Color color, int size, boolean isBold) {
        color_ = color;
        originalSize_ = size;

        //Se busca la fuente en la ruta dada. Si no existe, salta una excepción.
        try {
            font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new File(name)).deriveFont((isBold) ? java.awt.Font.BOLD : java.awt.Font.PLAIN, size);
        }
        catch (IOException | FontFormatException e)
        {
            String workingDir = System.getProperty("user.dir");
            System.out.println("Current working directory : " + workingDir);
            e.printStackTrace();
        }
    }

    /**
     * Devuelve la fuente que usa JFrame para renderizar.
     */
    java.awt.Font getFont(){
        return font;
    }

    /**
     * Fija el tamaño de la fuente con el que se va a escribir el texto.
     */
    @Override
    //Modifica el tamaño de renderizado
    public void setRenderSize(int size) {
        if(font.getSize() == size) return;
        font = font.deriveFont((float) size);
    }

    /**
     * Fija el color de la fuente con el que se va a escribir el texto.
     */
    @Override
    public void setColor(Color color) {
        color_ = color;
    }

    /**
     * Fija el tamaño original de la fuente para poder hacer reset en caso de que haya sido modificado.
     */
    @Override
    public void setSize(int size) {
        originalSize_ = size;
        font = font.deriveFont((float) size);
    }

    /**
     * Fija la negrita de la fuente con el que se va a escribir.
     */
    @Override
    public void setBold(boolean isBold) {
        font = font.deriveFont((isBold) ? java.awt.Font.BOLD : java.awt.Font.PLAIN);
    }

    /**
     * Devuelve el tamaño de dibujado de la fuente.
     */
    @Override
    public int getSize() {
        return font.getSize();
    }

    /**
     * Devuelve el color del a fuente.
     */
    @Override
    public Color getColor() {
        return color_;
    }
}
